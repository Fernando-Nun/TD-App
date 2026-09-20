package com.tdcoins.app

import androidx.annotation.DrawableRes

data class Mission(
    val id: String,
    val title: String,
    val category: MissionCategory,
    val target: Int,
    val progress: Int,
    val coins: Int,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

data class EconomyEvent(
    val id: String,
    val delta: Int,
    val createdAt: Long = System.currentTimeMillis(),
)

data class AppSnapshot(
    val coins: Int = 45,
    val pomodorosDone: Int = 0,
    val pomodoroBaseline: Int = 0,
    val missions: List<Mission> = initialMissions(),
    val purchasedIds: List<String> = emptyList(),
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val voiceNotes: List<String> = emptyList(),
    val economyEvents: List<EconomyEvent> = emptyList(),
)

fun mergeSnapshots(local: AppSnapshot, remote: AppSnapshot): AppSnapshot {
    val missions = (local.missions + remote.missions)
        .groupBy { it.id }
        .map { (_, versions) ->
            versions.maxWith(compareBy<Mission> { it.progress }.thenBy { it.updatedAt })
        }
        .sortedByDescending { it.createdAt }
    val events = (local.economyEvents + remote.economyEvents).distinctBy { it.id }
    val pomodoroBaseline = maxOf(local.pomodoroBaseline, remote.pomodoroBaseline)
    return AppSnapshot(
        coins = 45 + events.sumOf { it.delta },
        pomodorosDone = pomodoroBaseline + events.count { it.id.startsWith("pomodoro-") },
        pomodoroBaseline = pomodoroBaseline,
        missions = missions,
        purchasedIds = (local.purchasedIds + remote.purchasedIds).distinct(),
        streakDays = maxOf(local.streakDays, remote.streakDays),
        lastActiveDate = maxOf(local.lastActiveDate, remote.lastActiveDate),
        voiceNotes = (local.voiceNotes + remote.voiceNotes).distinct().take(20),
        economyEvents = events,
    )
}

enum class MissionCategory(val label: String, val color: androidx.compose.ui.graphics.Color) {
    FOCUS("Enfoque", PrimaryPurple),
    HABITS("Hábitos", AccentOrange),
    HEALTH("Salud", SecondaryTeal),
    ORDER("Orden", androidx.compose.ui.graphics.Color(0xFF3B82F6)),
    SOCIAL("Social", AccentPink),
}

data class StoreItem(
    val id: String,
    val name: String,
    val price: Int,
    @DrawableRes val imageRes: Int,
    val description: String,
    val tag: StoreTag,
)

enum class StoreTag(val label: String) {
    ALL("Todos"),
    STRESS("Anti-estrés"),
    ACCESSORY("Accesorio"),
    LIFESTYLE("Lifestyle"),
    CLOTHING("Ropa"),
    TECH("Tech"),
    PACK("Pack"),
}

data class VoiceChallenge(
    val id: String,
    val text: String,
    val icon: String,
    val reminders: List<String>,
    val plan: List<String>,
)

enum class AppTab(val label: String) {
    HOME("Inicio"),
    POMODORO("Pomodoro"),
    MISSIONS("Misiones"),
    STORE("Tienda"),
    VOICE("Mi Perfil"),
}

data class PomodoroUiState(
    val isWork: Boolean,
    val seconds: Int,
    val running: Boolean,
    val sessions: Int,
    val showCelebration: Boolean,
)