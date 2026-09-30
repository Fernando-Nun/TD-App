package com.tdcoins.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

const val POMODORO_WORK_SECONDS = 25 * 60
const val POMODORO_BREAK_SECONDS = 5 * 60

data class PomodoroTimerState(
    val isWork: Boolean = true,
    val seconds: Int = POMODORO_WORK_SECONDS,
    val running: Boolean = false,
    val deadline: Long = 0L,
)

/**
 * Guarda el temporizador fuera de la composición para que, si el sistema cierra la app
 * en segundo plano, la sesión se complete (y otorgue monedas) al volver a abrirla.
 */
class PomodoroPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun load(): PomodoroTimerState {
        val isWork = preferences.getBoolean(KEY_IS_WORK, true)
        val running = preferences.getBoolean(KEY_RUNNING, false)
        val deadline = preferences.getLong(KEY_DEADLINE, 0L)
        val fullSeconds = if (isWork) POMODORO_WORK_SECONDS else POMODORO_BREAK_SECONDS
        return PomodoroTimerState(
            isWork = isWork,
            seconds = preferences.getInt(KEY_SECONDS, fullSeconds).coerceIn(0, fullSeconds),
            running = running && deadline > 0L,
            deadline = if (running) deadline else 0L,
        )
    }

    fun save(state: PomodoroTimerState) {
        preferences.edit()
            .putBoolean(KEY_IS_WORK, state.isWork)
            .putInt(KEY_SECONDS, state.seconds)
            .putBoolean(KEY_RUNNING, state.running)
            .putLong(KEY_DEADLINE, state.deadline)
            .apply()
    }

    private companion object {
        const val FILE_NAME = "td_coins_pomodoro"
        const val KEY_IS_WORK = "is_work"
        const val KEY_SECONDS = "seconds"
        const val KEY_RUNNING = "running"
        const val KEY_DEADLINE = "deadline"
    }
}

object PomodoroAlarm {
    private const val EXTRA_IS_WORK = "pomodoro_is_work"
    private const val ALARM_REQUEST_CODE = 7302

    /** Programa el aviso de fin de bloque; funciona aunque la app esté en segundo plano o cerrada. */
    fun schedule(context: Context, isWork: Boolean, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = pendingIntent(context, isWork)
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // El permiso de alarmas exactas puede revocarse; se usa una alarma inexacta.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent(context, isWork = true))
    }

    /** Las alarmas se pierden al reiniciar el teléfono; se reprograman si el bloque sigue en curso. */
    fun restore(context: Context) {
        val state = PomodoroPreferences(context).load()
        if (state.running && state.deadline > System.currentTimeMillis()) {
            schedule(context, state.isWork, state.deadline)
        }
    }

    private fun pendingIntent(context: Context, isWork: Boolean): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            Intent(context, PomodoroAlarmReceiver::class.java).putExtra(EXTRA_IS_WORK, isWork),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    internal fun isWork(intent: Intent?): Boolean = intent?.getBooleanExtra(EXTRA_IS_WORK, true) ?: true
}

class PomodoroAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        AppNotifications.createChannels(context)
        AppNotifications.showPomodoroFinished(context, wasWork = PomodoroAlarm.isWork(intent))
    }
}
