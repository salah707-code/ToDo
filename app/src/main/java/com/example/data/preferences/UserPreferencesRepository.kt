package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "enjaz_user_prefs")

enum class ThemeMode(val titleAr: String, val titleEn: String) {
    SYSTEM("تلقائي (حسب النظام)", "System Default"),
    LIGHT("فاتح", "Light Mode"),
    DARK("داكن", "Dark Mode")
}

enum class PrimaryColorPreset(val colorValue: Long, val nameAr: String, val nameEn: String) {
    INDIGO(0xFF4F46E5, "أزرق نيلي", "Indigo"),
    PURPLE(0xFF7C3AED, "بنفسجي", "Purple"),
    BLUE(0xFF2563EB, "أزرق ملكي", "Blue"),
    GREEN(0xFF10B981, "أخضر زمردي", "Green"),
    ORANGE(0xFFF97316, "برتقالي دافئ", "Orange"),
    RED(0xFFEF4444, "أحمر قرمزي", "Red"),
    CYAN(0xFF06B6D4, "سماوي حديث", "Cyan"),
    CUSTOM(0xFF4F46E5, "مخصص", "Custom")
}

enum class FontScaleSetting(val scaleFactor: Float, val nameAr: String, val nameEn: String) {
    SMALL(0.88f, "صغير", "Small"),
    MEDIUM(1.0f, "متوسط", "Medium"),
    LARGE(1.15f, "كبير", "Large")
}

enum class CardCornerStyle(val cornerRadiusDp: Int, val nameAr: String, val nameEn: String) {
    ROUNDED(24, "مستديرة جداً", "Rounded"),
    MEDIUM(16, "متوسطة", "Medium"),
    SIMPLE(8, "بسيطة", "Simple")
}

enum class AppLanguage(val code: String, val nameAr: String, val nameEn: String) {
    AR("ar", "العربية (RTL)", "Arabic"),
    EN("en", "English (LTR)", "English")
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val colorPreset: PrimaryColorPreset = PrimaryColorPreset.INDIGO,
    val customColorHex: Long = 0xFF4F46E5,
    val fontScale: FontScaleSetting = FontScaleSetting.MEDIUM,
    val cardStyle: CardCornerStyle = CardCornerStyle.ROUNDED,
    val language: AppLanguage = AppLanguage.AR,
    val isLoggedIn: Boolean = false,
    val userId: Long = 0L,
    val userName: String = "",
    val userEmail: String = ""
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val COLOR_PRESET = stringPreferencesKey("color_preset")
        val CUSTOM_COLOR_HEX = longPreferencesKey("custom_color_hex")
        val FONT_SCALE = stringPreferencesKey("font_scale")
        val CARD_STYLE = stringPreferencesKey("card_style")
        val LANGUAGE = stringPreferencesKey("language")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = longPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val themeMode = runCatching {
            ThemeMode.valueOf(prefs[PreferenceKeys.THEME_MODE] ?: ThemeMode.DARK.name)
        }.getOrDefault(ThemeMode.DARK)

        val colorPreset = runCatching {
            PrimaryColorPreset.valueOf(prefs[PreferenceKeys.COLOR_PRESET] ?: PrimaryColorPreset.INDIGO.name)
        }.getOrDefault(PrimaryColorPreset.INDIGO)

        val customColorHex = prefs[PreferenceKeys.CUSTOM_COLOR_HEX] ?: 0xFF4F46E5

        val fontScale = runCatching {
            FontScaleSetting.valueOf(prefs[PreferenceKeys.FONT_SCALE] ?: FontScaleSetting.MEDIUM.name)
        }.getOrDefault(FontScaleSetting.MEDIUM)

        val cardStyle = runCatching {
            CardCornerStyle.valueOf(prefs[PreferenceKeys.CARD_STYLE] ?: CardCornerStyle.ROUNDED.name)
        }.getOrDefault(CardCornerStyle.ROUNDED)

        val language = runCatching {
            AppLanguage.valueOf(prefs[PreferenceKeys.LANGUAGE] ?: AppLanguage.AR.name)
        }.getOrDefault(AppLanguage.AR)

        val isLoggedIn = prefs[PreferenceKeys.IS_LOGGED_IN] ?: false
        val userId = prefs[PreferenceKeys.USER_ID] ?: 0L
        val userName = prefs[PreferenceKeys.USER_NAME] ?: ""
        val userEmail = prefs[PreferenceKeys.USER_EMAIL] ?: ""

        UserPreferences(
            themeMode = themeMode,
            colorPreset = colorPreset,
            customColorHex = customColorHex,
            fontScale = fontScale,
            cardStyle = cardStyle,
            language = language,
            isLoggedIn = isLoggedIn,
            userId = userId,
            userName = userName,
            userEmail = userEmail
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferenceKeys.THEME_MODE] = mode.name }
    }

    suspend fun setColorPreset(preset: PrimaryColorPreset) {
        context.dataStore.edit { it[PreferenceKeys.COLOR_PRESET] = preset.name }
    }

    suspend fun setCustomColor(colorHex: Long) {
        context.dataStore.edit {
            it[PreferenceKeys.COLOR_PRESET] = PrimaryColorPreset.CUSTOM.name
            it[PreferenceKeys.CUSTOM_COLOR_HEX] = colorHex
        }
    }

    suspend fun setFontScale(scale: FontScaleSetting) {
        context.dataStore.edit { it[PreferenceKeys.FONT_SCALE] = scale.name }
    }

    suspend fun setCardStyle(style: CardCornerStyle) {
        context.dataStore.edit { it[PreferenceKeys.CARD_STYLE] = style.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[PreferenceKeys.LANGUAGE] = language.name }
    }

    suspend fun setUserSession(isLoggedIn: Boolean, userId: Long, name: String, email: String) {
        context.dataStore.edit {
            it[PreferenceKeys.IS_LOGGED_IN] = isLoggedIn
            it[PreferenceKeys.USER_ID] = userId
            it[PreferenceKeys.USER_NAME] = name
            it[PreferenceKeys.USER_EMAIL] = email
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[PreferenceKeys.IS_LOGGED_IN] = false
            it[PreferenceKeys.USER_ID] = 0L
            it[PreferenceKeys.USER_NAME] = ""
            it[PreferenceKeys.USER_EMAIL] = ""
        }
    }
}
