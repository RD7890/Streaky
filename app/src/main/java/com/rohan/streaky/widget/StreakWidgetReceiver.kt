package com.rohan.streaky.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.rohan.streaky.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        refreshWidgetData(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        refreshWidgetData(context)
    }

    private fun refreshWidgetData(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    appContext,
                    WidgetEntryPoint::class.java
                )
                val habitDao = entryPoint.habitDao()
                val habits   = habitDao.getAllHabits().first()
                val habit    = habits.firstOrNull()

                val todayEpoch = LocalDate.now().toEpochDay()
                val isDone = if (habit != null) {
                    entryPoint.completionDao()
                        .getCompletionForDay(habit.id, todayEpoch) != null
                } else false

                val glanceManager = GlanceAppWidgetManager(appContext)
                val glanceIds = glanceManager.getGlanceIds(StreakWidget::class.java)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(appContext, glanceId) { prefs ->
                        prefs[StreakWidget.PREF_HABIT_NAME] = habit?.name ?: "Add a habit"
                        prefs[StreakWidget.PREF_STREAK]     = habit?.currentStreak ?: 0
                        prefs[StreakWidget.PREF_DONE]       = isDone
                    }
                    glanceAppWidget.update(appContext, glanceId)
                }
            } catch (e: Exception) {
                // Widget shows default empty state on error — not a fatal crash
            }
        }
    }
}
