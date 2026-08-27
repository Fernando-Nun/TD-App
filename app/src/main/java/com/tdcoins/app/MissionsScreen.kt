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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MissionsScreen(
    missions: List<Mission>,
    onMissionsChange: (List<Mission>) -> Unit,
    onReward: (Int) -> Unit,
) {
    var showAdd by remember { mutableStateOf(false) }
    var celebratedId by remember { mutableStateOf<String?>(null) }

    fun incrementMission(mission: Mission) {
        if (mission.completed) return
        val nextProgress = (mission.progress + 1).coerceAtMost(mission.target)
        val completed = nextProgress >= mission.target
        onMissionsChange(
            missions.map {
                if (it.id == mission.id) it.copy(progress = nextProgress, completed = completed) else it
            },
        )
        if (completed) {
            celebratedId = mission.id
            onReward(mission.coins)
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
            modifier = Modifier.fillMaxWidth(),
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
                        Text("Completa y gana TD-Coins 🪙", color = MutedText, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                    IconButton(
                        onClick = { showAdd = true },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar misión", tint = Color.White)
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
                onDismiss = { showAdd = false },
                onAdd = { title, category, target, reward ->
                    val newMission = Mission(
                        id = "t${System.currentTimeMillis()}",
                        title = title,
                        category = category,
                        target = target,
                        progress = 0,
                        coins = reward,
                    )
                    onMissionsChange(listOf(newMission) + missions)
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
                                .background(MutedLavender),
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
                            modifier = Modifier.size(width = 48.dp, height = 36.dp),
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
    onDismiss: () -> Unit,
    onAdd: (String, MissionCategory, Int, Int) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(MissionCategory.FOCUS) }
    var target by remember { mutableIntStateOf(5) }
    var reward by remember { mutableIntStateOf(30) }

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
                )
                Text("Categoría", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
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
                        value = target.toString(),
                        onValueChange = { target = it.toIntOrNull()?.coerceIn(1, 30) ?: target },
                        label = { Text("Pasos meta") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = reward.toString(),
                        onValueChange = { reward = it.toIntOrNull()?.coerceIn(10, 500) ?: reward },
                        label = { Text("Recompensa") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onAdd(title.trim(), category, target, reward) },
                enabled = title.isNotBlank(),
            ) {
                Text("Agregar Misión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}