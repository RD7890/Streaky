package com.rohan.streaky.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import com.rohan.streaky.data.db.dao.HabitDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()

    @Inject lateinit var habitDao: HabitDao

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val habits = habitDao.getAllHabits().first()
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(StreakWidget::class.java)
            habits.firstOrNull()?.let { habit ->
                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[StreakWidget.PREF_HABIT_NAME] = habit.name
                        prefs[StreakWidget.PREF_STREAK]     = habit.currentStreak
                        prefs[StreakWidget.PREF_EMOJI]      = habit.iconEmoji
                        prefs[StreakWidget.PREF_DONE]       = false
                    }
                    glanceAppWidget.update(context, glanceId)
                }
            }
        }
    }
}
