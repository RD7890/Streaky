package com.rohan.streaky.widget

import android.content.Context
import android.content.Intent
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
private val WHITE      = ColorProvider(Color(0xFFFFFFFF.toInt()))
private val WHITE_70   = ColorProvider(Color(0xB3FFFFFF.toInt()))  // 70% white
private val WHITE_50   = ColorProvider(Color(0x80FFFFFF.toInt()))  // 50% white
private val GREEN_DONE = ColorProvider(Color(0xFF4ADE80.toInt()))  // bright green
private val ORANGE_DEF = Color(0xFFFF6B1A.toInt())

class StreakWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent(context) }
    }

    @Composable
    private fun WidgetContent(context: Context) {
        val prefs    = currentState<androidx.datastore.preferences.core.Preferences>()
        val name     = prefs[PREF_HABIT_NAME] ?: "Tap + hold to pick habit"
        val streak   = prefs[PREF_STREAK]     ?: 0
        val isDone   = prefs[PREF_DONE]       ?: false
        val colorHex = prefs[PREF_COLOR_HEX]  ?: "#FF6B1A"

        val argb = try {
            android.graphics.Color.parseColor(colorHex)
        } catch (e: Exception) {
            ORANGE_DEF.value.toLong().toInt()
        }
        val bgColor = ColorProvider(Color(argb))

        // Darken the ARGB by 20% for a richer base
        val darkArgb = android.graphics.Color.argb(
            255,
            (android.graphics.Color.red(argb) * 0.80).toInt().coerceIn(0, 255),
            (android.graphics.Color.green(argb) * 0.80).toInt().coerceIn(0, 255),
            (android.graphics.Color.blue(argb) * 0.80).toInt().coerceIn(0, 255)
        )
        val darkBg = ColorProvider(Color(darkArgb))

        // Mascot tier based on streak
        val mascotRes = when {
            isDone       -> R.drawable.flame_joy
            streak >= 21 -> R.drawable.flame_victory
            streak >= 7  -> R.drawable.flame_flex
            streak >= 1  -> R.drawable.flame_running
            else         -> R.drawable.flame_sleeping
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(bgColor)
                .padding(12.dp)
                .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment   = Alignment.CenterVertically
        ) {
            // ── Habit name pill ──────────────────────────────────────
            Box(
                modifier         = GlanceModifier
                    .background(ImageProvider(R.drawable.widget_pill_bg))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = name,
                    style    = TextStyle(
                        color      = WHITE,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            Spacer(GlanceModifier.height(10.dp))

            // ── Mascot + streak number side by side ──────────────────
            Row(
                verticalAlignment   = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider           = ImageProvider(mascotRes),
                    contentDescription = "mascot",
                    modifier           = GlanceModifier.size(54.dp)
                )
                Spacer(GlanceModifier.width(6.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalAlignment   = Alignment.CenterVertically
                ) {
                    Text(
                        text  = streak.toString(),
                        style = TextStyle(
                            color      = WHITE,
                            fontSize   = 44.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text  = "Days",
                        style = TextStyle(
                            color    = WHITE_70,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(GlanceModifier.height(10.dp))

            // ── Status badge ─────────────────────────────────────────
            when {
                isDone -> {
                    Box(
                        modifier         = GlanceModifier
                            .background(ImageProvider(R.drawable.widget_done_badge))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "Done Today",
                            style = TextStyle(
                                color      = GREEN_DONE,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                streak > 0 -> {
                    Text(
                        text  = "You're On A Streak!",
                        style = TextStyle(
                            color      = WHITE_70,
                            fontSize   = 10.sp,
                            fontStyle  = FontStyle.Italic
                        )
                    )
                }
                else -> {
                    Text(
                        text  = "Start your streak!",
                        style = TextStyle(
                            color    = WHITE_50,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }

    companion object {
        val PREF_HABIT_NAME = stringPreferencesKey("habit_name")
        val PREF_STREAK     = intPreferencesKey("streak")
        val PREF_DONE       = booleanPreferencesKey("done")
        val PREF_COLOR_HEX  = stringPreferencesKey("color_hex")
        // Stored in SharedPreferences by config activity for receiver lookup
        const val SP_NAME   = "widget_habit_prefs"
    }
}
