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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image

@Composable
fun CoinBadge(
    amount: Int,
    small: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFFFBBF24))
            .padding(horizontal = if (small) 8.dp else 12.dp, vertical = if (small) 3.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(if (small) "🪙" else "🪙", fontSize = if (small) 12.sp else 15.sp)
        Text(
            amount.toString(),
            color = Color(0xFF78350F),
            fontWeight = FontWeight.Black,
            fontSize = if (small) 11.sp else 14.sp,
        )
    }
}

@Composable
fun AppHeader(coins: Int) {
    Surface(color = Background, shadowElevation = 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo TD-Coins",
                modifier = Modifier.size(42.dp),
                contentScale = ContentScale.Fit,
            )
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3C4))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("🪙", fontSize = 12.sp)
                Text(
                    coins.toString(),
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(active: AppTab, onNavigate: (AppTab) -> Unit) {
    val tabs = listOf(
        AppTab.HOME to (Icons.Filled.Home to "Inicio"),
        AppTab.POMODORO to (Icons.Filled.Timer to "Pomodoro"),
        AppTab.MISSIONS to (Icons.Filled.Checklist to "Misiones"),
        AppTab.STORE to (Icons.Filled.ShoppingBag to "Tienda"),
        AppTab.VOICE to (Icons.Filled.Mic to "Mi Perfil"),
    )
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            tabs.forEach { (tab, iconAndLabel) ->
                val (icon, label) = iconAndLabel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    IconButton(
                        onClick = { onNavigate(tab) },
                        modifier = Modifier.size(34.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (active == tab) PrimaryPurple else Color(0xFF9CA3AF),
                            modifier = Modifier.size(if (active == tab) 25.dp else 21.dp),
                        )
                    }
                    Text(
                        label,
                        color = if (active == tab) PrimaryPurple else Color(0xFF9CA3AF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    if (active == tab) {
                        Spacer(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(PrimaryPurple),
                        )
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderLavender, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        content = content,
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        modifier = modifier,
        color = MutedText,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.1.sp,
    )
}

@Composable
fun MemphisCircle(modifier: Modifier, color: Color, alpha: Float = 0.15f) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = alpha)),
    )
}

@Composable
fun IconTile(icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}