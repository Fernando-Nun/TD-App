package com.tdcoins.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun VoiceScreen() {
    val challenges = remember { voiceChallenges() }
    var recording by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(emptyList<String>()) }
    var showPlan by remember { mutableStateOf(false) }

    LaunchedEffect(recording) {
        if (recording) {
            delay(3000)
            recording = false
        }
    }

    if (showPlan && selectedIds.isNotEmpty()) {
        PersonalizedPlan(
            challenges = challenges.filter { it.id in selectedIds },
            onBack = { showPlan = false },
        )
        return
    }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Personaliza tu App", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Cuéntame tus retos o elige los que aplican", color = MutedText, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
        item {
            ScreenCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Presiona y habla sobre tus dificultades", color = MutedText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Surface(
                        onClick = { recording = !recording },
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .size(80.dp),
                        shape = CircleShape,
                        color = if (recording) Color(0xFFEF4444) else PrimaryPurple,
                        shadowElevation = 8.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (recording) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = if (recording) "Detener grabación" else "Iniciar grabación",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp),
                            )
                        }
                    }
                    if (recording) {
                        Row(
                            modifier = Modifier
                                .height(42.dp)
                                .padding(top = 10.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            listOf(15, 28, 20, 32, 18).forEach { barHeight ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 7.dp, height = barHeight.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryPurple),
                                )
                            }
                        }
                        Text("Escuchando...", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("o selecciona tus retos abajo", color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
                    }
                }
            }
        }
        item { SectionLabel("Mis retos principales", modifier = Modifier.padding(top = 3.dp)) }
        items(challenges, key = { it.id }) { challenge ->
            val selected = challenge.id in selectedIds
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selected) MutedLavender else Color.White)
                    .border(2.dp, if (selected) PrimaryPurple else BorderLavender, RoundedCornerShape(16.dp))
                    .clickable {
                        selectedIds = if (selected) selectedIds - challenge.id else selectedIds + challenge.id
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                Text(challenge.icon, fontSize = 23.sp)
                Text(challenge.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (selected) PrimaryPurple else Color.Transparent)
                        .border(2.dp, if (selected) PrimaryPurple else BorderLavender, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selected) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
        if (selectedIds.isNotEmpty()) {
            item {
                Button(
                    onClick = { showPlan = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Ver mi Plan Personalizado (${selectedIds.size} retos) →", fontWeight = FontWeight.Black)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
}

@Composable
private fun PersonalizedPlan(
    challenges: List<VoiceChallenge>,
    onBack: () -> Unit,
) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MutedLavender),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", modifier = Modifier.size(19.dp))
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Tu Plan Personalizado", fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("Basado en tus desafíos", color = MutedText, fontSize = 11.sp)
                }
            }
        }
        items(challenges, key = { it.id }) { challenge ->
            ScreenCard {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(challenge.icon, fontSize = 23.sp)
                        Text(challenge.text, fontSize = 13.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(start = 8.dp))
                    }
                    Text(
                        "RECORDATORIOS ACTIVADOS",
                        color = PrimaryPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 14.dp, bottom = 5.dp),
                    )
                    challenge.reminders.forEach { reminder ->
                        Text("• $reminder", fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(vertical = 3.dp))
                    }
                    Text(
                        "PLAN DE ACCIÓN",
                        color = SecondaryTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 12.dp, bottom = 5.dp),
                    )
                    challenge.plan.forEachIndexed { index, action ->
                        Text("${index + 1}. $action", fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(vertical = 3.dp))
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(SecondaryTeal, Color(0xFF0D9488))))
                    .padding(15.dp),
            ) {
                Text("Recuerda", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(
                    "El TDAH es una diferencia, no un defecto. Con las herramientas correctas, puedes lograr todo lo que te propones. ¡Vamos!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
}