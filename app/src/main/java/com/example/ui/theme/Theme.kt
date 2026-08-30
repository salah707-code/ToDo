package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.PrimaryColorPreset
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferences
import com.example.ui.localization.AppStringsProvider
import com.example.ui.localization.LocalAppStrings

@Composable
fun EnjazTheme(
    preferences: UserPreferences,
    content: @Composable () -> Unit
) {
    val isDark = when (preferences.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AMOLED -> true
    }

    val isAmoled = preferences.themeMode == ThemeMode.AMOLED

    val primaryColorRaw = if (preferences.colorPreset == PrimaryColorPreset.CUSTOM) {
        Color(preferences.customColorHex)
    } else {
        Color(preferences.colorPreset.colorValue)
    }

    val animatedPrimary by animateColorAsState(
        targetValue = primaryColorRaw,
        animationSpec = tween(durationMillis = 350),
        label = "primary_color_anim"
    )

    val secondaryColor = Color(
        red = (animatedPrimary.red * 0.85f + 0.15f).coerceIn(0f, 1f),
        green = (animatedPrimary.green * 0.8f).coerceIn(0f, 1f),
        blue = (animatedPrimary.blue * 1.1f).coerceIn(0f, 1f)
    )

    val colorScheme = if (isDark) {
        if (isAmoled) {
            darkColorScheme(
                primary = animatedPrimary,
                onPrimary = Color.White,
                primaryContainer = animatedPrimary.copy(alpha = 0.25f),
                onPrimaryContainer = Color.White,
                secondary = secondaryColor,
                onSecondary = Color.White,
                background = Color(0xFF000000),
                onBackground = Color(0xFFF9FAFB),
                surface = Color(0xFF080C14),
                onSurface = Color(0xFFF9FAFB),
                surfaceVariant = Color(0xFF101624),
                onSurfaceVariant = Color(0xFF9CA3AF),
                outline = Color(0xFF1F2937),
                outlineVariant = Color(0xFF111827),
                error = DangerRed,
                onError = Color.White
            )
        } else {
            darkColorScheme(
                primary = animatedPrimary,
                onPrimary = Color.White,
                primaryContainer = animatedPrimary.copy(alpha = 0.2f),
                onPrimaryContainer = Color.White,
                secondary = secondaryColor,
                onSecondary = Color.White,
                background = DarkBackground,
                onBackground = DarkTextPrimary,
                surface = DarkSurface,
                onSurface = DarkTextPrimary,
                surfaceVariant = DarkSurfaceElevated,
                onSurfaceVariant = DarkTextSecondary,
                outline = DarkBorder,
                outlineVariant = DarkBorder.copy(alpha = 0.5f),
                error = DangerRed,
                onError = Color.White
            )
        }
    } else {
        lightColorScheme(
            primary = animatedPrimary,
            onPrimary = Color.White,
            primaryContainer = animatedPrimary.copy(alpha = 0.12f),
            onPrimaryContainer = animatedPrimary,
            secondary = secondaryColor,
            onSecondary = Color.White,
            background = LightBackground,
            onBackground = LightTextPrimary,
            surface = LightSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = LightSurfaceElevated,
            onSurfaceVariant = LightTextSecondary,
            outline = LightBorder,
            outlineVariant = LightBorder.copy(alpha = 0.5f),
            error = DangerRed,
            onError = Color.White
        )
    }

    val typography = getAppTypography(preferences.fontScale, preferences.fontFamily)
    val shapes = getAppShapes(preferences.cardStyle)
    val strings = AppStringsProvider.getStrings(preferences.language)
    val layoutDirection = if (preferences.language == AppLanguage.AR) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}
