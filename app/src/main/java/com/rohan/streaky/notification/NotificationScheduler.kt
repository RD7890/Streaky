package com.rohan.streaky.notification

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(habitId: Long, hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        var target = now.withHour(hour).withMinute(minute).withSecond(0)
        if (!target.isAfter(now)) target = target.plusDays(1)

        val delay = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                System.currentTimeMillis()

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("habitId" to habitId))
            .addTag("habit_reminder_$habitId")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "streak_reminder_$habitId",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancel(habitId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("streak_reminder_$habitId")
    }

    fun scheduleAll() {
        // Re-schedules all reminders (called on boot)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "streak_reminder_global",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<GlobalReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()
        )
    }
}
