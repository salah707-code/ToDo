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
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tonality
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.CardBorderStyle
import com.example.data.preferences.CardCornerStyle
import com.example.data.preferences.CardLayoutStyle
import com.example.data.preferences.CardShadowStyle
import com.example.data.preferences.FontScaleSetting
import com.example.data.preferences.IconThemeStyle
import com.example.data.preferences.NotificationTone
import com.example.data.preferences.PrimaryColorPreset
import com.example.data.preferences.ThemeMode
import com.example.data.preferences.UserPreferences
import com.example.reminder.SoundPlayerUtils
import com.example.ui.components.ColorPaletteGrid
import com.example.ui.components.ColorPickerDialog
import com.example.ui.localization.LocalAppStrings
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
            contentPadding = PaddingValues(bottom = 50.dp)
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

                    Text(
                        text = strings.customization,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Live Interactive Preview Card
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = if (isArabic) "معاينة حية للمظهر والبطاقة" else "Live Appearance & Card Preview",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val previewShape = RoundedCornerShape(preferences.cardStyle.cornerRadiusDp.dp)
                    val previewBorder = when (preferences.cardBorderStyle) {
                        CardBorderStyle.NONE -> Modifier
                        CardBorderStyle.SUBTLE_LINE -> Modifier.border(0.8.dp, Color(0xFF2E3856), previewShape)
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151B2D))
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
                                    .size(26.dp)
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
                                    text = if (isArabic) "مراجعة تقرير المشروع الشهري" else "Review Monthly Project Report",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF9FAFB)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
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
                                        text = if (isArabic) "عمل — اليوم 4:00 م" else "Work — Today 4:00 PM",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 1. Card Layout Format (Standard vs Large Grid vs Compact List)
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

            // 2. Primary Color Preset Section & Color Picker
            item {
                SectionContainer(
                    title = strings.primaryColor,
                    icon = Icons.Default.ColorLens,
                    primaryColor = primaryColor
                ) {
                    Column {
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
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(col)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(10.dp)
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
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom color button via Color Box Dialog
                        TextButton(
                            onClick = { showCustomColorDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = if (isArabic) "+ فتح مربع الألوان الكامل" else "+ Open Color Picker Box",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Card Depth & Shadowing
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

            // 4. Card Border Styling & Glow
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

            // 5. Custom Notification Sound & Volume (Separate from system)
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

            // 6. Icon Styling (Monochrome vs Colored)
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

            // 7. Theme Mode (Dark / Light / System)
            item {
                SectionContainer(
                    title = strings.themeMode,
                    icon = Icons.Default.Tonality,
                    primaryColor = primaryColor
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isSelected = preferences.themeMode == mode
                            val title = if (isArabic) mode.titleAr else mode.titleEn

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.setThemeMode(mode) }
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

            // 8. Font Scale Section
            item {
                SectionContainer(
                    title = strings.fontSize,
                    icon = Icons.Default.FormatSize,
                    primaryColor = primaryColor
                ) {
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

            // 9. Card Corner Radius
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
