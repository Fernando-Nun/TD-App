package com.tdcoins.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MissionsScreen(
    missions: List<Mission>,
    onMissionsChange: (List<Mission>) -> Unit,
    onReward: (Mission) -> Unit = {},
    onMissionCompleted: ((Mission) -> Unit)? = null,
    initialMissionTitle: String? = null,
    onInitialMissionTitleConsumed: () -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    var showAdd by remember { mutableStateOf(false) }
    var celebratedId by remember { mutableStateOf<String?>(null) }
    var dialogTitle by remember { mutableStateOf("") }

    LaunchedEffect(initialMissionTitle) {
        initialMissionTitle
            ?.takeIf { it.isNotBlank() }
            ?.let { title ->
                dialogTitle = title
                showAdd = true
                onInitialMissionTitleConsumed()
            }
    }

    fun incrementMission(mission: Mission) {
        if (mission.completed) return
        val nextProgress = (mission.progress + 1).coerceAtMost(mission.target)
        val completed = nextProgress >= mission.target
        onMissionsChange(
            missions.map {
                if (it.id == mission.id) {
                    it.copy(progress = nextProgress, completed = completed, updatedAt = System.currentTimeMillis())
                } else it
            },
        )
        if (completed) {
            celebratedId = mission.id
            onMissionCompleted?.invoke(mission) ?: onReward(mission)
        }
    }

    LaunchedEffect(celebratedId) {
        if (celebratedId != null) {
            delay(1800)
            celebratedId = null
        }
    }

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("missions-list"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Mis Misiones", fontSize = 24.sp, fontWeight = FontWeight.Black)
                         Text("Completa y gana TD-Coins", color = MutedText, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    IconButton(
                        onClick = {
                            dialogTitle = ""
                            showAdd = true
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar misión", tint = Color.White)
                    }
                }
            }
            if (missions.isEmpty()) {
                item {
                    ScreenCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("Aún no tienes misiones", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Agrega una meta propia para comenzar a ganar TD-Coins.", color = MutedText, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                        }
                    }
                }
            }
            items(missions, key = { it.id }) { mission ->
                MissionCard(
                    mission = mission,
                    celebrating = celebratedId == mission.id,
                    onIncrement = { incrementMission(mission) },
                )
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
        }

        if (showAdd) {
            AddMissionDialog(
                initialTitle = dialogTitle,
                onDismiss = {
                    dialogTitle = ""
                    showAdd = false
                },
                onAdd = { title, category, target ->
                    val newMission = Mission(
                        id = java.util.UUID.randomUUID().toString(),
                        title = title,
                        category = category,
                        target = target,
                        progress = 0,
                        coins = calculateMissionReward(title, category, target),
                    )
                    onMissionsChange(listOf(newMission) + missions)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    dialogTitle = ""
                    showAdd = false
                },
            )
        }
    }
}

@Composable
private fun MissionCard(
    mission: Mission,
    celebrating: Boolean,
    onIncrement: () -> Unit,
) {
    val percentage = (mission.progress.toFloat() / mission.target).coerceIn(0f, 1f)
    Box {
        ScreenCard {
            Column(modifier = Modifier.padding(15.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                mission.category.label,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(mission.category.color)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                            if (mission.completed) {
                                Text(
                                    "✓ Completada",
                                    color = Color(0xFF15803D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                        Text(
                            mission.title,
                            color = if (mission.completed) MutedText else Foreground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 5.dp),
                        )
                    }
                    CoinBadge(mission.coins, small = true)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("${mission.progress}/${mission.target} pasos", color = MutedText, fontSize = 11.sp)
                            Text("${(percentage * 100).toInt()}%", color = MutedText, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp)
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(MutedLavender)
                                .semantics {
                                    progressBarRangeInfo = ProgressBarRangeInfo(percentage, 0f..1f)
                                },
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(percentage)
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(mission.category.color),
                            )
                        }
                    }
                    if (!mission.completed) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onIncrement,
                            modifier = Modifier
                                .size(width = 48.dp, height = 36.dp)
                                .semantics {
                                    contentDescription = "Avanzar misión ${mission.title}"
                                },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = mission.category.color),
                        ) {
                            Text("+1", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
        if (celebrating) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33FBBF24)),
                contentAlignment = Alignment.Center,
            ) {
                Text("🎉", fontSize = 42.sp)
            }
        }
    }
}

@Composable
private fun AddMissionDialog(
    initialTitle: String,
    onDismiss: () -> Unit,
    onAdd: (String, MissionCategory, Int) -> Unit,
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var category by remember { mutableStateOf<MissionCategory?>(null) }
    var targetInput by remember { mutableStateOf("") }
    val target = targetInput.toIntOrNull()
    val targetIsValid = target != null && target in 1..30
    val canCreate = title.isNotBlank() && category != null && targetIsValid
    val reward = category?.takeIf { title.isNotBlank() }?.let { selectedCategory ->
        target?.takeIf { targetIsValid }?.let { validTarget ->
            calculateMissionReward(title, selectedCategory, validTarget)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Misión", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("¿Qué quieres lograr?") },
                    singleLine = true,
                    modifier = Modifier
                        .semantics { traversalIndex = 0f }
                        .testTag("mission-title-input"),
                )
                Text("Categoría", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .semantics { traversalIndex = 1f },
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    MissionCategory.entries.forEach { option ->
                        FilterChip(
                            selected = category == option,
                            onClick = { category = option },
                            label = { Text(option.label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = option.color,
                                selectedLabelColor = Color.White,
                            ),
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetInput,
                        onValueChange = { value ->
                            targetInput = value.filter(Char::isDigit).take(2)
                        },
                        label = { Text("Pasos meta (1–30)") },
                        isError = targetInput.isNotEmpty() && !targetIsValid,
                        supportingText = {
                            if (targetInput.isNotEmpty() && !targetIsValid) {
                                Text("Ingresa un número del 1 al 30")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mission-target-input"),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recompensa automática", color = MutedText, fontSize = 11.sp)
                        Text(
                            reward?.let { "$it TD-Coins" } ?: "Completa los campos requeridos",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    category?.let { selectedCategory ->
                        target?.takeIf { it in 1..30 }?.let { validTarget ->
                            if (title.isNotBlank()) {
                                onAdd(title.trim(), selectedCategory, validTarget)
                            }
                        }
                    }
                },
                enabled = canCreate,
            ) {
                Text("Agregar Misión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}