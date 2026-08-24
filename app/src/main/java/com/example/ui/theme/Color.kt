package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Default brand colors
val PrimaryIndigo = Color(0xFF4F46E5)
val SecondaryPurple = Color(0xFF7C3AED)
val AccentGradientStart = Color(0xFF4F46E5)
val AccentGradientEnd = Color(0xFF7C3AED)

// Light Mode palette
val LightBackground = Color(0xFFF6F7FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFF1F4F9)
val LightTextPrimary = Color(0xFF111827)
val LightTextSecondary = Color(0xFF6B7280)
val LightBorder = Color(0xFFE5E7EB)

// Dark Mode palette (Sophisticated Dark)
val DarkBackground = Color(0xFF0B1020)
val DarkSurface = Color(0xFF151B2D)
val DarkSurfaceElevated = Color(0xFF1C2438)
val DarkTextPrimary = Color(0xFFF9FAFB)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkBorder = Color(0x1AFFFFFF) // subtle white/10 border
val DarkBorderSubtle = Color(0x0DFFFFFF) // subtle white/5 border

// Status colors
val SuccessGreen = Color(0xFF22C55E)
val SuccessGreenDark = Color(0xFF16A34A)
val WarningAmber = Color(0xFFF59E0B)
val DangerRed = Color(0xFFEF4444)
val InfoSky = Color(0xFF0EA5E9)

// Preset brand colors for customization
val PresetColors = listOf(
    0xFF4F46E5, // Indigo
    0xFF7C3AED, // Purple
    0xFF2563EB, // Blue
    0xFF10B981, // Emerald
    0xFFF97316, // Orange
    0xFFEF4444, // Red
    0xFF06B6D4, // Cyan
    0xFFEC4899, // Pink
    0xFF8B5CF6, // Violet
    0xFF14B8A6  // Teal
)

fun getPrimaryGradient(primary: Color): Brush {
    val endColor = when {
        primary == PrimaryIndigo -> SecondaryPurple
        else -> Color(
            red = (primary.red * 0.85f + 0.15f).coerceIn(0f, 1f),
            green = (primary.green * 0.8f).coerceIn(0f, 1f),
            blue = (primary.blue * 1.1f).coerceIn(0f, 1f)
        )
    }
    return Brush.horizontalGradient(listOf(primary, endColor))
}
