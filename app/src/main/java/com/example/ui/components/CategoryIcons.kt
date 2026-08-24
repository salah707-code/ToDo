package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class AvailableIcon(val id: String, val nameAr: String, val nameEn: String, val icon: ImageVector)

object CategoryIcons {

    val allIcons = listOf(
        AvailableIcon("shopping_cart", "تسوق", "Shopping", Icons.Default.ShoppingCart),
        AvailableIcon("work", "عمل", "Work", Icons.Default.Work),
        AvailableIcon("school", "دراسة", "Study", Icons.Default.School),
        AvailableIcon("home", "منزل", "Home", Icons.Default.Home),
        AvailableIcon("medication", "أدوية", "Medicine", Icons.Default.Medication),
        AvailableIcon("account_balance_wallet", "مالية", "Finance", Icons.Default.AccountBalanceWallet),
        AvailableIcon("directions_car", "سيارة", "Car", Icons.Default.DirectionsCar),
        AvailableIcon("fitness_center", "رياضة", "Sports", Icons.Default.FitnessCenter),
        AvailableIcon("restaurant", "طعام", "Food", Icons.Default.Restaurant),
        AvailableIcon("call", "اتصالات", "Calls", Icons.Default.Call),
        AvailableIcon("family_restroom", "عائلة", "Family", Icons.Default.FamilyRestroom),
        AvailableIcon("event", "مواعيد", "Events", Icons.Default.Event),
        AvailableIcon("build", "صيانة", "Maintenance", Icons.Default.Build),
        AvailableIcon("flight", "سفر", "Travel", Icons.Default.Flight),
        AvailableIcon("person", "شخصي", "Personal", Icons.Default.Person),
        AvailableIcon("star", "مميز", "Star", Icons.Default.Star),
        AvailableIcon("favorite", "صحة", "Health", Icons.Default.Favorite),
        AvailableIcon("bookmark", "قراءة", "Reading", Icons.Default.Bookmark),
        AvailableIcon("lightbulb", "أفكار", "Ideas", Icons.Default.Lightbulb),
        AvailableIcon("timer", "مؤقت", "Timer", Icons.Default.Timer),
        AvailableIcon("celebration", "مناسبات", "Celebration", Icons.Default.Celebration),
        AvailableIcon("pets", "حيوانات", "Pets", Icons.Default.Pets)
    )

    fun getIcon(name: String): ImageVector {
        return allIcons.find { it.id == name }?.icon ?: Icons.Default.Bookmark
    }

    fun getEmoji(iconName: String): String {
        return when (iconName) {
            "shopping_cart" -> "🛒"
            "work" -> "💼"
            "school" -> "📚"
            "home" -> "🏠"
            "medication" -> "💊"
            "account_balance_wallet" -> "💰"
            "directions_car" -> "🚗"
            "fitness_center" -> "🏋️"
            "restaurant" -> "🍲"
            "call" -> "📞"
            "family_restroom" -> "👨‍👩‍👧"
            "event" -> "📅"
            "build" -> "🔧"
            "flight" -> "✈️"
            "person" -> "👤"
            "star" -> "⭐"
            "favorite" -> "❤️"
            "bookmark" -> "🔖"
            "lightbulb" -> "💡"
            "timer" -> "⏱️"
            "celebration" -> "🎉"
            "pets" -> "🐾"
            else -> "📌"
        }
    }
}
