package com.rohan.streaky.di

import com.rohan.streaky.data.db.dao.CompletionDao
import com.rohan.streaky.data.db.dao.HabitDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun habitDao(): HabitDao
    fun completionDao(): CompletionDao
}
