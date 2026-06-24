package com.rohan.streaky.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
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
        // Do NOT call super.onUpdate here — we drive the full update ourselves
        // so state is always populated before Glance renders.
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                loadAndSetState(context)
            } catch (_: Exception) {
                setDefaultState(context)
            } finally {
                triggerGlanceUpdate(context)
                pending.finish()
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                loadAndSetState(context)
            } catch (_: Exception) {
                setDefaultState(context)
            } finally {
                triggerGlanceUpdate(context)
                pending.finish()
            }
        }
    }

    private suspend fun loadAndSetState(context: Context) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(appContext, WidgetEntryPoint::class.java)
        val habits     = entryPoint.habitDao().getAllHabits().first()
        val habit      = habits.firstOrNull()
        val todayEpoch = LocalDate.now().toEpochDay()
        val isDone     = habit?.let {
            entryPoint.completionDao().getCompletionForDay(it.id, todayEpoch) != null
        } ?: false

        val manager = GlanceAppWidgetManager(appContext)
        manager.getGlanceIds(StreakWidget::class.java).forEach { id ->
            updateAppWidgetState(appContext, id) { prefs ->
                prefs[StreakWidget.PREF_HABIT_NAME] = habit?.name ?: "Add a habit"
                prefs[StreakWidget.PREF_STREAK]     = habit?.currentStreak ?: 0
                prefs[StreakWidget.PREF_DONE]       = isDone
            }
        }
    }

    private suspend fun setDefaultState(context: Context) {
        val appContext = context.applicationContext
        val manager   = GlanceAppWidgetManager(appContext)
        manager.getGlanceIds(StreakWidget::class.java).forEach { id ->
            updateAppWidgetState(appContext, id) { prefs ->
                prefs[StreakWidget.PREF_HABIT_NAME] = "Streaky"
                prefs[StreakWidget.PREF_STREAK]     = 0
                prefs[StreakWidget.PREF_DONE]       = false
            }
        }
    }

    private suspend fun triggerGlanceUpdate(context: Context) {
        val appContext = context.applicationContext
        val manager   = GlanceAppWidgetManager(appContext)
        manager.getGlanceIds(StreakWidget::class.java).forEach { id ->
            glanceAppWidget.update(appContext, id)
        }
    }
}
