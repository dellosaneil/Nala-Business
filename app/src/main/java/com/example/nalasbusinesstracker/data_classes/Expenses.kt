package com.example.nalasbusinesstracker.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expenses(
    val expenseType : String,
    val expenseDate : Long,
    val expenseAmount : Double
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}
