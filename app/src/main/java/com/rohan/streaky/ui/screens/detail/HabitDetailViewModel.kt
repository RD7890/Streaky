package com.rohan.streaky.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.streaky.data.db.entity.CompletionEntity
import com.rohan.streaky.data.db.entity.HabitEntity
import com.rohan.streaky.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DetailUiState(
    val habit: HabitEntity? = null,
    val completions: List<CompletionEntity> = emptyList(),
    val isCompletedToday: Boolean = false,
    val showConfetti: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    fun load(habitId: Long) {
        viewModelScope.launch {
            combine(
                repo.getHabitById(habitId),
                repo.getCompletionsForHabit(habitId),
                repo.isCompletedToday(habitId)
            ) { habit, completions, doneToday ->
                DetailUiState(
                    habit = habit,
                    completions = completions,
                    isCompletedToday = doneToday,
                    isLoading = false
                )
            }.collect { _state.value = it }
        }
    }

    fun toggleToday() {
        val habitId = _state.value.habit?.id ?: return
        viewModelScope.launch {
            val completed = repo.toggleCompletion(habitId)
            if (completed) _state.update { it.copy(showConfetti = true) }
        }
    }

    fun dismissConfetti() = _state.update { it.copy(showConfetti = false) }

    fun archiveHabit() {
        val id = _state.value.habit?.id ?: return
        viewModelScope.launch { repo.archiveHabit(id) }
    }
}
