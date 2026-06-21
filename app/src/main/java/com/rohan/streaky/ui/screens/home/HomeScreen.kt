package com.rohan.streaky.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohan.streaky.R
import com.rohan.streaky.ui.components.ConfettiOverlay
import com.rohan.streaky.ui.components.HabitCompletionRow
import com.rohan.streaky.ui.theme.GreenSuccess
import com.rohan.streaky.ui.theme.OrangePrimary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddHabit: () -> Unit,
    onHabitClick: (Long) -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    val dateLabel = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddHabit,
                    containerColor = OrangePrimary,
                    shape = RoundedCornerShape(16.dp)
                ) { Icon(Icons.Filled.Add, "Add Habit", tint = Color.White) }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.flame_mascot_cool),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                "Streaky",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = OrangePrimary
                                )
                            )
                        }
                        Text(
                            dateLabel,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                item {
                    WeekStripRow(today = today, modifier = Modifier.padding(horizontal = 20.dp))
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    val activeCount = state.habits.count { it.currentStreak > 0 }
                    SummaryCard(
                        active    = activeCount,
                        total     = state.habits.size,
                        doneToday = state.todayCompletions.size,
                        modifier  = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                }

                if (state.habits.isEmpty()) {
                    item { EmptyState(onAddHabit) }
                } else {
                    item {
                        Text(
                            "Today's Habits",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    items(state.habits, key = { it.id }) { habit ->
                        HabitCompletionRow(
                            habit       = habit,
                            isCompleted = state.todayCompletions.contains(habit.id),
                            onToggle    = { vm.toggleToday(habit) },
                            onHabitClick = { onHabitClick(habit.id) },
                            modifier    = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (state.showConfetti) {
            ConfettiOverlay(
                active   = true,
                modifier = Modifier.zIndex(10f),
                onDone   = { vm.dismissConfetti() }
            )
        }
    }
}

@Composable
private fun WeekStripRow(today: LocalDate, modifier: Modifier = Modifier) {
    val days = (-3..3).map { today.plusDays(it.toLong()) }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { day ->
            val isToday = day == today
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isToday) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text(
                    day.dayOfWeek.name.take(1),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isToday) FontWeight.Black else FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(active: Int, total: Int, doneToday: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(total.toString(),     "Total")
            VerticalDivider()
            StatItem(active.toString(),    "On Streak",  OrangePrimary)
            VerticalDivider()
            StatItem(doneToday.toString(), "Done Today", GreenSuccess)
        }
    }
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier.width(1.dp).height(40.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )
}

@Composable
private fun StatItem(value: String, label: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = color))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@Composable
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.flame_mascot_standing),
            contentDescription = "Start your streak",
            modifier = Modifier.size(130.dp)
        )
        Text("No habits yet", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(
            "Add your first habit and start building your streak today!",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Add First Habit", fontWeight = FontWeight.Bold) }
    }
}
