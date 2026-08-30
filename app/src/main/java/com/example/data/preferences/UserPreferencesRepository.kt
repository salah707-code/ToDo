package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "enjaz_user_prefs")

enum class ThemeMode(val titleAr: String, val titleEn: String) {
    SYSTEM("تلقائي (حسب النظام)", "System Default"),
    LIGHT("فاتح ناصع", "Light Mode"),
    DARK("داكن حديث", "Dark Mode"),
    AMOLED("أسود نقي (AMOLED فائق التوفير)", "Pure Black AMOLED")
}

enum class PrimaryColorPreset(val colorValue: Long, val nameAr: String, val nameEn: String) {
    INDIGO(0xFF4F46E5, "أزرق نيلي حديث", "Indigo"),
    PURPLE(0xFF7C3AED, "بنفسجي ملكي", "Royal Purple"),
    BLUE(0xFF2563EB, "أزرق محيطي", "Ocean Blue"),
    GREEN(0xFF10B981, "أخضر زمردي", "Emerald Green"),
    TEAL(0xFF0D9488, "سماوي بترولي (تيل)", "Teal Ocean"),
    ORANGE(0xFFF97316, "برتقالي شروق دافئ", "Sunset Orange"),
    AMBER(0xFFF59E0B, "عنبري ذهبي متألق", "Amber Gold"),
    RED(0xFFEF4444, "أحمر قرمزي جريء", "Crimson Red"),
    ROSE(0xFFE11D48, "وردي ياقوتي أنيق", "Rose Pink"),
    CYAN(0xFF06B6D4, "تركواز نيون مائي", "Neon Cyan"),
    FUCHSIA(0xFFD946EF, "فوشيا حيوي فاقع", "Vibrant Fuchsia"),
    LIME(0xFF84CC16, "أخضر ليموني نابض", "Lime Energy"),
    SLATE(0xFF475569, "رمادي فحمي هادئ", "Slate Gray"),
    GOLD(0xFFD97706, "ذهبي ملكي فاخر", "Luxury Gold"),
    CUSTOM(0xFF4F46E5, "لون مخصص (منتقي الألوان)", "Custom Color")
}

enum class FontFamilySetting(val titleAr: String, val titleEn: String, val previewAr: String, val previewEn: String) {
    DEFAULT("الخط القياسي (النظام)", "System Default", "إنجاز مهامك اليومية بذكاء", "Smart Task Management"),
    SANS_SERIF("سان سيريف حديث وواضح", "Modern Sans-Serif", "تصميم عصري بخط واضح وجميل", "Clean, modern and readable typography"),
    SERIF("سيريف كلاسيكي رسمي", "Classic Serif", "أناقة الحروف الكلاسيكية الفخمة", "Timeless, elegant serif typography"),
    MONOSPACE("تقني متناسق (Monospace)", "Technical Monospace", "أرقام ونصوص متناسقة هندسياً", "Code & tech aesthetic monospaced"),
    CURSIVE("فني انسيابي (Cursive)", "Artistic Flow", "لمسة فنية مميزة وانسيابية", "Creative flowing handwritten style")
}

enum class CardLayoutStyle(val titleAr: String, val titleEn: String) {
    STANDARD("بطاقات قياسية", "Standard Cards"),
    LARGE_GRID("مربعات كبيرة (شبكة)", "Large Squares (Grid)"),
    COMPACT_LIST("قائمة سريعة", "Compact List")
}

enum class CardShadowStyle(val titleAr: String, val titleEn: String, val elevationDp: Int) {
    NONE("مسطح (بدون ظل)", "Flat (No Shadow)", 0),
    SUBTLE("ظل خفيف", "Subtle Shadow", 4),
    MEDIUM("ظل متوسط", "Medium Shadow", 8),
    GLOWING("توهج وظلال عريضة", "Glow & Deep Shadow", 16)
}

enum class CardBorderStyle(val titleAr: String, val titleEn: String, val widthDp: Float) {
    NONE("بدون حواف", "No Border", 0f),
    SUBTLE_LINE("خط خفيف (0.8dp)", "Subtle Line (0.8dp)", 0.8f),
    COLORED_BORDER("حواف ملونة بلون البطاقة", "Colored Card Border", 1.5f),
    GLOW_BORDER("حواف متوهجة وبارزة", "Glowing Prominent Border", 2.2f)
}

enum class NotificationTone(val titleAr: String, val titleEn: String) {
    CHIME_ALERT("رنين إنجاز الحديث", "Modern Enjaz Chime"),
    GENTLE_BELL("أجراس هادئة", "Gentle Bell"),
    DIGITAL_PULSE("نبض رقمي متطور", "Digital Pulse"),
    CRYSTAL_DROP("قطرة نقية", "Crystal Drop"),
    SYSTEM_DEFAULT("صوت النظام الافتراضي", "System Default"),
    MUTE("صامت (بدون صوت)", "Mute (No Sound)")
}

enum class IconThemeStyle(val titleAr: String, val titleEn: String) {
    COLORED_EMOJI("أيقونات وإيموجي ملونة", "Colored Emoji Icons"),
    MONOCHROME_LINE("أيقونات راقية غير ملونة (أحادية)", "Elegant Monochrome Icons")
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
    val fontFamily: FontFamilySetting = FontFamilySetting.DEFAULT,
    val cardStyle: CardCornerStyle = CardCornerStyle.ROUNDED,
    val cardLayoutStyle: CardLayoutStyle = CardLayoutStyle.STANDARD,
    val cardShadowStyle: CardShadowStyle = CardShadowStyle.MEDIUM,
    val cardBorderStyle: CardBorderStyle = CardBorderStyle.SUBTLE_LINE,
    val notificationTone: NotificationTone = NotificationTone.CHIME_ALERT,
    val notificationVolume: Float = 0.85f,
    val iconThemeStyle: IconThemeStyle = IconThemeStyle.COLORED_EMOJI,
    val language: AppLanguage = AppLanguage.AR,
    val isLoggedIn: Boolean = false,
    val userId: Long = 0L,
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userAddress: String = "",
    val userJobTitle: String = "",
    val userAvatarIndex: Int = 0,
    val userAvatarColor: Long = 0xFF4F46E5
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val COLOR_PRESET = stringPreferencesKey("color_preset")
        val CUSTOM_COLOR_HEX = longPreferencesKey("custom_color_hex")
        val FONT_SCALE = stringPreferencesKey("font_scale")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val CARD_STYLE = stringPreferencesKey("card_style")
        val CARD_LAYOUT_STYLE = stringPreferencesKey("card_layout_style")
        val CARD_SHADOW_STYLE = stringPreferencesKey("card_shadow_style")
        val CARD_BORDER_STYLE = stringPreferencesKey("card_border_style")
        val NOTIFICATION_TONE = stringPreferencesKey("notification_tone")
        val NOTIFICATION_VOLUME = floatPreferencesKey("notification_volume")
        val ICON_THEME_STYLE = stringPreferencesKey("icon_theme_style")
        val LANGUAGE = stringPreferencesKey("language")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = longPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_ADDRESS = stringPreferencesKey("user_address")
        val USER_JOB_TITLE = stringPreferencesKey("user_job_title")
        val USER_AVATAR_INDEX = intPreferencesKey("user_avatar_index")
        val USER_AVATAR_COLOR = longPreferencesKey("user_avatar_color")
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

        val fontFamily = runCatching {
            FontFamilySetting.valueOf(prefs[PreferenceKeys.FONT_FAMILY] ?: FontFamilySetting.DEFAULT.name)
        }.getOrDefault(FontFamilySetting.DEFAULT)

        val cardStyle = runCatching {
            CardCornerStyle.valueOf(prefs[PreferenceKeys.CARD_STYLE] ?: CardCornerStyle.ROUNDED.name)
        }.getOrDefault(CardCornerStyle.ROUNDED)

        val cardLayoutStyle = runCatching {
            CardLayoutStyle.valueOf(prefs[PreferenceKeys.CARD_LAYOUT_STYLE] ?: CardLayoutStyle.STANDARD.name)
        }.getOrDefault(CardLayoutStyle.STANDARD)

        val cardShadowStyle = runCatching {
            CardShadowStyle.valueOf(prefs[PreferenceKeys.CARD_SHADOW_STYLE] ?: CardShadowStyle.MEDIUM.name)
        }.getOrDefault(CardShadowStyle.MEDIUM)

        val cardBorderStyle = runCatching {
            CardBorderStyle.valueOf(prefs[PreferenceKeys.CARD_BORDER_STYLE] ?: CardBorderStyle.SUBTLE_LINE.name)
        }.getOrDefault(CardBorderStyle.SUBTLE_LINE)

        val notificationTone = runCatching {
            NotificationTone.valueOf(prefs[PreferenceKeys.NOTIFICATION_TONE] ?: NotificationTone.CHIME_ALERT.name)
        }.getOrDefault(NotificationTone.CHIME_ALERT)

        val notificationVolume = prefs[PreferenceKeys.NOTIFICATION_VOLUME] ?: 0.85f

        val iconThemeStyle = runCatching {
            IconThemeStyle.valueOf(prefs[PreferenceKeys.ICON_THEME_STYLE] ?: IconThemeStyle.COLORED_EMOJI.name)
        }.getOrDefault(IconThemeStyle.COLORED_EMOJI)

        val language = runCatching {
            AppLanguage.valueOf(prefs[PreferenceKeys.LANGUAGE] ?: AppLanguage.AR.name)
        }.getOrDefault(AppLanguage.AR)

        val isLoggedIn = prefs[PreferenceKeys.IS_LOGGED_IN] ?: false
        val userId = prefs[PreferenceKeys.USER_ID] ?: 0L
        val userName = prefs[PreferenceKeys.USER_NAME] ?: ""
        val userEmail = prefs[PreferenceKeys.USER_EMAIL] ?: ""
        val userPhone = prefs[PreferenceKeys.USER_PHONE] ?: ""
        val userAddress = prefs[PreferenceKeys.USER_ADDRESS] ?: ""
        val userJobTitle = prefs[PreferenceKeys.USER_JOB_TITLE] ?: ""
        val userAvatarIndex = prefs[PreferenceKeys.USER_AVATAR_INDEX] ?: 0
        val userAvatarColor = prefs[PreferenceKeys.USER_AVATAR_COLOR] ?: 0xFF4F46E5

        UserPreferences(
            themeMode = themeMode,
            colorPreset = colorPreset,
            customColorHex = customColorHex,
            fontScale = fontScale,
            fontFamily = fontFamily,
            cardStyle = cardStyle,
            cardLayoutStyle = cardLayoutStyle,
            cardShadowStyle = cardShadowStyle,
            cardBorderStyle = cardBorderStyle,
            notificationTone = notificationTone,
            notificationVolume = notificationVolume,
            iconThemeStyle = iconThemeStyle,
            language = language,
            isLoggedIn = isLoggedIn,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            userPhone = userPhone,
            userAddress = userAddress,
            userJobTitle = userJobTitle,
            userAvatarIndex = userAvatarIndex,
            userAvatarColor = userAvatarColor
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

    suspend fun setFontFamily(family: FontFamilySetting) {
        context.dataStore.edit { it[PreferenceKeys.FONT_FAMILY] = family.name }
    }

    suspend fun setCardStyle(style: CardCornerStyle) {
        context.dataStore.edit { it[PreferenceKeys.CARD_STYLE] = style.name }
    }

    suspend fun setCardLayoutStyle(layout: CardLayoutStyle) {
        context.dataStore.edit { it[PreferenceKeys.CARD_LAYOUT_STYLE] = layout.name }
    }

    suspend fun setCardShadowStyle(shadow: CardShadowStyle) {
        context.dataStore.edit { it[PreferenceKeys.CARD_SHADOW_STYLE] = shadow.name }
    }

    suspend fun setCardBorderStyle(border: CardBorderStyle) {
        context.dataStore.edit { it[PreferenceKeys.CARD_BORDER_STYLE] = border.name }
    }

    suspend fun setNotificationTone(tone: NotificationTone) {
        context.dataStore.edit { it[PreferenceKeys.NOTIFICATION_TONE] = tone.name }
    }

    suspend fun setNotificationVolume(volume: Float) {
        context.dataStore.edit { it[PreferenceKeys.NOTIFICATION_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setIconThemeStyle(iconTheme: IconThemeStyle) {
        context.dataStore.edit { it[PreferenceKeys.ICON_THEME_STYLE] = iconTheme.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[PreferenceKeys.LANGUAGE] = language.name }
    }

    suspend fun setUserSession(
        isLoggedIn: Boolean,
        userId: Long,
        name: String,
        email: String,
        phone: String = "",
        address: String = "",
        jobTitle: String = "",
        avatarIndex: Int = 0,
        avatarColor: Long = 0xFF4F46E5
    ) {
        context.dataStore.edit {
            it[PreferenceKeys.IS_LOGGED_IN] = isLoggedIn
            it[PreferenceKeys.USER_ID] = userId
            it[PreferenceKeys.USER_NAME] = name
            it[PreferenceKeys.USER_EMAIL] = email
            it[PreferenceKeys.USER_PHONE] = phone
            it[PreferenceKeys.USER_ADDRESS] = address
            it[PreferenceKeys.USER_JOB_TITLE] = jobTitle
            it[PreferenceKeys.USER_AVATAR_INDEX] = avatarIndex
            it[PreferenceKeys.USER_AVATAR_COLOR] = avatarColor
        }
    }

    suspend fun updateProfileCache(
        name: String,
        phone: String,
        address: String,
        jobTitle: String,
        avatarIndex: Int,
        avatarColor: Long
    ) {
        context.dataStore.edit {
            it[PreferenceKeys.USER_NAME] = name
            it[PreferenceKeys.USER_PHONE] = phone
            it[PreferenceKeys.USER_ADDRESS] = address
            it[PreferenceKeys.USER_JOB_TITLE] = jobTitle
            it[PreferenceKeys.USER_AVATAR_INDEX] = avatarIndex
            it[PreferenceKeys.USER_AVATAR_COLOR] = avatarColor
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[PreferenceKeys.IS_LOGGED_IN] = false
            it[PreferenceKeys.USER_ID] = 0L
            it[PreferenceKeys.USER_NAME] = ""
            it[PreferenceKeys.USER_EMAIL] = ""
            it[PreferenceKeys.USER_PHONE] = ""
            it[PreferenceKeys.USER_ADDRESS] = ""
            it[PreferenceKeys.USER_JOB_TITLE] = ""
            it[PreferenceKeys.USER_AVATAR_INDEX] = 0
            it[PreferenceKeys.USER_AVATAR_COLOR] = 0xFF4F46E5
        }
    }
}
