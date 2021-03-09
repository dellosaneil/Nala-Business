package com.example.nalasbusinesstracker.di

import android.content.Context
import androidx.room.Room
import com.example.nalasbusinesstracker.Constants.DATABASE_NAME
import com.example.nalasbusinesstracker.room.MyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MyDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideClothesDao(database: MyDatabase) = database.clothesDao()

    @Singleton
    @Provides
    fun provideExpensesDao(database: MyDatabase) = database.expensesDao()

}