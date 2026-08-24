package com.example.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.EnjazApplication
import com.example.MainActivity
import com.example.R
import com.example.data.model.RepeatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "تذكير بمهمة جديدة"
        val taskCategory = intent.getStringExtra(EXTRA_TASK_CATEGORY) ?: "مهامي"

        if (taskId == -1L) return

        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TASK_ID", taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, EnjazApplication.CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.app_launcher_icon)
            .setContentTitle(taskTitle)
            .setContentText("⏰ حان موعد: $taskCategory")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(taskId.toInt(), notification)

        // Check if task repeats, and handle repeat schedule
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as? EnjazApplication ?: return@launch
                val task = app.taskRepository.getTaskByIdDirect(taskId) ?: return@launch
                if (!task.isCompleted && task.repeatType != RepeatType.NONE) {
                    val nextDate = calculateNextRepeatDate(task.date, task.repeatType)
                    val updatedTask = task.copy(date = nextDate, updatedAt = System.currentTimeMillis())
                    app.taskRepository.updateTask(updatedTask)
                    app.reminderScheduler.scheduleTaskReminder(updatedTask)
                }
            } catch (e: Exception) {
                // Non-fatal
            }
        }
    }

    private fun calculateNextRepeatDate(currentDate: Long, repeatType: RepeatType): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = currentDate
        }
        when (repeatType) {
            RepeatType.DAILY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            RepeatType.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            RepeatType.MONTHLY -> cal.add(Calendar.MONTH, 1)
            else -> cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_CATEGORY = "extra_task_category"
        const val EXTRA_TASK_COLOR = "extra_task_color"
    }
}
