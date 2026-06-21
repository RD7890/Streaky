package com.rohan.streaky.ui.screens.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohan.streaky.ui.components.AnimatedCounter
import com.rohan.streaky.ui.components.ConfettiOverlay
import com.rohan.streaky.ui.theme.GreenSuccess
import com.rohan.streaky.ui.theme.OrangePrimary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    onBack: () -> Unit,
    vm: HabitDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(habitId) { vm.load(habitId) }
    val state by vm.state.collectAsStateWithLifecycle()
    val habit = state.habit

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(habit?.name ?: "Habit", fontWeight = FontWeight.Bold, maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                    },
                    actions = {
                        IconButton(onClick = { vm.archiveHabit(); onBack() }) {
                            Icon(Icons.Filled.Archive, "Archive", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            if (state.isLoading || habit == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero streak card
                HeroStreakCard(
                    habit = habit,
                    isCompletedToday = state.isCompletedToday,
                    onToggle = { vm.toggleToday() }
                )

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("🏆", "Best", "${habit.bestStreak}d", Modifier.weight(1f))
                    StatCard("✅", "Total", "${habit.totalCompletions}", Modifier.weight(1f))
                    StatCard("🎯", "Goal", "${habit.goalDays}d", Modifier.weight(1f))
                }

                // Progress
                val progress = (habit.currentStreak.toFloat() / habit.goalDays.coerceAtLeast(1)).coerceIn(0f, 1f)
                ProgressSection(progress = progress, current = habit.currentStreak, goal = habit.goalDays)

                // Calendar
                CalendarSection(
                    completedDays = state.completions.map { it.dateEpochDay }.toSet(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(40.dp))
            }
        }

        if (state.showConfetti) {
            ConfettiOverlay(active = true, modifier = Modifier.zIndex(10f), onDone = { vm.dismissConfetti() })
        }
    }
}

@Composable
private fun HeroStreakCard(
    habit: com.rohan.streaky.data.db.entity.HabitEntity,
    isCompletedToday: Boolean,
    onToggle: () -> Unit
) {
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulse by pulseAnim.animateFloat(
        1f, if (habit.currentStreak > 0) 1.05f else 1f,
        infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "p"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompletedToday) OrangePrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isCompletedToday) BorderStroke(2.dp, OrangePrimary) else null,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = habit.iconEmoji,
                fontSize = (56 * pulse).sp
            )
            AnimatedCounter(
                count = habit.currentStreak,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = if (habit.currentStreak > 0) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = if (habit.currentStreak == 1) "day streak 🔥" else "days streak 🔥",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompletedToday) GreenSuccess else OrangePrimary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isCompletedToday) "✓ Done Today!" else "Mark as Done",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (isCompletedToday) {
                Text(
                    "Tap again to unmark",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StatCard(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = OrangePrimary))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

@Composable
private fun ProgressSection(progress: Float, current: Int, goal: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Goal Progress", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            Text("$current / $goal days", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = OrangePrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun CalendarSection(completedDays: Set<Long>, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val startDay = today.minusDays(27)
    val days = (0 until 28).map { startDay.plusDays(it.toLong()) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Last 28 Days", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(160.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            userScrollEnabled = false
        ) {
            items(days) { day ->
                val isCompleted = completedDays.contains(day.toEpochDay())
                val isToday = day == today
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isCompleted && isToday -> OrangePrimary
                                isCompleted           -> OrangePrimary.copy(alpha = 0.6f)
                                isToday               -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else                  -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isToday) Text("·", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), Arrangement.End, Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(OrangePrimary))
            Spacer(Modifier.width(4.dp))
            Text("Completed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
            Spacer(Modifier.width(4.dp))
            Text("Missed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
