package com.rohan.streaky.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.streaky.data.db.entity.HabitEntity
import com.rohan.streaky.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val habits: List<HabitEntity> = emptyList(),
    val todayCompletions: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val showConfetti: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val today = LocalDate.now().toEpochDay()

    init {
        viewModelScope.launch {
            repo.getAllHabits().collect { habits ->
                // Check today's completion for each habit via completionDao
                _state.update { it.copy(habits = habits, isLoading = false) }
            }
        }

        // Track today's completions via individual habit flows
        viewModelScope.launch {
            repo.getAllHabits()
                .flatMapLatest { habits ->
                    if (habits.isEmpty()) flowOf(emptySet())
                    else {
                        val flows = habits.map { h -> repo.isCompletedToday(h.id).map { done -> h.id to done } }
                        combine(flows) { pairs ->
                            pairs.filter { it.second }.map { it.first }.toSet()
                        }
                    }
                }
                .collect { completions ->
                    _state.update { it.copy(todayCompletions = completions) }
                }
        }
    }

    fun toggleToday(habit: HabitEntity) {
        viewModelScope.launch {
            val completed = repo.toggleCompletion(habit.id)
            if (completed) {
                _state.update { it.copy(showConfetti = true) }
            }
        }
    }

    fun dismissConfetti() {
        _state.update { it.copy(showConfetti = false) }
    }
}
