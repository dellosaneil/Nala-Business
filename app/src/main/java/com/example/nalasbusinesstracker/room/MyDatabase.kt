package com.example.nalasbusinesstracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import com.example.nalasbusinesstracker.room.data_classes.Expenses
import com.example.nalasbusinesstracker.room.dao.ClothesDao
import com.example.nalasbusinesstracker.room.dao.ExpenseDao


@Database(entities = [Clothes::class, Expenses::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun clothesDao() : ClothesDao
    abstract fun expensesDao() : ExpenseDao
}