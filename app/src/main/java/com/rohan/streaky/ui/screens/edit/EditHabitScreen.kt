package com.rohan.streaky.ui.screens.edit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohan.streaky.ui.screens.add.Categories
import com.rohan.streaky.ui.theme.OrangePrimary
import com.rohan.streaky.ui.utils.CategoryIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habitId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: EditHabitViewModel = hiltViewModel()
) {
    LaunchedEffect(habitId) { vm.load(habitId) }
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedId) {
        if (state.savedId != null) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Habit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                actions = {
                    TextButton(
                        onClick  = { vm.save() },
                        enabled  = state.name.isNotBlank() && !state.isSaving
                    ) {
                        Text("Save", color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionLabel("Habit Name")
            OutlinedTextField(
                value         = state.name,
                onValueChange = vm::setName,
                placeholder   = { Text("e.g. Morning Run") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = OrangePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            SectionLabel("Category")
            Row(
                modifier              = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Categories.forEach { cat ->
                    val selected = cat == state.category
                    val iconRes  = CategoryIcons.drawableRes(CategoryIcons.forCategory(cat))
                    FilterChip(
                        selected = selected,
                        onClick  = { vm.setCategory(cat) },
                        label    = {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(painterResource(iconRes), cat, Modifier.size(16.dp))
                                Text(cat)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor     = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            SectionLabel("Goal — ${state.goalDays} days")
            Slider(
                value         = state.goalDays.toFloat(),
                onValueChange = { vm.setGoalDays(it.toInt()) },
                valueRange    = 7f..365f,
                colors        = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
            )
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("7 days",   style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("365 days", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            SectionLabel("Repeat Days")
            val labels = listOf("M", "T", "W", "T", "F", "S", "S")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                labels.forEachIndexed { index, label ->
                    val day        = index + 1
                    val isSelected = state.selectedDays.contains(day)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { vm.toggleDay(day) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color      = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            SectionLabel("Daily Reminder")
            Card(
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Notifications, "Reminder", tint = OrangePrimary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Daily Reminder", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                        Text(
                            "%02d:%02d".format(state.reminderHour, state.reminderMinute),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked         = state.reminderEnabled,
                        onCheckedChange = { vm.toggleReminder() },
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor = OrangePrimary,
                            checkedTrackColor = OrangePrimary.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick  = { vm.save() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled  = state.name.isNotBlank() && !state.isSaving,
                colors   = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape    = RoundedCornerShape(16.dp)
            ) {
                if (state.isSaving)
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                else
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
