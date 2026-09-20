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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import kotlin.math.ceil

@Composable
fun TDCoinsApp(
    notificationDestination: AppTab? = null,
    onDestinationConsumed: () -> Unit = {},
) {
    val context = LocalContext.current
    val persistence = remember { AppPersistence(context) }
    val syncClient = remember { SyncClient(persistence) }
    val scope = rememberCoroutineScope()
    var signedIn by remember { mutableStateOf(persistence.sessionToken() != null) }
    var authenticating by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    var authMessage by remember { mutableStateOf<String?>(null) }

    if (!signedIn) {
        AuthScreen(
            loading = authenticating,
            error = authError,
            message = authMessage,
            onSubmit = { email, password, register ->
                scope.launch {
                    authenticating = true
                    authError = null
                    authMessage = null
                    syncClient.authenticate(email, password, register)
                        .onSuccess { signedIn = true }
                        .onFailure {
                            authError = it.message ?: if (register) {
                                "No se pudo crear la cuenta. Revisa tu conexión e inténtalo de nuevo."
                            } else {
                                "No se pudo iniciar sesión. Revisa tu conexión e inténtalo de nuevo."
                            }
                        }
                    authenticating = false
                }
            },
            onRequestReset = { email ->
                scope.launch {
                    authenticating = true
                    authError = null
                    authMessage = null
                    syncClient.requestPasswordReset(email)
                        .onSuccess { authMessage = it }
                        .onFailure { authError = it.message ?: "No se pudo solicitar el código." }
                    authenticating = false
                }
            },
            onResetPassword = { email, code, password ->
                scope.launch {
                    authenticating = true
                    authError = null
                    authMessage = null
                    syncClient.resetPassword(email, code, password)
                        .onSuccess { authMessage = it }
                        .onFailure { authError = it.message ?: "No se pudo cambiar la contraseña." }
                    authenticating = false
                }
            },
        )
        return
    }
    TDCoinsContent(persistence, syncClient, notificationDestination, onDestinationConsumed) {
        syncClient.signOut()
        signedIn = false
    }
}

@Composable
private fun TDCoinsContent(
    persistence: AppPersistence,
    syncClient: SyncClient,
    notificationDestination: AppTab?,
    onDestinationConsumed: () -> Unit,
    onSignOut: () -> Unit,
) {
    val context = LocalContext.current
    val initial = remember {
        persistence.load().let { snapshot ->
            if (snapshot.economyEvents.isEmpty() && snapshot.coins != 45) {
                snapshot.copy(
                    economyEvents = listOf(EconomyEvent("migration-${UUID.randomUUID()}", snapshot.coins - 45)),
                )
            } else snapshot
        }
    }
    var tab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var coins by remember { mutableIntStateOf(initial.coins) }
    var pomodorosDone by remember { mutableIntStateOf(initial.pomodorosDone) }
    var pomodoroBaseline by remember { mutableIntStateOf(initial.pomodoroBaseline) }
    var missions by remember { mutableStateOf(initial.missions) }
    var deletedMissionIds by remember { mutableStateOf(initial.deletedMissionIds) }
    var challenges by remember { mutableStateOf(initial.challenges) }
    var deletedChallengeIds by remember { mutableStateOf(initial.deletedChallengeIds) }
    var purchasedIds by remember { mutableStateOf(initial.purchasedIds) }
    var streakDays by remember { mutableIntStateOf(initial.streakDays) }
    var lastActiveDate by remember { mutableStateOf(initial.lastActiveDate) }
    var voiceNotes by remember { mutableStateOf(initial.voiceNotes) }
    var economyEvents by remember { mutableStateOf(initial.economyEvents) }
    var syncStatus by remember { mutableStateOf("Sincronizando…") }
    var pomodoroIsWork by rememberSaveable { mutableStateOf(true) }
    var pomodoroSeconds by rememberSaveable { mutableIntStateOf(25 * 60) }
    var pomodoroRunning by rememberSaveable { mutableStateOf(false) }
    var pomodoroSessions by rememberSaveable { mutableIntStateOf(0) }
    var pomodoroDeadline by rememberSaveable { mutableLongStateOf(0L) }
    var showPomodoroCelebration by rememberSaveable { mutableStateOf(false) }
    val reminderPreferences = remember { ReminderPreferences(context) }
    var reminderSettings by remember { mutableStateOf(reminderPreferences.load()) }
    var showReminderSettings by rememberSaveable { mutableStateOf(false) }
    var notificationPermissionGranted by remember {
        mutableStateOf(AppNotifications.notificationsAllowed(context))
    }
    var enableRemindersAfterPermission by remember { mutableStateOf(false) }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationPermissionGranted = granted
        val updated = settingsAfterPermissionResult(
            reminderSettings,
            granted = granted,
            enableAfterGrant = enableRemindersAfterPermission,
        )
        enableRemindersAfterPermission = false
        if (updated != reminderSettings) {
            reminderSettings = updated
            reminderPreferences.save(reminderSettings)
            AppNotifications.updateSchedule(context, reminderSettings)
        }
    }

    LaunchedEffect(notificationDestination) {
        notificationDestination?.let {
            tab = it
            showReminderSettings = false
            onDestinationConsumed()
        }
    }

    fun registerActivity() {
        val today = LocalDate.now()
        val updated = updateStreak(streakDays, lastActiveDate, today)
        streakDays = updated.days
        lastActiveDate = updated.activeDate
    }

    fun applySnapshot(snapshot: AppSnapshot) {
        coins = snapshot.coins
        pomodorosDone = snapshot.pomodorosDone
        pomodoroBaseline = snapshot.pomodoroBaseline
        missions = snapshot.missions
        deletedMissionIds = snapshot.deletedMissionIds
        challenges = snapshot.challenges
        deletedChallengeIds = snapshot.deletedChallengeIds
        purchasedIds = snapshot.purchasedIds
        streakDays = snapshot.streakDays
        lastActiveDate = snapshot.lastActiveDate
        voiceNotes = snapshot.voiceNotes
        economyEvents = snapshot.economyEvents
    }

    fun currentSnapshot() = AppSnapshot(
        coins = coins,
        pomodorosDone = pomodorosDone,
        pomodoroBaseline = pomodoroBaseline,
        missions = missions,
        deletedMissionIds = deletedMissionIds,
        challenges = challenges,
        deletedChallengeIds = deletedChallengeIds,
        purchasedIds = purchasedIds,
        streakDays = streakDays,
        lastActiveDate = lastActiveDate,
        voiceNotes = voiceNotes,
        economyEvents = economyEvents,
    )

    fun addCoins(delta: Int, eventId: String) {
        if (economyEvents.any { it.id == eventId }) return
        economyEvents = economyEvents + EconomyEvent(eventId, delta)
        coins += delta
    }

    val latestSnapshot by rememberUpdatedState(currentSnapshot())

    LaunchedEffect(coins, pomodorosDone, missions, deletedMissionIds, challenges, deletedChallengeIds, purchasedIds, streakDays, lastActiveDate, voiceNotes, economyEvents) {
        persistence.save(currentSnapshot())
    }

    LaunchedEffect(Unit) {
        while (true) {
            syncClient.sync(latestSnapshot)
                .onSuccess {
                    applySnapshot(mergeSnapshots(latestSnapshot, it))
                    syncStatus = "Sincronizado"
                }
                .onFailure {
                    syncStatus = if (it.message?.contains("sesión", ignoreCase = true) == true) {
                        onSignOut()
                        "Sesión caducada"
                    } else "Sin conexión · cambios guardados"
                }
            delay(10_000)
        }
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
                    addCoins(10, "pomodoro-${UUID.randomUUID()}")
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
            topBar = {
                AppHeader(
                    coins = coins,
                    syncStatus = syncStatus,
                    onOpenReminders = { showReminderSettings = true },
                    onSignOut = onSignOut,
                )
            },
            bottomBar = {
                if (!wideLayout && !showReminderSettings) BottomNavigation(tab) { tab = it }
            },
        ) { padding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                if (wideLayout && !showReminderSettings) SideNavigation(tab) { tab = it }
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    if (showReminderSettings) {
                        ReminderSettingsScreen(
                            settings = reminderSettings,
                            permissionGranted = notificationPermissionGranted,
                            onSettingsChange = { updated ->
                                reminderSettings = updated
                                reminderPreferences.save(updated)
                                AppNotifications.updateSchedule(context, updated)
                            },
                            onRequestPermission = { enableAfterGrant ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    enableRemindersAfterPermission = enableAfterGrant
                                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            },
                            onBack = { showReminderSettings = false },
                        )
                    } else when (tab) {
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
                    onMissionCompleted = { mission ->
                        missions = missions.filterNot { it.id == mission.id }
                        deletedMissionIds = (deletedMissionIds + mission.id).distinct()
                        addCoins(mission.coins, "mission-reward-${mission.id}")
                        registerActivity()
                        AppNotifications.showProgress(
                            context,
                            "¡Misión completada!",
                            "Ganaste ${mission.coins} TD-Coins y mantienes una racha de $streakDays día(s).",
                        )
                    },
                )
                AppTab.STORE -> StoreScreen(
                    coins = coins,
                    purchasedIds = purchasedIds,
                    onPurchase = { item ->
                        if (coins >= item.price && item.id !in purchasedIds) {
                            addCoins(-item.price, "purchase-${item.id}")
                            purchasedIds = purchasedIds + item.id
                        }
                    },
                )
                AppTab.VOICE -> VoiceScreen(
                    savedNotes = voiceNotes,
                    challenges = challenges,
                    onChallengesChange = { challenges = it },
                    onChallengeDeleted = { id ->
                        challenges = challenges.filterNot { it.id == id }
                        deletedChallengeIds = (deletedChallengeIds + id).distinct()
                    },
                    onSaveNote = { note ->
                        voiceNotes = (listOf(note) + voiceNotes).distinct().take(20)
                    },
                    onCreateMission = { title ->
                        missions = listOf(
                            Mission(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                category = MissionCategory.FOCUS,
                                target = 1,
                                progress = 0,
                                coins = calculateMissionReward(title, MissionCategory.FOCUS, 1),
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