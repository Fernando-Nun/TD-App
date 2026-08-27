package com.tdcoins.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    coins: Int,
    pomodorosDone: Int,
    completedMissions: Int,
    onNavigate: (AppTab) -> Unit,
) {
    val hour = LocalDateTime.now().hour
    val greeting = when {
        hour < 12 -> "Buenos días"
        hour < 18 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    val tips = listOf(
        "El TDAH no es falta de atención — es atención desregulada. Hay una gran diferencia.",
        "Los pequeños pasos cuentan. Cada Pomodoro completado es una victoria real.",
        "Tu cerebro con TDAH es creativo, apasionado y único. Úsalo a tu favor.",
        "No necesitas motivación para empezar — solo necesitas dar el primer paso.",
    )
    val tipOfDay = tips[LocalDateTime.now().dayOfWeek.value % tips.size]
    val stats = listOf(
        Triple("TD-Coins", coins, Color(0xFFF59E0B) to "🪙"),
        Triple("Pomodoros", pomodorosDone, PrimaryPurple to "🍅"),
        Triple("Misiones", completedMissions, SecondaryTeal to "✅"),
    )
    val shortcuts = listOf(
        Shortcut(AppTab.POMODORO, Icons.Filled.Timer, "Pomodoro", "Inicia un bloque de enfoque", PrimaryPurple),
        Shortcut(AppTab.MISSIONS, Icons.AutoMirrored.Filled.ListAlt, "Misiones", "Revisa tu progreso", AccentOrange),
        Shortcut(AppTab.VOICE, Icons.Filled.Mic, "Personalizar", "Cuéntame tus retos", SecondaryTeal),
        Shortcut(AppTab.STORE, Icons.Filled.ShoppingBag, "Tienda", "Canjea tus monedas", AccentPink),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        MemphisCircle(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 26.dp, y = (-30).dp),
            color = AccentOrange,
            alpha = 0.12f,
        )
        MemphisCircle(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopStart)
                .offset(x = (-25).dp, y = 75.dp),
            color = PrimaryPurple,
            alpha = 0.1f,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(greeting, color = MutedText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "¡Hoy puedes\nlograrlo!",
                        color = Foreground,
                        fontSize = 31.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    CoinBadge(coins)
                    Text("TD-Coins", color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                stats.forEach { (label, value, colorAndIcon) ->
                    val (color, icon) = colorAndIcon
                    ScreenCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(icon, fontSize = 23.sp)
                            Text(
                                value.toString(),
                                color = color,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                            Text(label, color = MutedText, fontSize = 10.sp)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1A0A2E), Color(0xFF321451))))
                    .padding(16.dp),
            ) {
                MemphisCircle(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 22.dp, y = (-22).dp),
                    color = AccentOrange,
                    alpha = 0.12f,
                )
                Column {
                    Text("FRASE DEL DÍA", color = Color(0xFFD8B4FE), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text(
                        tipOfDay,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
            }

            Column {
                SectionLabel("Acceso rápido")
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    shortcuts.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            row.forEach { shortcut ->
                                ScreenCard(modifier = Modifier.weight(1f)) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onNavigate(shortcut.tab) }
                                            .padding(14.dp),
                                    ) {
                                        IconTile(shortcut.icon, shortcut.color)
                                        Text(
                                            shortcut.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 8.dp),
                                        )
                                        Text(shortcut.description, color = MutedText, fontSize = 11.sp, lineHeight = 15.sp)
                                    }
                                }
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            ScreenCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Próxima recompensa", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        CoinBadge(80, small = true)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MutedLavender),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((coins / 80f).coerceIn(0f, 1f))
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                                .background(PrimaryPurple),
                        )
                    }
                    Text(
                        if (coins >= 80) "¡Ya puedes canjear el Llavero!"
                        else "Te faltan ${80 - coins} monedas para el Llavero Fuerza Mental",
                        color = MutedText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private data class Shortcut(
    val tab: AppTab,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val color: Color,
)