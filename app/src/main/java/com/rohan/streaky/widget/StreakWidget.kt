package com.rohan.streaky.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.rohan.streaky.MainActivity

// IMPORTANT: Never pass android.graphics.Color values to Glance's ColorProvider(Int) —
// that constructor expects a @ColorRes resource ID. Always use ColorProvider(Color(argbInt)).
private val BgColor     = ColorProvider(Color(0xFF1C1C1E.toInt()))
private val OrangeColor = ColorProvider(Color(0xFFFF6B1A.toInt()))
private val GrayColor   = ColorProvider(Color(0xFFAAAAAA.toInt()))
private val WhiteColor  = ColorProvider(Color(0xFFFFFFFF.toInt()))
private val GreenColor  = ColorProvider(Color(0xFF22C55E.toInt()))

class StreakWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs     = currentState<androidx.datastore.preferences.core.Preferences>()
            val habitName = prefs[PREF_HABIT_NAME] ?: "Add a habit"
            val streak    = prefs[PREF_STREAK]     ?: 0
            val isDone    = prefs[PREF_DONE]       ?: false

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(BgColor)
                    .padding(12.dp)
                    .clickable(
                        actionStartActivity(Intent(context, MainActivity::class.java))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Text(
                        text  = streak.toString(),
                        style = TextStyle(
                            fontSize   = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color      = OrangeColor
                        )
                    )
                    Text(
                        text  = if (streak == 1) "day streak" else "days streak",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color    = GrayColor
                        )
                    )
                    Spacer(GlanceModifier.height(6.dp))
                    Text(
                        text     = habitName,
                        style    = TextStyle(
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color      = WhiteColor
                        ),
                        maxLines = 1
                    )
                    if (isDone) {
                        Spacer(GlanceModifier.height(4.dp))
                        Text(
                            text  = "Done today",
                            style = TextStyle(
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = GreenColor
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        val PREF_HABIT_NAME = androidx.datastore.preferences.core.stringPreferencesKey("habit_name")
        val PREF_STREAK     = androidx.datastore.preferences.core.intPreferencesKey("streak")
        val PREF_DONE       = androidx.datastore.preferences.core.booleanPreferencesKey("done")
    }
}
