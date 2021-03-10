package com.example.nalasbusinesstracker.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nalasbusinesstracker.room.data_classes.Clothes

@Dao
interface ClothesDao {

    @Insert
    suspend fun insertClothing(clothing: Clothes)

    @Query("SELECT * FROM clothes ORDER BY storageTime DESC")
    suspend fun retrieveAllClothes(): List<Clothes>

    @Delete
    suspend fun deleteClothing(clothes: Clothes)

    @Query("SELECT * FROM clothes WHERE clothingType LIKE :type AND dominantColor LIKE :color AND currentStatus LIKE :status AND itemCode LIKE :code ORDER BY storageTime DESC")
    suspend fun queryClothes(
        type: String,
        color: String,
        status: String,
        code: String
    ): List<Clothes>

    @Query("SELECT COUNT(*) FROM clothes WHERE itemCode = :code")
    suspend fun checkCode(code: String) = Int


}