package com.rohan.streaky.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohan.streaky.MainActivity
import com.rohan.streaky.R
import com.rohan.streaky.StreakApp
import com.rohan.streaky.data.db.dao.HabitDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val habitDao: HabitDao
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getLong("habitId", -1L)
        if (habitId < 0) return Result.failure()

        val habit = habitDao.getHabitById(habitId).first() ?: return Result.failure()
        sendNotification(habit.name, habit.iconEmoji, habit.currentStreak)
        return Result.success()
    }

    private fun sendNotification(habitName: String, emoji: String, streak: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val streakText = if (streak > 0) "Keep your $streak-day streak alive!" else "Start your streak today!"
        val notification = NotificationCompat.Builder(applicationContext, StreakApp.NOTIF_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$emoji $habitName")
            .setContentText(streakText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$streakText\n🔥 Don't break the chain — check in now!"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        nm.notify(habitName.hashCode(), notification)
    }
}

@HiltWorker
class GlobalReminderWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, StreakApp.NOTIF_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🔥 Don't break the streak!")
            .setContentText("Check in your habits for today")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        nm.notify(9999, notification)
        return Result.success()
    }
}
