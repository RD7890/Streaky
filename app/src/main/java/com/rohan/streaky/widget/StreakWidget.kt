package com.rohan.streaky.widget

import android.content.Context
import android.content.Intent
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
import com.rohan.streaky.R

class StreakWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs      = currentState<androidx.datastore.preferences.core.Preferences>()
            val habitName  = prefs[PREF_HABIT_NAME] ?: "Add a habit"
            val streak     = prefs[PREF_STREAK]     ?: 0
            val isDone     = prefs[PREF_DONE]       ?: false

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(android.graphics.Color.WHITE))
                    .appWidgetBackground()
                    .padding(12.dp)
                    .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(
                            if (isDone) R.drawable.flame_joy
                            else if (streak > 0) R.drawable.flame_victory
                            else R.drawable.flame_mascot_standing
                        ),
                        contentDescription = "Streak Mascot",
                        modifier = GlanceModifier.size(52.dp)
                    )
                    Spacer(GlanceModifier.height(4.dp))
                    Text(
                        text = "$streak",
                        style = TextStyle(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(android.graphics.Color.parseColor("#FF6B1A"))
                        )
                    )
                    Text(
                        text = if (streak == 1) "day streak" else "days streak",
                        style = TextStyle(
                            fontSize = 11.sp,
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
                        Spacer(GlanceModifier.height(2.dp))
                        Text(
                            "Done today!",
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = ColorProvider(android.graphics.Color.parseColor("#22C55E")),
                                fontWeight = FontWeight.Bold
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
