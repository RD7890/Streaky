package com.rohan.streaky.data.repository

import com.rohan.streaky.data.db.dao.CompletionDao
import com.rohan.streaky.data.db.dao.HabitDao
import com.rohan.streaky.data.db.entity.CompletionEntity
import com.rohan.streaky.data.db.entity.HabitEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao
) {
    fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()
    fun getActiveHabits(): Flow<List<HabitEntity>> = habitDao.getActiveHabits()
    fun getInactiveHabits(): Flow<List<HabitEntity>> = habitDao.getInactiveHabits()
    fun getHabitById(id: Long): Flow<HabitEntity?> = habitDao.getHabitById(id)
    fun getCompletionsForHabit(habitId: Long) = completionDao.getCompletionsForHabit(habitId)
    fun isCompletedToday(habitId: Long): Flow<Boolean> =
        completionDao.isCompletedOnDay(habitId, LocalDate.now().toEpochDay())

    suspend fun addHabit(habit: HabitEntity): Long = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)
    suspend fun archiveHabit(id: Long) = habitDao.archiveHabit(id)

    suspend fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()): Boolean {
        val day = date.toEpochDay()
        val existing = completionDao.getCompletionForDay(habitId, day)
        return if (existing != null) {
            completionDao.deleteCompletion(habitId, day)
            recalcStreak(habitId)
            false
        } else {
            completionDao.insertCompletion(CompletionEntity(habitId = habitId, dateEpochDay = day))
            recalcStreak(habitId)
            true
        }
    }

    private suspend fun recalcStreak(habitId: Long) {
        val days = completionDao.getAllDaysForHabit(habitId).sortedDescending()
        var streak = 0
        var expected = LocalDate.now().toEpochDay()
        for (day in days) {
            if (day == expected || day == expected - 1) {
                streak++
                expected = day - 1
            } else break
        }
        val habit = habitDao.getHabitById(habitId).first() ?: return
        val best = maxOf(habit.bestStreak, streak)
        habitDao.updateStreakStats(habitId, streak, best, days.size)
    }

    suspend fun getStreakForHabit(habitId: Long): Int {
        val days = completionDao.getAllDaysForHabit(habitId).sortedDescending()
        var streak = 0
        var expected = LocalDate.now().toEpochDay()
        for (day in days) {
            if (day == expected || day == expected - 1) { streak++; expected = day - 1 }
            else break
        }
        return streak
    }
}
