package com.tdcoins.app

import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun CoinIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFF59E0B),
    contentDescription: String? = "TD-Coins",
) {
    Icon(
        imageVector = Icons.Filled.MonetizationOn,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}

@Composable
fun MissionCompleteIcon(
    modifier: Modifier = Modifier,
    tint: Color = SecondaryTeal,
    contentDescription: String? = "Misión completada",
) {
    Icon(
        imageVector = Icons.Filled.CheckCircle,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}

@Composable
fun StreakIcon(
    modifier: Modifier = Modifier,
    tint: Color = AccentOrange,
    contentDescription: String? = "Racha",
) {
    Icon(
        imageVector = Icons.Filled.LocalFireDepartment,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}

@Composable
fun PomodoroIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFEF4444),
    contentDescription: String? = "Pomodoro",
) {
    Canvas(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription ?: ""
        },
    ) {
        val unit = size.minDimension
        val body = tint
        val leaf = Color(0xFF16A34A)
        val center = Offset(size.width / 2f, size.height * 0.58f)
        drawOval(
            color = body,
            topLeft = Offset(size.width * 0.16f, size.height * 0.28f),
            size = Size(size.width * 0.68f, size.height * 0.56f),
        )
        drawCircle(
            color = body,
            radius = unit * 0.26f,
            center = Offset(size.width * 0.32f, size.height * 0.56f),
        )
        drawCircle(
            color = body,
            radius = unit * 0.26f,
            center = Offset(size.width * 0.68f, size.height * 0.56f),
        )
        val leafPath = Path().apply {
            moveTo(center.x, size.height * 0.31f)
            cubicTo(
                size.width * 0.34f,
                size.height * 0.08f,
                size.width * 0.44f,
                size.height * 0.08f,
                center.x,
                size.height * 0.30f,
            )
            cubicTo(
                size.width * 0.56f,
                size.height * 0.08f,
                size.width * 0.66f,
                size.height * 0.08f,
                center.x,
                size.height * 0.31f,
            )
            close()
        }
        drawPath(leafPath, leaf)
        drawCircle(
            color = Color.White.copy(alpha = 0.22f),
            radius = unit * 0.055f,
            center = Offset(size.width * 0.37f, size.height * 0.48f),
        )
    }
}