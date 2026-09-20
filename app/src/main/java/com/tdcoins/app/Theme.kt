package com.tdcoins.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Background = Color(0xFFF5F0FF)
val Foreground = Color(0xFF1A0A2E)
val PrimaryPurple = Color(0xFF7C3AED)
val SecondaryTeal = Color(0xFF14B8A6)
val AccentOrange = Color(0xFFF97316)
val AccentPink = Color(0xFFEC4899)
val BorderLavender = Color(0xFFD8CCF0)
val MutedLavender = Color(0xFFEDE9F8)
val MutedText = Color(0xFF6B5B8A)

private val TdCoinsColors = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    background = Background,
    onBackground = Foreground,
    surface = Color.White,
    onSurface = Foreground,
    surfaceVariant = MutedLavender,
    onSurfaceVariant = MutedText,
    outline = BorderLavender,
)

private val TdCoinsTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        lineHeight = 29.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 21.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
    ),
)

@Composable
fun TDCoinsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TdCoinsColors,
        typography = TdCoinsTypography,
        content = content,
    )
}