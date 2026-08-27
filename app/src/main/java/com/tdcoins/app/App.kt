package com.tdcoins.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import android.net.Uri
import kotlinx.coroutines.delay
import kotlin.math.ceil

private val MissionListSaver = listSaver<List<Mission>, String>(
    save = { missions ->
        missions.map { mission ->
            listOf(
                mission.id,
                Uri.encode(mission.title),
                mission.category.name,
                mission.target.toString(),
                mission.progress.toString(),
                mission.coins.toString(),
                mission.completed.toString(),
            ).joinToString("|")
        }
    },
    restore = { entries ->
        entries.mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size != 7) return@mapNotNull null
            runCatching {
                Mission(
                    id = parts[0],
                    title = Uri.decode(parts[1]),
                    category = MissionCategory.valueOf(parts[2]),
                    target = parts[3].toInt(),
                    progress = parts[4].toInt(),
                    coins = parts[5].toInt(),
                    completed = parts[6].toBoolean(),
                )
            }.getOrNull()
        }.ifEmpty { initialMissions() }
    },
)

@Composable
fun TDCoinsApp() {
    var tab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var coins by rememberSaveable { mutableIntStateOf(45) }
    var pomodorosDone by rememberSaveable { mutableIntStateOf(0) }
    var missions by rememberSaveable(stateSaver = MissionListSaver) { mutableStateOf(initialMissions()) }
    var purchasedIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var pomodoroIsWork by rememberSaveable { mutableStateOf(true) }
    var pomodoroSeconds by rememberSaveable { mutableIntStateOf(25 * 60) }
    var pomodoroRunning by rememberSaveable { mutableStateOf(false) }
    var pomodoroSessions by rememberSaveable { mutableIntStateOf(0) }
    var pomodoroDeadline by rememberSaveable { mutableLongStateOf(0L) }
    var showPomodoroCelebration by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(pomodoroRunning, pomodoroDeadline) {
        while (pomodoroRunning) {
            val millisLeft = pomodoroDeadline - System.currentTimeMillis()
            val secondsLeft = ceil(millisLeft / 1000.0).toInt().coerceAtLeast(0)
            pomodoroSeconds = secondsLeft
            if (secondsLeft == 0) {
                pomodoroRunning = false
                pomodoroDeadline = 0L
                if (pomodoroIsWork) {
                    pomodoroSessions += 1
                    pomodorosDone += 1
                    coins += 10
                    showPomodoroCelebration = true
                    pomodoroIsWork = false
                    pomodoroSeconds = 5 * 60
                } else {
                    pomodoroIsWork = true
                    pomodoroSeconds = 25 * 60
                }
            } else {
                delay(250)
            }
        }
    }

    LaunchedEffect(showPomodoroCelebration) {
        if (showPomodoroCelebration) {
            delay(2000)
            showPomodoroCelebration = false
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = { AppHeader(coins) },
        bottomBar = { BottomNavigation(tab) { tab = it } },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (tab) {
                AppTab.HOME -> HomeScreen(
                    coins = coins,
                    pomodorosDone = pomodorosDone,
                    completedMissions = missions.count { it.completed },
                    onNavigate = { tab = it },
                )
                AppTab.POMODORO -> PomodoroScreen(
                    state = PomodoroUiState(
                        isWork = pomodoroIsWork,
                        seconds = pomodoroSeconds,
                        running = pomodoroRunning,
                        sessions = pomodoroSessions,
                        showCelebration = showPomodoroCelebration,
                    ),
                    onToggleRunning = {
                        if (pomodoroRunning) {
                            val millisLeft = pomodoroDeadline - System.currentTimeMillis()
                            pomodoroSeconds = ceil(millisLeft / 1000.0).toInt().coerceAtLeast(0)
                            pomodoroRunning = false
                            pomodoroDeadline = 0L
                        } else {
                            pomodoroDeadline = System.currentTimeMillis() + pomodoroSeconds * 1000L
                            pomodoroRunning = true
                        }
                    },
                    onReset = {
                        pomodoroRunning = false
                        pomodoroDeadline = 0L
                        pomodoroSeconds = if (pomodoroIsWork) 25 * 60 else 5 * 60
                    },
                    onSwitchMode = {
                        pomodoroRunning = false
                        pomodoroDeadline = 0L
                        pomodoroIsWork = !pomodoroIsWork
                        pomodoroSeconds = if (pomodoroIsWork) 25 * 60 else 5 * 60
                    },
                )
                AppTab.MISSIONS -> MissionsScreen(
                    missions = missions,
                    onMissionsChange = { missions = it },
                    onReward = { coins += it },
                )
                AppTab.STORE -> StoreScreen(
                    coins = coins,
                    purchasedIds = purchasedIds,
                    onPurchase = { item ->
                        if (coins >= item.price && item.id !in purchasedIds) {
                            coins -= item.price
                            purchasedIds = purchasedIds + item.id
                        }
                    },
                )
                AppTab.VOICE -> VoiceScreen()
            }
        }
    }
}