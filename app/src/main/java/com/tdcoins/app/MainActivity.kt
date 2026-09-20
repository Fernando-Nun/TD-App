package com.tdcoins.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val notificationDestination = mutableStateOf<AppTab?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppNotifications.createChannels(this)
        readDestination(intent)
        enableEdgeToEdge()
        setContent {
            TDCoinsTheme {
                var showLoading by androidx.compose.runtime.rememberSaveable { mutableStateOf(true) }
                if (showLoading) {
                    LoadingSplash(onFinished = { showLoading = false })
                } else {
                    TDCoinsApp(
                        notificationDestination = notificationDestination.value,
                        onDestinationConsumed = { notificationDestination.value = null },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readDestination(intent)
    }

    private fun readDestination(intent: Intent?) {
        notificationDestination.value = intent
            ?.getStringExtra(AppNotifications.EXTRA_DESTINATION)
            ?.let { value -> AppTab.entries.find { it.name == value } }
    }
}

@Composable
private fun LoadingSplash(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }
    val transition = rememberInfiniteTransition(label = "td-loading")
    val logoScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "logo-scale",
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.14f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow-alpha",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFF7F3FF), Color(0xFFEDE7FF), Color(0xFFE2F8F4)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(210.dp)
                .background(Color(0xFF7C3AED).copy(alpha = glowAlpha), CircleShape),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo TD-App",
                modifier = Modifier
                    .size(132.dp)
                    .scale(logoScale),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                "TD-App",
                color = Color(0xFF28164F),
                fontSize = 27.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                "Enfoque que sí avanza",
                color = Color(0xFF695B82),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(25.dp))
            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots() {
    val transition = rememberInfiniteTransition(label = "loading-dots")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "dots-offset",
    )
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val alpha = (0.35f + ((offset + index * 0.22f) % 1f) * 0.65f).coerceIn(0.35f, 1f)
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Color(0xFF14B8A6).copy(alpha = alpha), CircleShape),
            )
        }
    }
}