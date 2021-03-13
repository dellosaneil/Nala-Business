package com.example.nalasbusinesstracker.room.dao

import androidx.lifecycle.LiveData
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

    @Query("UPDATE clothes SET currentStatus = :status WHERE itemCode = :code")
    suspend fun updateStatus(status : String, code : String)


    @Query("SELECT COUNT(*) FROM clothes WHERE itemCode = :code")
    suspend fun checkCode(code: String) : Int

    @Query("SELECT DISTINCT clothingType FROM clothes ORDER BY storageTime")
    fun getDistinctClothingType() : LiveData<List<String>>

    @Query("SELECT DISTINCT dominantColor FROM clothes ORDER BY storageTime")
    fun getDistinctDominantColor() : LiveData<List<String>>

    @Query("SELECT DISTINCT supplierName FROM clothes ORDER BY storageTime")
    fun getDistinctSupplierName() : LiveData<List<String>>



}