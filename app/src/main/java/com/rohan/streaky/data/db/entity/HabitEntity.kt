package com.rohan.streaky.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val iconEmoji: String = "🔥",
    val colorHex: String = "#FF6B1A",
    val goalDays: Int = 21,
    val activeDays: Set<Int> = setOf(1,2,3,4,5,6,7), // 1=Mon..7=Sun
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val reminderEnabled: Boolean = true,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
