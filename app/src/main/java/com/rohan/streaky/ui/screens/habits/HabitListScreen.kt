package com.rohan.streaky.ui.screens.habits

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohan.streaky.ui.components.HabitCompletionRow
import com.rohan.streaky.ui.screens.home.HomeViewModel
import com.rohan.streaky.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    onAddHabit: () -> Unit,
    onHabitClick: (Long) -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val activeHabits  = state.habits.filter { it.currentStreak > 0 }
    val inactiveHabits = state.habits.filter { it.currentStreak == 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Habits", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onAddHabit) {
                        Icon(Icons.Filled.Add, "Add", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabit, containerColor = OrangePrimary, shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Filled.Add, "Add", tint = Color.White)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (state.habits.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("🔥", fontSize = 64.sp)
                    Text("No habits yet", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Button(onClick = onAddHabit, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), shape = RoundedCornerShape(12.dp)) {
                        Text("Add First Habit")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (activeHabits.isNotEmpty()) {
                    item {
                        SectionHeader("🔥 Active Streaks (${activeHabits.size})")
                    }
                    items(activeHabits, key = { it.id }) { habit ->
                        HabitCompletionRow(
                            habit = habit,
                            isCompleted = state.todayCompletions.contains(habit.id),
                            onToggle = { vm.toggleToday(habit) },
                            onHabitClick = { onHabitClick(habit.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                if (inactiveHabits.isNotEmpty()) {
                    item {
                        SectionHeader("💤 Not Started (${inactiveHabits.size})")
                    }
                    items(inactiveHabits, key = { it.id }) { habit ->
                        HabitCompletionRow(
                            habit = habit,
                            isCompleted = state.todayCompletions.contains(habit.id),
                            onToggle = { vm.toggleToday(habit) },
                            onHabitClick = { onHabitClick(habit.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}
