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
    val missions: List<Mission> = emptyList(),
    val deletedMissionIds: List<String> = emptyList(),
    val challenges: List<VoiceChallenge> = emptyList(),
    val deletedChallengeIds: List<String> = emptyList(),
    val deletedVoiceNoteIds: List<String> = emptyList(),
    val purchasedIds: List<String> = emptyList(),
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val voiceNotes: List<String> = emptyList(),
    val economyEvents: List<EconomyEvent> = emptyList(),
)

fun mergeSnapshots(local: AppSnapshot, remote: AppSnapshot): AppSnapshot {
    val deletedMissionIds = (local.deletedMissionIds + remote.deletedMissionIds + listOf("t1", "t2", "t3", "t4", "t5")).distinct()
    val missions = (local.missions + remote.missions)
        .filter { it.id !in deletedMissionIds && it.id !in setOf("t1", "t2", "t3", "t4", "t5") }
        .groupBy { it.id }
        .map { (_, versions) ->
            versions.maxWith(compareBy<Mission> { it.progress }.thenBy { it.updatedAt })
        }
        .sortedByDescending { it.createdAt }
    val deletedChallengeIds = (local.deletedChallengeIds + remote.deletedChallengeIds).distinct()
    val challenges = (local.challenges + remote.challenges)
        .filter { it.id !in deletedChallengeIds }
        .groupBy { it.id }
        .map { (_, versions) -> versions.maxBy { it.updatedAt } }
        .sortedByDescending { it.createdAt }
    val deletedVoiceNoteIds = (local.deletedVoiceNoteIds + remote.deletedVoiceNoteIds).distinct()
    val events = (local.economyEvents + remote.economyEvents).distinctBy { it.id }
    val pomodoroBaseline = maxOf(local.pomodoroBaseline, remote.pomodoroBaseline)
    return AppSnapshot(
        coins = 45 + events.sumOf { it.delta },
        pomodorosDone = pomodoroBaseline + events.count { it.id.startsWith("pomodoro-") },
        pomodoroBaseline = pomodoroBaseline,
        missions = missions,
        deletedMissionIds = deletedMissionIds,
        challenges = challenges,
        deletedChallengeIds = deletedChallengeIds,
        purchasedIds = (local.purchasedIds + remote.purchasedIds).distinct(),
        streakDays = maxOf(local.streakDays, remote.streakDays),
        lastActiveDate = maxOf(local.lastActiveDate, remote.lastActiveDate),
        voiceNotes = (local.voiceNotes + remote.voiceNotes)
            .distinct()
            .filterNot { it in deletedVoiceNoteIds }
            .take(20),
        deletedVoiceNoteIds = deletedVoiceNoteIds,
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
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

fun calculateMissionReward(title: String, category: MissionCategory, target: Int): Int {
    val normalized = title.lowercase()
    val keywordBonus = listOf("proyecto", "examen", "ejercicio", "entren", "estudi", "limpiar", "organizar")
        .count { normalized.contains(it) } * 3
    val categoryBonus = when (category) {
        MissionCategory.HEALTH, MissionCategory.HABITS -> 5
        MissionCategory.FOCUS -> 4
        MissionCategory.SOCIAL -> 3
        MissionCategory.ORDER -> 2
    }
    return (10 + target.coerceIn(1, 30) * 4 + categoryBonus + keywordBonus).coerceIn(15, 150)
}

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