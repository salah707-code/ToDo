package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.UserPreferences
import com.example.ui.localization.LocalAppStrings
import com.example.ui.viewmodel.TaskViewModel

@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    preferences: UserPreferences,
    onNavigateToProfile: () -> Unit,
    onNavigateToCustomization: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = strings.navSettings,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Clickable Profile Card to edit profile
            item {
                val avatarColor = Color(preferences.userAvatarColor)
                val avatarEmoji = AvatarEmojis.getOrElse(preferences.userAvatarIndex) { "👤" }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
                        .clickable { onNavigateToProfile() }
                        .testTag("settings_profile_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.25f))
                                .border(2.dp, avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = avatarEmoji, fontSize = 26.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (preferences.userName.isNotBlank()) preferences.userName else strings.guestUser,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (preferences.userJobTitle.isNotBlank()) {
                                Text(
                                    text = preferences.userJobTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = primaryColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = if (preferences.userEmail.isNotBlank()) preferences.userEmail else "user@enjaz.app",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Menu Items Section
            item {
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsRowItem(
                            icon = Icons.Default.Person,
                            title = strings.userProfile,
                            subtitle = if (isArabic) "تعديل الاسم، الصورة، الهاتف، وتغيير كلمة المرور" else "Edit name, avatar, phone, and password",
                            iconColor = primaryColor,
                            onClick = onNavigateToProfile,
                            testTag = "settings_profile_row"
                        )

                        SettingsDivider()

                        SettingsRowItem(
                            icon = Icons.Default.Palette,
                            title = strings.customization,
                            subtitle = if (isArabic) "الألوان، المظهر، تنسيق البطاقات، وأصوات التنبيه" else "Colors, theme, card layout, and alert sounds",
                            iconColor = secondaryColor,
                            onClick = onNavigateToCustomization,
                            testTag = "settings_customization_row"
                        )

                        SettingsDivider()

                        SettingsRowItem(
                            icon = Icons.Default.Category,
                            title = strings.manageCategories,
                            subtitle = if (isArabic) "إضافة وتعديل التصنيفات المخصصة" else "Create and manage custom categories",
                            iconColor = Color(0xFF06B6D4),
                            onClick = onNavigateToCategories,
                            testTag = "settings_categories_row"
                        )

                        SettingsDivider()

                        // Language Toggle
                        SettingsRowItem(
                            icon = Icons.Default.Language,
                            title = strings.language,
                            subtitle = if (preferences.language == AppLanguage.AR) "العربية (Arabic)" else "English (الإنجليزية)",
                            iconColor = Color(0xFF10B981),
                            onClick = {
                                val nextLang = if (preferences.language == AppLanguage.AR) AppLanguage.EN else AppLanguage.AR
                                viewModel.setLanguage(nextLang)
                            },
                            testTag = "settings_language_row"
                        )

                        SettingsDivider()

                        SettingsRowItem(
                            icon = Icons.Default.Info,
                            title = strings.aboutApp,
                            subtitle = strings.version,
                            iconColor = Color(0xFFF59E0B),
                            onClick = onNavigateToAbout,
                            testTag = "settings_about_row"
                        )

                        SettingsDivider()

                        SettingsRowItem(
                            icon = Icons.Default.Logout,
                            title = strings.logout,
                            subtitle = if (isArabic) "تسجيل الخروج من الحساب الحالي" else "Sign out from current session",
                            iconColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            testTag = "settings_logout_row"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}
