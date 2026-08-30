package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tonality
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.CardBorderStyle
import com.example.data.preferences.CardCornerStyle
import com.example.data.preferences.CardLayoutStyle
import com.example.data.preferences.CardShadowStyle
import com.example.data.preferences.FontFamilySetting
import com.example.data.preferences.FontScaleSetting
import com.example.data.preferences.IconThemeStyle
import com.example.data.preferences.NotificationTone
import com.example.data.preferences.PrimaryColorPreset
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferences
import com.example.reminder.SoundPlayerUtils
import com.example.ui.components.ColorPickerDialog
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.toComposeFontFamily
import com.example.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    viewModel: TaskViewModel,
    preferences: UserPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val primaryColor = MaterialTheme.colorScheme.primary

    var showCustomColorDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("customization_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("customization_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = strings.customization,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isArabic) "خصص ألوان السمة، نوع الخط، والمظهر العام" else "Customize theme colors, font styles & appearance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Live Interactive Preview Card (Reflects Color + Font + Shape)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = if (isArabic) "معاينة حية ومباشرة للسمة والخط" else "Live Theme & Font Preview",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val previewShape = RoundedCornerShape(preferences.cardStyle.cornerRadiusDp.dp)
                    val previewBorder = when (preferences.cardBorderStyle) {
                        CardBorderStyle.NONE -> Modifier
                        CardBorderStyle.SUBTLE_LINE -> Modifier.border(0.8.dp, MaterialTheme.colorScheme.outline, previewShape)
                        CardBorderStyle.COLORED_BORDER -> Modifier.border(1.5.dp, primaryColor.copy(alpha = 0.75f), previewShape)
                        CardBorderStyle.GLOW_BORDER -> Modifier.border(
                            2.dp,
                            Brush.linearGradient(listOf(primaryColor, primaryColor.copy(alpha = 0.3f))),
                            previewShape
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = preferences.cardShadowStyle.elevationDp.dp,
                                shape = previewShape,
                                spotColor = primaryColor.copy(alpha = 0.4f)
                            )
                            .then(previewBorder),
                        shape = previewShape,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "مراجعة تقرير المشروع والتصميم" else "Review Project & Design Progress",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (preferences.iconThemeStyle == IconThemeStyle.COLORED_EMOJI) {
                                        Text("💼", fontSize = 12.sp)
                                    } else {
                                        Icon(Icons.Default.Style, contentDescription = null, tint = primaryColor, modifier = Modifier.size(13.dp))
                                    }
                                    Text(
                                        text = if (isArabic) "أولوية عالية 🔴 — اليوم 4:00 م" else "High Priority 🔴 — Today 4:00 PM",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = primaryColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isArabic) preferences.fontFamily.titleAr else preferences.fontFamily.titleEn,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 1: Theme Mode (Dark / Light / AMOLED / System)
            item {
                SectionContainer(
                    title = strings.themeMode,
                    icon = Icons.Default.Tonality,
                    primaryColor = primaryColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeMode.values().forEach { mode ->
                                val isSelected = preferences.themeMode == mode
                                val title = when (mode) {
                                    ThemeMode.SYSTEM -> if (isArabic) "تلقائي" else "System"
                                    ThemeMode.LIGHT -> if (isArabic) "فاتح" else "Light"
                                    ThemeMode.DARK -> if (isArabic) "داكن" else "Dark"
                                    ThemeMode.AMOLED -> if (isArabic) "AMOLED" else "AMOLED"
                                }

                                val icon = when (mode) {
                                    ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                    ThemeMode.LIGHT -> Icons.Default.LightMode
                                    ThemeMode.DARK -> Icons.Default.DarkMode
                                    ThemeMode.AMOLED -> Icons.Default.Tonality
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.setThemeMode(mode) }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: App Primary Theme Colors (ألوان سمة التطبيق)
            item {
                SectionContainer(
                    title = strings.primaryColor,
                    icon = Icons.Default.ColorLens,
                    primaryColor = primaryColor
                ) {
                    Column {
                        // Current Color Badge
                        val activeColorName = if (preferences.colorPreset == PrimaryColorPreset.CUSTOM) {
                            if (isArabic) "لون مخصص" else "Custom Color"
                        } else {
                            if (isArabic) preferences.colorPreset.nameAr else preferences.colorPreset.nameEn
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor)
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                )
                                Text(
                                    text = activeColorName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            TextButton(
                                onClick = { showCustomColorDialog = true }
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp), tint = primaryColor)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "منتقي الألوان +" else "+ Custom Color",
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        // Presets Palette FlowRow
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PrimaryColorPreset.values().filter { it != PrimaryColorPreset.CUSTOM }.forEach { preset ->
                                val isSelected = preferences.colorPreset == preset
                                val col = Color(preset.colorValue)

                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(col)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.setColorPreset(preset) }
                                        .testTag("color_${preset.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 3: Font Family Style Selection (تغيير نوع الخط)
            item {
                SectionContainer(
                    title = strings.fontFamily,
                    icon = Icons.Default.TextFields,
                    primaryColor = primaryColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isArabic) "اختر نوع الخط المفضل لتطبيقه على كافة شاشات ونصوص التطبيق:" else "Choose your preferred font family across the entire app:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        FontFamilySetting.values().forEach { familySetting ->
                            val isSelected = preferences.fontFamily == familySetting
                            val compFont = familySetting.toComposeFontFamily()
                            val title = if (isArabic) familySetting.titleAr else familySetting.titleEn
                            val previewText = if (isArabic) familySetting.previewAr else familySetting.previewEn

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { viewModel.setFontFamily(familySetting) }
                                    .testTag("font_family_${familySetting.name.lowercase()}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(1.8.dp, primaryColor)
                                } else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = title,
                                                style = TextStyle(
                                                    fontFamily = compFont,
                                                    fontSize = 15.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                                ),
                                                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = previewText,
                                            style = TextStyle(
                                                fontFamily = compFont,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal
                                            ),
                                            color = if (isSelected) primaryColor.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(primaryColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: Font Size Scale (حجم الخط)
            item {
                SectionContainer(
                    title = strings.fontSize,
                    icon = Icons.Default.FormatSize,
                    primaryColor = primaryColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FontScaleSetting.values().forEach { fontScale ->
                                val isSelected = preferences.fontScale == fontScale
                                val title = if (isArabic) fontScale.nameAr else fontScale.nameEn

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.setFontScale(fontScale) }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 5: Card Layout Format (Standard vs Large Grid vs Compact List)
            item {
                SectionContainer(
                    title = strings.cardLayout,
                    icon = Icons.Default.GridView,
                    primaryColor = primaryColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CardLayoutStyle.values().forEach { layout ->
                            val isSelected = preferences.cardLayoutStyle == layout
                            val title = if (isArabic) layout.titleAr else layout.titleEn

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.setCardLayoutStyle(layout) }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 6: Card Depth & Shadowing
            item {
                SectionContainer(
                    title = strings.cardShadow,
                    icon = Icons.Default.Layers,
                    primaryColor = primaryColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CardShadowStyle.values().forEach { shadow ->
                            val isSelected = preferences.cardShadowStyle == shadow
                            val title = if (isArabic) shadow.titleAr else shadow.titleEn

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.setCardShadowStyle(shadow) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 7: Card Border Styling & Glow
            item {
                SectionContainer(
                    title = strings.cardBorder,
                    icon = Icons.Default.BorderColor,
                    primaryColor = primaryColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CardBorderStyle.values().forEach { border ->
                            val isSelected = preferences.cardBorderStyle == border
                            val title = if (isArabic) border.titleAr else border.titleEn

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primaryColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) primaryColor else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setCardBorderStyle(border) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 8: Card Corner Radius
            item {
                SectionContainer(
                    title = strings.cardCorners,
                    icon = Icons.Default.RoundedCorner,
                    primaryColor = primaryColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CardCornerStyle.values().forEach { style ->
                            val isSelected = preferences.cardStyle == style
                            val title = if (isArabic) style.nameAr else style.nameEn

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(style.cornerRadiusDp.dp))
                                    .background(
                                        if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.setCardStyle(style) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 9: Custom Notification Sound & Volume
            item {
                SectionContainer(
                    title = strings.notificationSound,
                    icon = Icons.Default.NotificationsActive,
                    primaryColor = primaryColor
                ) {
                    Column {
                        NotificationTone.values().forEach { tone ->
                            val isSelected = preferences.notificationTone == tone
                            val title = if (isArabic) tone.titleAr else tone.titleEn

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        viewModel.setNotificationTone(tone)
                                        SoundPlayerUtils.previewTone(context, tone, preferences.notificationVolume)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                                    }
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (tone != NotificationTone.MUTE) {
                                    IconButton(
                                        onClick = {
                                            SoundPlayerUtils.previewTone(context, tone, preferences.notificationVolume)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Test tone",
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Volume Control Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                            Text(
                                text = "${strings.soundVolume}: ${(preferences.notificationVolume * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Slider(
                            value = preferences.notificationVolume,
                            onValueChange = { viewModel.setNotificationVolume(it) },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = primaryColor,
                                activeTrackColor = primaryColor
                            )
                        )
                    }
                }
            }

            // SECTION 10: Icon Styling (Monochrome vs Colored)
            item {
                SectionContainer(
                    title = strings.iconTheme,
                    icon = Icons.Default.AutoAwesome,
                    primaryColor = primaryColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconThemeStyle.values().forEach { style ->
                            val isSelected = preferences.iconThemeStyle == style
                            val title = if (isArabic) style.titleAr else style.titleEn

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.setIconThemeStyle(style) }
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCustomColorDialog) {
        ColorPickerDialog(
            initialColorHex = preferences.customColorHex,
            onColorSelected = { chosenColor ->
                viewModel.setCustomColor(chosenColor)
            },
            onDismiss = { showCustomColorDialog = false },
            title = strings.primaryColor
        )
    }
}

@Composable
fun SectionContainer(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}
