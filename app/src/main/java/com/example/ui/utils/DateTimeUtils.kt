package com.example.ui.utils

import com.example.data.model.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    fun getTodayStartMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getTomorrowStartMillis(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getThisWeekEndMillis(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 7)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun normalizeToStartOfDay(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun formatDateDisplay(dateMillis: Long, isArabic: Boolean): String {
        val today = getTodayStartMillis()
        val tomorrow = getTomorrowStartMillis()
        val dayAfterTomorrow = tomorrow + 24 * 60 * 60 * 1000L
        val yesterday = today - 24 * 60 * 60 * 1000L

        val targetStart = normalizeToStartOfDay(dateMillis)

        return when (targetStart) {
            today -> if (isArabic) "اليوم" else "Today"
            tomorrow -> if (isArabic) "غداً" else "Tomorrow"
            yesterday -> if (isArabic) "أمس" else "Yesterday"
            dayAfterTomorrow -> if (isArabic) "بعد غد" else "In 2 days"
            else -> {
                val locale = if (isArabic) Locale("ar") else Locale.ENGLISH
                val formatter = SimpleDateFormat("EEE، d MMM", locale)
                formatter.format(Date(dateMillis))
            }
        }
    }

    fun formatFullDate(dateMillis: Long, isArabic: Boolean): String {
        val locale = if (isArabic) Locale("ar") else Locale.ENGLISH
        val formatter = SimpleDateFormat("EEEE، d MMMM yyyy", locale)
        return formatter.format(Date(dateMillis))
    }

    fun formatTimeDisplay(hour: Int, minute: Int, isArabic: Boolean): String {
        if (hour < 0 || minute < 0) return if (isArabic) "طوال اليوم" else "All day"

        val isPm = hour >= 12
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val formattedMin = String.format(Locale.ENGLISH, "%02d", minute)
        val amPm = if (isArabic) {
            if (isPm) "م" else "ص"
        } else {
            if (isPm) "PM" else "AM"
        }
        return "$displayHour:$formattedMin $amPm"
    }

    fun getTimeRemainingString(task: TaskEntity, isArabic: Boolean): String {
        val targetMillis = if (task.timeHour in 0..23 && task.timeMinute in 0..59) {
            Calendar.getInstance().apply {
                timeInMillis = task.date
                set(Calendar.HOUR_OF_DAY, task.timeHour)
                set(Calendar.MINUTE, task.timeMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } else {
            Calendar.getInstance().apply {
                timeInMillis = task.date
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        val diffMillis = targetMillis - System.currentTimeMillis()
        if (diffMillis < 0) {
            return if (isArabic) "متأخرة" else "Overdue"
        }

        val minutes = diffMillis / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 60 -> if (isArabic) "بعد $minutes دقيقة" else "In $minutes min"
            hours < 24 -> if (isArabic) "بعد $hours ساعة" else "In $hours hrs"
            days == 1L -> if (isArabic) "غداً" else "Tomorrow"
            days < 7 -> if (isArabic) "بعد $days أيام" else "In $days days"
            else -> formatDateDisplay(task.date, isArabic)
        }
    }

    fun isOverdue(task: TaskEntity): Boolean {
        if (task.isCompleted) return false
        val today = getTodayStartMillis()
        val targetDay = normalizeToStartOfDay(task.date)
        if (targetDay < today) return true
        if (targetDay == today && task.timeHour in 0..23) {
            val nowCal = Calendar.getInstance()
            val nowHour = nowCal.get(Calendar.HOUR_OF_DAY)
            val nowMin = nowCal.get(Calendar.MINUTE)
            if (nowHour > task.timeHour || (nowHour == task.timeHour && nowMin > task.timeMinute)) {
                return true
            }
        }
        return false
    }
}
