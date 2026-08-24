package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.EnjazApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val app = context.applicationContext as? EnjazApplication ?: return
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pendingTasks = app.taskRepository.getPendingRemindersDirect()
                    for (task in pendingTasks) {
                        app.reminderScheduler.scheduleTaskReminder(task)
                    }
                } catch (e: Exception) {
                    // Non-fatal
                }
            }
        }
    }
}
