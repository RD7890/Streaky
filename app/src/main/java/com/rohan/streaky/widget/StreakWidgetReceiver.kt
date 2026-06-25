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
        // Never call super — we populate state before Glance renders to avoid "Can't load" flash.
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                refreshAll(context, appWidgetIds)
            } catch (_: Exception) {
                fallbackAll(context)
            } finally {
                triggerRender(context)
                pending.finish()
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                // onEnabled: refresh all registered glance IDs
                val allIds = GlanceAppWidgetManager(context)
                    .getGlanceIds(StreakWidget::class.java)
                    .mapNotNull { glanceId ->
                        // Convert back to appWidgetId so we can look up the configured habit
                        glanceId to glanceId
                    }
                // Use the full-refresh path which iterates SharedPrefs per widget
                refreshAll(context, intArrayOf())
            } catch (_: Exception) {
                fallbackAll(context)
            } finally {
                triggerRender(context)
                pending.finish()
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Core refresh: for each widget ID, look up its configured habit and write state
    // ──────────────────────────────────────────────────────────────────────────

    private suspend fun refreshAll(context: Context, appWidgetIds: IntArray) {
        val appCtx      = context.applicationContext
        val sp          = appCtx.getSharedPreferences(StreakWidget.SP_NAME, Context.MODE_PRIVATE)
        val entryPoint  = EntryPointAccessors.fromApplication(appCtx, WidgetEntryPoint::class.java)
        val glanceMgr   = GlanceAppWidgetManager(appCtx)
        val todayEpoch  = LocalDate.now().toEpochDay()

        // All currently registered glance IDs
        val glanceIds = glanceMgr.getGlanceIds(StreakWidget::class.java)

        for (glanceId in glanceIds) {
            try {
                // Derive the android appWidgetId so we can look up SharedPrefs
                // GlanceId internal string is "AppWidgetId-<int>"
                val rawId = glanceId.toString()
                val awId  = rawId.substringAfterLast("-").toIntOrNull()
                val habitId = if (awId != null) sp.getLong("habit_id_$awId", -1L) else -1L

                if (habitId >= 0L) {
                    val habit  = entryPoint.habitDao().getHabitById(habitId).first()
                    val isDone = habit?.let {
                        entryPoint.completionDao().getCompletionForDay(it.id, todayEpoch) != null
                    } ?: false

                    updateAppWidgetState(appCtx, glanceId) { prefs ->
                        prefs[StreakWidget.PREF_HABIT_NAME] = habit?.name    ?: "No habit"
                        prefs[StreakWidget.PREF_STREAK]     = habit?.currentStreak ?: 0
                        prefs[StreakWidget.PREF_DONE]       = isDone
                        prefs[StreakWidget.PREF_COLOR_HEX]  = habit?.colorHex ?: "#FF6B1A"
                    }
                } else {
                    // Widget not configured yet
                    updateAppWidgetState(appCtx, glanceId) { prefs ->
                        prefs[StreakWidget.PREF_HABIT_NAME] = "Tap + hold to pick habit"
                        prefs[StreakWidget.PREF_STREAK]     = 0
                        prefs[StreakWidget.PREF_DONE]       = false
                        prefs[StreakWidget.PREF_COLOR_HEX]  = "#FF6B1A"
                    }
                }
            } catch (_: Exception) {
                // Skip this widget on error — don't break others
            }
        }
    }

    private suspend fun fallbackAll(context: Context) {
        val appCtx  = context.applicationContext
        val manager = GlanceAppWidgetManager(appCtx)
        manager.getGlanceIds(StreakWidget::class.java).forEach { id ->
            try {
                updateAppWidgetState(appCtx, id) { prefs ->
                    prefs[StreakWidget.PREF_HABIT_NAME] = "Streaky"
                    prefs[StreakWidget.PREF_STREAK]     = 0
                    prefs[StreakWidget.PREF_DONE]       = false
                    prefs[StreakWidget.PREF_COLOR_HEX]  = "#FF6B1A"
                }
            } catch (_: Exception) { }
        }
    }

    private suspend fun triggerRender(context: Context) {
        val appCtx  = context.applicationContext
        val manager = GlanceAppWidgetManager(appCtx)
        manager.getGlanceIds(StreakWidget::class.java).forEach { id ->
            try { glanceAppWidget.update(appCtx, id) } catch (_: Exception) { }
        }
    }
}
