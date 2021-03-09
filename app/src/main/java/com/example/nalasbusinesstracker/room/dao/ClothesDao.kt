package com.example.nalasbusinesstracker.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nalasbusinesstracker.room.data_classes.Clothes

@Dao
interface ClothesDao {

    @Insert
    suspend fun insertClothe(clothes: Clothes)

    @Query("SELECT * FROM clothes ORDER BY storageTime DESC")
    suspend fun retrieveAllClothes(): List<Clothes>

    @Query("SELECT * FROM clothes WHERE clotheType = :type ORDER BY storageTime DESC")
    suspend fun retrieveByType(type: String): List<Clothes>

    @Query("SELECT * FROM clothes WHERE dominantColor = :color ORDER BY storageTime DESC")
    suspend fun retrieveByColor(color: String): List<Clothes>

    @Delete
    suspend fun deleteClothe(clothes: Clothes)


    @Query("SELECT COUNT(*) FROM clothes WHERE purchaseDate = :date AND currentStatus = :status")
    suspend fun clothesSoldForOneDay(date : Long, status : String) : Int


}