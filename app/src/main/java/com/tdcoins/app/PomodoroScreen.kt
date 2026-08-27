package com.tdcoins.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PomodoroScreen(
    state: PomodoroUiState,
    onToggleRunning: () -> Unit,
    onReset: () -> Unit,
    onSwitchMode: () -> Unit,
) {
    val totalSeconds = if (state.isWork) 25 * 60 else 5 * 60
    val progress = (1f - state.seconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val accent = if (state.isWork) PrimaryPurple else SecondaryTeal
    val accentLight = if (state.isWork) Color(0xFFEDE9F8) else Color(0xFFCCFBF1)
    val minutes = state.seconds / 60
    val remainingSeconds = state.seconds % 60

    Box(modifier = Modifier.fillMaxSize()) {
        MemphisCircle(
            modifier = Modifier
                .padding(start = 4.dp, top = 10.dp)
                .size(64.dp),
            color = Color(0xFFFBBF24),
            alpha = 0.25f,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (state.isWork) "Tiempo de Enfoque" else "Tiempo de Descanso",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    if (state.isWork) "Concéntrate. Puedes hacerlo." else "Respira, te lo mereces.",
                    color = MutedText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(260.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    drawArc(
                        color = accentLight,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = accent,
                        startAngle = -90f,
                        sweepAngle = (360f * progress).coerceAtLeast(0.01f),
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "%02d:%02d".format(minutes, remainingSeconds),
                        color = accent,
                        fontSize = 47.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        if (state.isWork) "ENFOQUE" else "DESCANSO",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SmallControlButton(Icons.Filled.Refresh, "Reiniciar", onReset)
                Surface(
                    onClick = onToggleRunning,
                    modifier = Modifier.size(78.dp),
                    shape = CircleShape,
                    color = accent,
                    shadowElevation = 8.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (state.running) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (state.running) "Pausar" else "Iniciar",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp),
                        )
                    }
                }
                SmallControlButton(
                    if (state.isWork) Icons.Filled.Coffee else Icons.Filled.TrackChanges,
                    "Cambiar modo",
                    onSwitchMode,
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sesiones completadas", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    repeat(4) { index ->
                        Text(if (index < state.sessions) "🍅" else "⚪", fontSize = 19.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentLight)
                    .padding(16.dp),
            ) {
                Text("Tip para este bloque", color = accent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                Text(
                    if (state.isWork) "Cierra redes sociales. Pon el celular boca abajo. Solo tú y esta tarea."
                    else "Levántate, estírate, toma agua. Tu cerebro lo necesita.",
                    color = Foreground.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (state.showCelebration) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 12.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("🎉", fontSize = 52.sp)
                        Text("¡Pomodoro completo!", color = PrimaryPurple, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("+10 TD-Coins ganadas", color = MutedText, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp))
                        CoinBadge(10, modifier = Modifier.padding(top = 10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallControlButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = Background,
        border = BorderStroke(2.dp, BorderLavender),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = description, tint = Foreground, modifier = Modifier.size(19.dp))
        }
    }
}