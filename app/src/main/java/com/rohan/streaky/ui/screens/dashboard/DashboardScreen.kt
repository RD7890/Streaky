package com.rohan.streaky.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohan.streaky.ui.screens.home.HomeViewModel
import com.rohan.streaky.ui.theme.GreenSuccess
import com.rohan.streaky.ui.theme.OrangePrimary
import com.rohan.streaky.ui.theme.PurpleViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: HomeViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val habits = state.habits

    val totalHabits   = habits.size
    val activeStreaks  = habits.count { it.currentStreak > 0 }
    val inactiveCount = habits.count { it.currentStreak == 0 }
    val bestStreak    = habits.maxOfOrNull { it.bestStreak } ?: 0
    val totalDone     = habits.sumOf { it.totalCompletions }
    val longestName   = habits.maxByOrNull { it.bestStreak }?.name ?: "—"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big stats
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigStatCard("🔥", "Best Streak", "$bestStreak days", OrangePrimary, Modifier.weight(1f))
                    BigStatCard("✅", "Total Done", totalDone.toString(), GreenSuccess, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BigStatCard("⚡", "Active", "$activeStreaks habits", OrangePrimary, Modifier.weight(1f))
                    BigStatCard("💤", "Inactive", "$inactiveCount habits", MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                }
            }

            // Best habit
            if (habits.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = OrangePrimary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🏆", fontSize = 32.sp)
                            Column {
                                Text("Champion Habit", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(longestName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("$bestStreak day best streak 🔥", style = MaterialTheme.typography.bodySmall.copy(color = OrangePrimary))
                            }
                        }
                    }
                }
            }

            // Per-habit breakdown
            if (habits.isNotEmpty()) {
                item {
                    Text("All Habits", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                items(habits.sortedByDescending { it.currentStreak }) { habit ->
                    HabitDashCard(habit)
                }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📊", fontSize = 48.sp)
                            Text("No data yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Add habits to see your stats here", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BigStatCard(emoji: String, label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(emoji, fontSize = 28.sp)
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black, color = color))
            Text(label, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

@Composable
private fun HabitDashCard(habit: com.rohan.streaky.data.db.entity.HabitEntity) {
    val progress = (habit.currentStreak.toFloat() / habit.goalDays.coerceAtLeast(1)).coerceIn(0f, 1f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape)
                    .background(try { Color(android.graphics.Color.parseColor(habit.colorHex)) } catch(e: Exception) { OrangePrimary }.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Text(habit.iconEmoji, fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(habit.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), maxLines = 1)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                    color = OrangePrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text("${habit.currentStreak}/${habit.goalDays}d · Best: ${habit.bestStreak}d", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            Spacer(Modifier.width(10.dp))
            Text(
                if (habit.currentStreak > 0) "${habit.currentStreak}🔥" else "—",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = if (habit.currentStreak > 0) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
