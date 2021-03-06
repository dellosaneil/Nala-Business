package com.example.nalasbusinesstracker.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nalasbusinesstracker.room.data_classes.Expenses

@Dao
interface ExpenseDao {


    @Insert
    suspend fun insertExpense(expenses: Expenses)

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC")
    suspend fun retrieveAllExpenses() : List<Expenses>

    @Delete
    suspend fun deleteExpense(expenses: Expenses)


}