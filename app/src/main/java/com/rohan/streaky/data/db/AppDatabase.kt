package com.rohan.streaky.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rohan.streaky.data.db.dao.CompletionDao
import com.rohan.streaky.data.db.dao.HabitDao
import com.rohan.streaky.data.db.entity.CompletionEntity
import com.rohan.streaky.data.db.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, CompletionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao
}
