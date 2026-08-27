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
)

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