package com.rohan.streaky.data.db.dao

  import androidx.room.*
  import com.rohan.streaky.data.db.entity.CompletionEntity
  import kotlinx.coroutines.flow.Flow

  @Dao
  interface CompletionDao {
      @Query("SELECT * FROM completions WHERE habitId = :habitId ORDER BY dateEpochDay DESC")
      fun getCompletionsForHabit(habitId: Long): Flow<List<CompletionEntity>>

      @Query("SELECT * FROM completions WHERE habitId = :habitId AND dateEpochDay = :day LIMIT 1")
      suspend fun getCompletionForDay(habitId: Long, day: Long): CompletionEntity?

      @Query("SELECT EXISTS(SELECT 1 FROM completions WHERE habitId = :habitId AND dateEpochDay = :day)")
      fun isCompletedOnDay(habitId: Long, day: Long): Flow<Boolean>

      @Query("SELECT * FROM completions WHERE dateEpochDay = :day")
      suspend fun getCompletedHabitIdsOnDay(day: Long): List<CompletionEntity>

      @Query("SELECT dateEpochDay FROM completions WHERE habitId = :habitId ORDER BY dateEpochDay DESC")
      suspend fun getAllDaysForHabit(habitId: Long): List<Long>

      @Insert(onConflict = OnConflictStrategy.IGNORE)
      suspend fun insertCompletion(completion: CompletionEntity): Long

      @Query("DELETE FROM completions WHERE habitId = :habitId AND dateEpochDay = :day")
      suspend fun deleteCompletion(habitId: Long, day: Long)
  }
  