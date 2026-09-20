package com.tdcoins.app

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.ZonedDateTime
import java.util.Calendar

enum class ReminderType(
    val label: String,
    val title: String,
    val message: String,
    val destination: AppTab,
) {
    FOCUS(
        "Enfoque",
        "Un momento para enfocarte",
        "Inicia un Pomodoro corto y avanza con un solo paso.",
        AppTab.POMODORO,
    ),
    MISSIONS(
        "Misiones",
        "Revisa tus misiones",
        "Elige una misión pequeña para mantener tu progreso.",
        AppTab.MISSIONS,
    ),
    STREAK(
        "Racha diaria",
        "Mantén viva tu racha",
        "Una acción breve hoy también cuenta. Entra y registra tu avance.",
        AppTab.HOME,
    ),
}

data class ReminderSettings(
    val enabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0,
    val types: Set<ReminderType> = setOf(ReminderType.FOCUS),
    val quietStartHour: Int = 22,
    val quietEndHour: Int = 7,
)

class ReminderPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun load(): ReminderSettings {
        val savedTypes = preferences.getStringSet(KEY_TYPES, null)
            ?.mapNotNull { value -> ReminderType.entries.find { it.name == value } }
            ?.toSet()
            .orEmpty()
        return ReminderSettings(
            enabled = preferences.getBoolean(KEY_ENABLED, false),
            hour = preferences.getInt(KEY_HOUR, 9).coerceIn(0, 23),
            minute = preferences.getInt(KEY_MINUTE, 0).coerceIn(0, 59),
            types = savedTypes.ifEmpty { setOf(ReminderType.FOCUS) },
            quietStartHour = preferences.getInt(KEY_QUIET_START, 22).coerceIn(0, 23),
            quietEndHour = preferences.getInt(KEY_QUIET_END, 7).coerceIn(0, 23),
        )
    }

    fun save(settings: ReminderSettings) {
        preferences.edit()
            .putBoolean(KEY_ENABLED, settings.enabled)
            .putInt(KEY_HOUR, settings.hour)
            .putInt(KEY_MINUTE, settings.minute)
            .putStringSet(KEY_TYPES, settings.types.map { it.name }.toSet())
            .putInt(KEY_QUIET_START, settings.quietStartHour)
            .putInt(KEY_QUIET_END, settings.quietEndHour)
            .apply()
    }

    private companion object {
        const val FILE_NAME = "td_coins_reminders"
        const val KEY_ENABLED = "enabled"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
        const val KEY_TYPES = "types"
        const val KEY_QUIET_START = "quiet_start"
        const val KEY_QUIET_END = "quiet_end"
    }
}

object AppNotifications {
    const val EXTRA_DESTINATION = "notification_destination"
    private const val PROGRESS_CHANNEL_ID = "progress"
    private const val REMINDER_CHANNEL_ID = "daily_reminders"
    private const val ALARM_REQUEST_CODE = 7301

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    PROGRESS_CHANNEL_ID,
                    "Progreso y rachas",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Recompensas, misiones completadas y continuidad de tu racha"
                },
            )
            manager.createNotificationChannel(
                NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Recordatorios diarios",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Avisos configurables para enfoque, misiones y rachas"
                },
            )
        }
    }

    fun updateSchedule(context: Context, settings: ReminderSettings = ReminderPreferences(context).load()) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = reminderPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        if (!settings.enabled || settings.types.isEmpty()) return

        scheduleAt(context, nextReminderMillis(settings))
    }

    fun showProgress(context: Context, title: String, message: String) {
        show(context, PROGRESS_CHANNEL_ID, title, message, AppTab.HOME, System.currentTimeMillis().toInt())
    }

    fun showDailyReminder(context: Context, type: ReminderType) {
        show(context, REMINDER_CHANNEL_ID, type.title, type.message, type.destination, 8100 + type.ordinal)
    }

    fun notificationsAllowed(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    internal fun isQuietHour(hour: Int, start: Int, end: Int): Boolean = when {
        start == end -> false
        start < end -> hour in start until end
        else -> hour >= start || hour < end
    }

    internal fun nextReminderMillis(
        settings: ReminderSettings,
        now: ZonedDateTime = ZonedDateTime.now(),
    ): Long {
        var next = now.withHour(settings.hour).withMinute(settings.minute).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        if (isQuietHour(next.hour, settings.quietStartHour, settings.quietEndHour)) {
            next = next.withHour(settings.quietEndHour).withMinute(0)
            if (!next.isAfter(now)) next = next.plusDays(1)
        }
        return next.toInstant().toEpochMilli()
    }

    internal fun nextQuietEndMillis(
        settings: ReminderSettings,
        now: ZonedDateTime = ZonedDateTime.now(),
    ): Long {
        var quietEnd = now.withHour(settings.quietEndHour).withMinute(0).withSecond(0).withNano(0)
        if (!quietEnd.isAfter(now)) quietEnd = quietEnd.plusDays(1)
        return quietEnd.toInstant().toEpochMilli()
    }

    private fun scheduleAt(context: Context, triggerAtMillis: Long) {
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            reminderPendingIntent(context),
        )
    }

    internal fun deferUntilQuietPeriodEnds(context: Context, settings: ReminderSettings) {
        scheduleAt(context, nextQuietEndMillis(settings))
    }

    private fun show(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        destination: AppTab,
        notificationId: Int,
    ) {
        if (!notificationsAllowed(context)) return
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_DESTINATION, destination.name)
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            destination.ordinal,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
            // El permiso puede revocarse entre la comprobación y esta llamada.
        }
    }

    private fun reminderPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val settings = ReminderPreferences(context).load()
        if (!settings.enabled) return
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (AppNotifications.isQuietHour(currentHour, settings.quietStartHour, settings.quietEndHour)) {
            AppNotifications.deferUntilQuietPeriodEnds(context, settings)
            return
        }
        settings.types.forEach { AppNotifications.showDailyReminder(context, it) }
        AppNotifications.updateSchedule(context, settings)
    }
}

internal fun settingsAfterPermissionResult(
    settings: ReminderSettings,
    granted: Boolean,
    enableAfterGrant: Boolean,
): ReminderSettings =
    if (granted && enableAfterGrant) settings.copy(enabled = true) else settings

class ReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        AppNotifications.createChannels(context)
        AppNotifications.updateSchedule(context)
    }
}