package com.tdcoins.app

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.math.ceil

@Composable
fun TDCoinsApp() {
    val context = LocalContext.current
    val persistence = remember { AppPersistence(context) }
    val initial = remember { persistence.load() }
    var tab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var coins by remember { mutableIntStateOf(initial.coins) }
    var pomodorosDone by remember { mutableIntStateOf(initial.pomodorosDone) }
    var missions by remember { mutableStateOf(initial.missions) }
    var purchasedIds by remember { mutableStateOf(initial.purchasedIds) }
    var streakDays by remember { mutableIntStateOf(initial.streakDays) }
    var lastActiveDate by remember { mutableStateOf(initial.lastActiveDate) }
    var voiceNotes by remember { mutableStateOf(initial.voiceNotes) }
    var pomodoroIsWork by rememberSaveable { mutableStateOf(true) }
    var pomodoroSeconds by rememberSaveable { mutableIntStateOf(25 * 60) }
    var pomodoroRunning by rememberSaveable { mutableStateOf(false) }
    var pomodoroSessions by rememberSaveable { mutableIntStateOf(0) }
    var pomodoroDeadline by rememberSaveable { mutableLongStateOf(0L) }
    var showPomodoroCelebration by rememberSaveable { mutableStateOf(false) }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun registerActivity() {
        val today = LocalDate.now()
        val updated = updateStreak(streakDays, lastActiveDate, today)
        streakDays = updated.days
        lastActiveDate = updated.activeDate
    }

    LaunchedEffect(coins, pomodorosDone, missions, purchasedIds, streakDays, lastActiveDate, voiceNotes) {
        persistence.save(
            AppSnapshot(
                coins = coins,
                pomodorosDone = pomodorosDone,
                missions = missions,
                purchasedIds = purchasedIds,
                streakDays = streakDays,
                lastActiveDate = lastActiveDate,
                voiceNotes = voiceNotes,
            ),
        )
    }

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
                    registerActivity()
                    AppNotifications.showProgress(
                        context,
                        "¡Pomodoro completado!",
                        "Ganaste 10 TD-Coins. Tu racha es de $streakDays día(s).",
                    )
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val wideLayout = maxWidth >= 700.dp
        Scaffold(
            containerColor = Background,
            topBar = { AppHeader(coins) },
            bottomBar = {
                if (!wideLayout) BottomNavigation(tab) { tab = it }
            },
        ) { padding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                if (wideLayout) SideNavigation(tab) { tab = it }
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    when (tab) {
                AppTab.HOME -> HomeScreen(
                    coins = coins,
                    pomodorosDone = pomodorosDone,
                    completedMissions = missions.count { it.completed },
                    streakDays = streakDays,
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
                    onReward = {
                        coins += it
                        registerActivity()
                        AppNotifications.showProgress(
                            context,
                            "¡Misión completada!",
                            "Ganaste $it TD-Coins y mantienes una racha de $streakDays día(s).",
                        )
                    },
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
                AppTab.VOICE -> VoiceScreen(
                    savedNotes = voiceNotes,
                    onSaveNote = { note ->
                        voiceNotes = (listOf(note) + voiceNotes).distinct().take(20)
                    },
                    onCreateMission = { title ->
                        missions = listOf(
                            Mission(
                                id = "voice-${System.currentTimeMillis()}",
                                title = title,
                                category = MissionCategory.FOCUS,
                                target = 1,
                                progress = 0,
                                coins = 25,
                            ),
                        ) + missions
                        tab = AppTab.MISSIONS
                    },
                )
                    }
                }
            }
        }
    }
}