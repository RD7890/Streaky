package com.rohan.streaky.data.db.dao

import androidx.room.*
import com.rohan.streaky.data.db.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE currentStreak > 0 AND isArchived = 0 ORDER BY currentStreak DESC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE currentStreak = 0 AND isArchived = 0")
    fun getInactiveHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("UPDATE habits SET currentStreak = :streak, bestStreak = :best, totalCompletions = :total WHERE id = :id")
    suspend fun updateStreakStats(id: Long, streak: Int, best: Int, total: Int)

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :id")
    suspend fun archiveHabit(id: Long)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
}
