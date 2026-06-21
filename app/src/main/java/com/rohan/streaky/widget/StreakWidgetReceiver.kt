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

class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java
                )
                val habits = entryPoint.habitDao().getAllHabits().first()
                val glanceIds = GlanceAppWidgetManager(context)
                    .getGlanceIds(StreakWidget::class.java)
                val habit = habits.firstOrNull()
                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[StreakWidget.PREF_HABIT_NAME] = habit?.name ?: "Add a habit"
                        prefs[StreakWidget.PREF_STREAK]     = habit?.currentStreak ?: 0
                        prefs[StreakWidget.PREF_DONE]       = false
                    }
                    glanceAppWidget.update(context, glanceId)
                }
            } catch (e: Exception) {
                // Widget will show default empty state
            }
        }
    }
}
