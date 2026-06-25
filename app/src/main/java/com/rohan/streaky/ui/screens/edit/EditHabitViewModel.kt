package com.rohan.streaky.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.streaky.data.repository.HabitRepository
import com.rohan.streaky.notification.NotificationScheduler
import com.rohan.streaky.ui.screens.add.AddHabitState
import com.rohan.streaky.ui.screens.add.CategoryColors
import com.rohan.streaky.ui.utils.CategoryIcons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    private val repo: HabitRepository,
    private val scheduler: NotificationScheduler
) : ViewModel() {

    private val _state = MutableStateFlow(AddHabitState())
    val state: StateFlow<AddHabitState> = _state.asStateFlow()

    private var habitId: Long = -1L

    fun load(id: Long) {
        if (habitId == id) return
        habitId = id
        viewModelScope.launch {
            repo.getHabitById(id).first()?.let { habit ->
                _state.update {
                    it.copy(
                        name            = habit.name,
                        category        = habit.category,
                        iconName        = habit.iconEmoji,
                        colorHex        = habit.colorHex,
                        goalDays        = habit.goalDays,
                        selectedDays    = habit.activeDays,
                        reminderHour    = habit.reminderHour,
                        reminderMinute  = habit.reminderMinute,
                        reminderEnabled = habit.reminderEnabled
                    )
                }
            }
        }
    }

    fun setName(v: String)      = _state.update { it.copy(name = v) }
    fun setGoalDays(v: Int)     = _state.update { it.copy(goalDays = v) }
    fun toggleReminder()        = _state.update { it.copy(reminderEnabled = !it.reminderEnabled) }
    fun setReminder(h: Int, m: Int) = _state.update { it.copy(reminderHour = h, reminderMinute = m) }

    fun setCategory(v: String) {
        val iconName = CategoryIcons.forCategory(v)
        val color    = CategoryColors[v] ?: "#FF6B1A"
        _state.update { it.copy(category = v, iconName = iconName, colorHex = color) }
    }

    fun toggleDay(day: Int) = _state.update {
        val days = it.selectedDays.toMutableSet()
        if (days.contains(day)) { if (days.size > 1) days.remove(day) } else days.add(day)
        it.copy(selectedDays = days)
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank() || habitId < 0) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val existing = repo.getHabitById(habitId).first() ?: return@launch
            repo.updateHabit(
                existing.copy(
                    name            = s.name.trim(),
                    category        = s.category,
                    iconEmoji       = s.iconName,
                    colorHex        = s.colorHex,
                    goalDays        = s.goalDays,
                    activeDays      = s.selectedDays,
                    reminderHour    = s.reminderHour,
                    reminderMinute  = s.reminderMinute,
                    reminderEnabled = s.reminderEnabled
                )
            )
            if (s.reminderEnabled) scheduler.schedule(habitId, s.reminderHour, s.reminderMinute)
            _state.update { it.copy(isSaving = false, savedId = habitId) }
        }
    }
}
