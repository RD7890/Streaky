package com.rohan.streaky.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.rohan.streaky.MainActivity

class StreakWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val habitName = prefs[PREF_HABIT_NAME] ?: "Habit"
            val streak    = prefs[PREF_STREAK]     ?: 0
            val emoji     = prefs[PREF_EMOJI]      ?: "🔥"
            val isDone    = prefs[PREF_DONE]       ?: false

            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(
                            if (isDone) androidx.glance.ImageProvider(android.R.drawable.screen_background_light)
                            else androidx.glance.ImageProvider(android.R.drawable.screen_background_light)
                        )
                        .appWidgetBackground()
                        .padding(12.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, style = TextStyle(fontSize = 28.sp))
                        Spacer(GlanceModifier.height(4.dp))
                        Text(
                            text = "$streak",
                            style = TextStyle(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(android.graphics.Color.parseColor("#FF6B1A"))
                            )
                        )
                        Text(
                            text = "days",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = ColorProvider(android.graphics.Color.GRAY)
                            )
                        )
                        Spacer(GlanceModifier.height(4.dp))
                        Text(
                            text = habitName,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = ColorProvider(android.graphics.Color.DKGRAY),
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                        if (isDone) {
                            Text(
                                "✓ Done",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = ColorProvider(android.graphics.Color.parseColor("#22C55E"))
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val PREF_HABIT_NAME = androidx.datastore.preferences.core.stringPreferencesKey("habit_name")
        val PREF_STREAK     = androidx.datastore.preferences.core.intPreferencesKey("streak")
        val PREF_EMOJI      = androidx.datastore.preferences.core.stringPreferencesKey("emoji")
        val PREF_DONE       = androidx.datastore.preferences.core.booleanPreferencesKey("done")
    }
}
