package com.rohan.streaky.di

import android.content.Context
import androidx.room.Room
import com.rohan.streaky.data.db.AppDatabase
import com.rohan.streaky.data.db.dao.CompletionDao
import com.rohan.streaky.data.db.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "streaky.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()
    @Provides fun provideCompletionDao(db: AppDatabase): CompletionDao = db.completionDao()
}
