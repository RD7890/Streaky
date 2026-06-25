package com.rohan.streaky.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.rohan.streaky.data.db.entity.HabitEntity
import com.rohan.streaky.di.WidgetEntryPoint
import com.rohan.streaky.ui.theme.OrangePrimary
import com.rohan.streaky.ui.theme.StreakTheme
import com.rohan.streaky.ui.utils.CategoryIcons
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
class StreakWidgetConfigActivity : ComponentActivity() {

    private val appWidgetId: Int by lazy {
        intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Default to CANCELLED so Android removes the widget if user backs out
        setResult(RESULT_CANCELED)

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            StreakTheme {
                HabitPickerScreen(onPick = { habit -> configureWidget(habit) })
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Composable UI
    // ──────────────────────────────────────────────────────────────────────────

    @Composable
    private fun HabitPickerScreen(onPick: (HabitEntity) -> Unit) {
        var habits by remember { mutableStateOf<List<HabitEntity>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            val ep = EntryPointAccessors.fromApplication(
                applicationContext, WidgetEntryPoint::class.java
            )
            habits  = ep.habitDao().getAllHabits().first()
            loading = false
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title  = { Text("Choose Habit for Widget", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            when {
                loading -> {
                    Box(
                        Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = OrangePrimary) }
                }
                habits.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize().padding(padding).padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "No habits yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Open Streaky and add your first habit, then add the widget.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(habits, key = { it.id }) { habit ->
                            HabitPickerRow(habit, onClick = { onPick(habit) })
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HabitPickerRow(habit: HabitEntity, onClick: () -> Unit) {
        val iconRes = CategoryIcons.drawableRes(habit.iconEmoji)
        val bgColor = remember(habit.colorHex) {
            try { Color(android.graphics.Color.parseColor(habit.colorHex)).copy(alpha = 0.12f) }
            catch (e: Exception) { OrangePrimary.copy(alpha = 0.12f) }
        }
        val accentColor = remember(habit.colorHex) {
            try { Color(android.graphics.Color.parseColor(habit.colorHex)) }
            catch (e: Exception) { OrangePrimary }
        }

        Card(
            modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
            shape     = RoundedCornerShape(14.dp),
            colors    = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier              = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter            = painterResource(iconRes),
                    contentDescription = habit.name,
                    modifier           = Modifier.size(36.dp)
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        habit.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        if (habit.currentStreak > 0) "${habit.currentStreak} day streak" else "No streak yet",
                        style = MaterialTheme.typography.bodySmall.copy(color = accentColor)
                    )
                }
                Icon(
                    painter            = painterResource(com.rohan.streaky.R.drawable.flame_running),
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp),
                    tint               = accentColor
                )
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Widget configuration: save habit, push state, return RESULT_OK
    // ──────────────────────────────────────────────────────────────────────────

    private fun configureWidget(habit: HabitEntity) {
        lifecycleScope.launch {
            try {
                val appCtx    = applicationContext
                val glanceMgr = GlanceAppWidgetManager(appCtx)
                val glanceId  = glanceMgr.getGlanceIdBy(appWidgetId)

                // Save habitId in SharedPreferences keyed by appWidgetId
                // (the receiver uses this to reload data on subsequent updates)
                appCtx.getSharedPreferences(StreakWidget.SP_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putLong("habit_id_$appWidgetId", habit.id)
                    .apply()

                // Also check today's completion
                val ep        = EntryPointAccessors.fromApplication(appCtx, WidgetEntryPoint::class.java)
                val todayEpoch = LocalDate.now().toEpochDay()
                val isDone    = ep.completionDao().getCompletionForDay(habit.id, todayEpoch) != null

                // Write initial display state into Glance DataStore
                updateAppWidgetState(appCtx, glanceId) { prefs ->
                    prefs[StreakWidget.PREF_HABIT_NAME] = habit.name
                    prefs[StreakWidget.PREF_STREAK]     = habit.currentStreak
                    prefs[StreakWidget.PREF_DONE]       = isDone
                    prefs[StreakWidget.PREF_COLOR_HEX]  = habit.colorHex
                }

                // Trigger Glance to re-render
                StreakWidget().update(appCtx, glanceId)

                setResult(
                    RESULT_OK,
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                )
            } catch (e: Exception) {
                setResult(RESULT_CANCELED)
            } finally {
                finish()
            }
        }
    }
}
