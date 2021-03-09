package com.example.nalasbusinesstracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nalasbusinesstracker.data_classes.Expenses

@Dao
interface ExpenseDao {


    @Insert
    suspend fun insertExpense(expenses: Expenses)

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC")
    suspend fun retrieveAllExpenses() : List<Expenses>

}