package com.rohan.streaky.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
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
import com.rohan.streaky.R

// CRITICAL: Never pass android.graphics.Color ARGB ints to ColorProvider(Int) — that
// constructor expects a @ColorRes resource ID. Always use ColorProvider(Color(argbInt)).
private val WHITE_SOLID = ColorProvider(Color(0xFFFFFFFF.toInt()))
private val WHITE_TEXT  = ColorProvider(Color(0xFFFFFFFF.toInt()))
private val WHITE_80    = ColorProvider(Color(0xCCFFFFFF.toInt()))
private val GREEN_DONE  = ColorProvider(Color(0xFF4ADE80.toInt()))
private val ORANGE_DEF  = 0xFFFF6B1A.toInt()

class StreakWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent(context) }
    }

    @Composable
    private fun WidgetContent(context: Context) {
        val prefs    = currentState<androidx.datastore.preferences.core.Preferences>()
        val name     = prefs[PREF_HABIT_NAME] ?: "Pick a habit"
        val streak   = prefs[PREF_STREAK]     ?: 0
        val isDone   = prefs[PREF_DONE]       ?: false
        val colorHex = prefs[PREF_COLOR_HEX]  ?: "#FF6B1A"

        val argb = try {
            android.graphics.Color.parseColor(colorHex)
        } catch (e: Exception) { ORANGE_DEF }

        // Darker shade for the top header — creates visual depth
        val darkArgb = android.graphics.Color.argb(
            255,
            (android.graphics.Color.red(argb)   * 0.78).toInt().coerceIn(0, 255),
            (android.graphics.Color.green(argb) * 0.78).toInt().coerceIn(0, 255),
            (android.graphics.Color.blue(argb)  * 0.78).toInt().coerceIn(0, 255)
        )

        val topBg     = ColorProvider(Color(darkArgb))
        val midAccent = ColorProvider(Color(argb))
        val bottomBg  = ColorProvider(Color(argb))

        val mascotRes = when {
            isDone       -> R.drawable.flame_joy
            streak >= 21 -> R.drawable.flame_victory
            streak >= 7  -> R.drawable.flame_flex
            streak >= 1  -> R.drawable.flame_running
            else         -> R.drawable.flame_sleeping
        }

        val statusText = when {
            isDone     -> "Done Today"
            streak > 0 -> "You're On A Streak!"
            else       -> "Start Your Streak!"
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(bottomBg)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── TOP: Habit name on darker header ───────────────────
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(topBg)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = name,
                    maxLines = 1,
                    style    = TextStyle(
                        color      = WHITE_TEXT,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // ── MIDDLE: White band — mascot + streak count ──────────
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(WHITE_SOLID)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        provider           = ImageProvider(mascotRes),
                        contentDescription = null,
                        modifier           = GlanceModifier.size(56.dp)
                    )
                    Spacer(GlanceModifier.width(8.dp))
                    Text(
                        text  = streak.toString(),
                        style = TextStyle(
                            color      = midAccent,
                            fontSize   = 52.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(GlanceModifier.width(4.dp))
                    Text(
                        text  = "Days",
                        style = TextStyle(
                            color      = midAccent,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // ── BOTTOM: Status — fills the rest ────────────────────
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = statusText,
                    style = TextStyle(
                        color      = if (isDone) GREEN_DONE else WHITE_80,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle  = FontStyle.Italic
                    )
                )
            }
        }
    }

    companion object {
        val PREF_HABIT_NAME = stringPreferencesKey("habit_name")
        val PREF_STREAK     = intPreferencesKey("streak")
        val PREF_DONE       = booleanPreferencesKey("done")
        val PREF_COLOR_HEX  = stringPreferencesKey("color_hex")
        const val SP_NAME   = "widget_habit_prefs"
    }
}
