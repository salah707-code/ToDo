package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.getPrimaryGradient

enum class NavDestination {
    HOME,
    MY_TASKS,
    CALENDAR,
    REMINDERS,
    SETTINGS
}

data class NavItem(
    val destination: NavDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val titleAr: String,
    val titleEn: String
)

@Composable
fun EnjazBottomNavigation(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    var isFabPressed by remember { mutableStateOf(false) }
    val fabScale by animateFloatAsState(
        targetValue = if (isFabPressed) 0.9f else 1f,
        animationSpec = spring(),
        label = "fab_scale_anim"
    )

    val navItems = listOf(
        NavItem(NavDestination.HOME, Icons.Filled.Home, Icons.Outlined.Home, strings.navHome, "Home"),
        NavItem(NavDestination.MY_TASKS, Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, strings.navMyTasks, "Tasks"),
        NavItem(NavDestination.CALENDAR, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, strings.navCalendar, "Calendar"),
        NavItem(NavDestination.REMINDERS, Icons.Filled.Notifications, Icons.Outlined.Notifications, strings.navReminders, "Reminders"),
        NavItem(NavDestination.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings, strings.navSettings, "Settings")
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            ),
        color = Color(0xFF151B2D),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = currentDestination == item.destination
                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                    val title = if (isArabic) item.titleAr else item.titleEn

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(item.destination) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("nav_item_${item.destination.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) primaryColor.copy(alpha = 0.16f) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = if (isSelected) primaryColor else Color(0xFF9CA3AF),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) primaryColor else Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnjazFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(),
        label = "fab_scale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = primaryColor.copy(alpha = 0.4f),
                spotColor = primaryColor.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .border(
                width = 3.dp,
                color = Color(0xFF0B1020),
                shape = CircleShape
            )
            .background(
                Brush.linearGradient(listOf(primaryColor, secondaryColor))
            )
            .clickable {
                onClick()
            }
            .padding(16.dp)
            .testTag("main_add_fab"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = strings.addTask,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
