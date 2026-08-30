package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReminderType(val minutesBefore: Long, val titleAr: String, val titleEn: String) {
    NONE(0, "بدون تذكير", "No reminder"),
    AT_TIME(0, "وقت المهمة", "At task time"),
    MIN_5(5, "قبل 5 دقائق", "5 minutes before"),
    MIN_15(15, "قبل 15 دقيقة", "15 minutes before"),
    MIN_30(30, "قبل 30 دقيقة", "30 minutes before"),
    HOUR_1(60, "قبل ساعة", "1 hour before"),
    DAY_1(1440, "قبل يوم", "1 day before")
}

enum class RepeatType(val titleAr: String, val titleEn: String) {
    NONE("لا يتكرر", "Does not repeat"),
    DAILY("تكرار يومي", "Daily"),
    WEEKLY("تكرار أسبوعي", "Weekly"),
    MONTHLY("تكرار شهري", "Monthly"),
    CUSTOM_DAYS("أيام محددة", "Specific days")
}

enum class TaskTimeFilter(val titleAr: String, val titleEn: String) {
    TODAY("اليوم", "Today"),
    TOMORROW("غداً", "Tomorrow"),
    THIS_WEEK("هذا الأسبوع", "This Week"),
    LATER("لاحقاً", "Later"),
    ALL("الكل", "All")
}

enum class TaskStatusFilter(val titleAr: String, val titleEn: String) {
    ALL("جميع المهام", "All Tasks"),
    PENDING("غير المكتملة", "Pending"),
    COMPLETED("المكتملة", "Completed"),
    UPCOMING("القادمة", "Upcoming")
}

enum class Priority(val titleAr: String, val titleEn: String, val colorHex: Long, val weight: Int) {
    HIGH("عالية", "High", 0xFFEF4444, 3),
    MEDIUM("متوسطة", "Medium", 0xFFF59E0B, 2),
    LOW("منخفضة", "Low", 0xFF10B981, 1)
}

enum class TaskSortOrder(val titleAr: String, val titleEn: String) {
    DATE_TIME("التاريخ والوقت", "Date & Time"),
    PRIORITY_HIGH_FIRST("الأولوية (الأعلى أولاً)", "Priority (Highest first)"),
    PRIORITY_LOW_FIRST("الأولوية (الأقل أولاً)", "Priority (Lowest first)"),
    ALPHABETICAL("أبجدياً (أ-ي)", "Alphabetical (A-Z)")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "شخصي",
    val categoryIcon: String = "person",
    val categoryColor: Long = 0xFF4F46E5,
    val cardColorHex: Long = 0L, // 0L means follow category/default, or custom card color
    val date: Long, // Epoch day in millis representing 00:00:00 of the target date
    val timeHour: Int = -1, // -1 means no specific time set
    val timeMinute: Int = -1,
    val priority: Priority = Priority.MEDIUM,
    val locationName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val reminderByLocation: Boolean = false,
    val reminderType: ReminderType = ReminderType.NONE,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatDays: String = "", // Comma-separated 1..7 (1=Sunday, 7=Saturday)
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameAr: String,
    val nameEn: String,
    val iconName: String,
    val colorHex: Long,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val displayName: String,
    val phoneNumber: String = "",
    val address: String = "",
    val jobTitle: String = "",
    val avatarIndex: Int = 0,
    val avatarColor: Long = 0xFF4F46E5,
    val passwordHash: String,
    val salt: String,
    val createdAt: Long = System.currentTimeMillis()
)
