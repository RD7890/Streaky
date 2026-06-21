package com.rohan.streaky.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.streaky.data.db.entity.HabitEntity
import com.rohan.streaky.data.repository.HabitRepository
import com.rohan.streaky.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddHabitState(
    val name: String = "",
    val category: String = "Health",
    val emoji: String = "🔥",
    val colorHex: String = "#FF6B1A",
    val goalDays: Int = 21,
    val selectedDays: Set<Int> = setOf(1,2,3,4,5,6,7),
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val reminderEnabled: Boolean = true,
    val isSaving: Boolean = false,
    val savedId: Long? = null
)

val Categories = listOf(
    "Health" to "💪",
    "Fitness" to "🏋️",
    "Mind" to "🧘",
    "Work" to "💼",
    "Social" to "🤝",
    "Finance" to "💰",
    "Creative" to "🎨",
    "Other" to "⭐"
)

val CategoryColors = mapOf(
    "Health"   to "#22C55E",
    "Fitness"  to "#FF6B1A",
    "Mind"     to "#8B5CF6",
    "Work"     to "#3B82F6",
    "Social"   to "#EC4899",
    "Finance"  to "#F59E0B",
    "Creative" to "#14B8A6",
    "Other"    to "#6B7280"
)

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val repo: HabitRepository,
    private val scheduler: NotificationScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(AddHabitState())
    val state: StateFlow<AddHabitState> = _state.asStateFlow()

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setCategory(v: String) {
        val emoji = Categories.find { it.first == v }?.second ?: "⭐"
        val color = CategoryColors[v] ?: "#FF6B1A"
        _state.update { it.copy(category = v, emoji = emoji, colorHex = color) }
    }
    fun setGoalDays(v: Int) = _state.update { it.copy(goalDays = v) }
    fun toggleDay(day: Int) = _state.update {
        val days = it.selectedDays.toMutableSet()
        if (days.contains(day)) { if (days.size > 1) days.remove(day) }
        else days.add(day)
        it.copy(selectedDays = days)
    }
    fun setReminder(h: Int, m: Int) = _state.update { it.copy(reminderHour = h, reminderMinute = m) }
    fun toggleReminder() = _state.update { it.copy(reminderEnabled = !it.reminderEnabled) }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val id = repo.addHabit(
                HabitEntity(
                    name = s.name.trim(),
                    category = s.category,
                    iconEmoji = s.emoji,
                    colorHex = s.colorHex,
                    goalDays = s.goalDays,
                    activeDays = s.selectedDays,
                    reminderHour = s.reminderHour,
                    reminderMinute = s.reminderMinute,
                    reminderEnabled = s.reminderEnabled
                )
            )
            if (s.reminderEnabled) scheduler.schedule(id, s.reminderHour, s.reminderMinute)
            _state.update { it.copy(isSaving = false, savedId = id) }
        }
    }
}
