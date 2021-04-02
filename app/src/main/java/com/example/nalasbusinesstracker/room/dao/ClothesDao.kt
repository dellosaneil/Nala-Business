package com.example.nalasbusinesstracker.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothesDao {

    @Insert
    suspend fun insertClothing(clothing: Clothes)

    @Query("SELECT * FROM clothes ORDER BY storageTime DESC")
    fun retrieveAllClothes(): Flow<List<Clothes>>

    @Delete
    suspend fun deleteClothing(clothes: Clothes)

    @Query("SELECT * FROM clothes WHERE clothingType LIKE :type AND dominantColor LIKE :color AND currentStatus LIKE :status AND itemCode LIKE :code ORDER BY storageTime DESC")
    fun queryClothes(
        type: String,
        color: String,
        status: String,
        code: String
    ): Flow<List<Clothes>>

    @Query("UPDATE clothes SET currentStatus = :status WHERE itemCode = :code")
    suspend fun updateStatus(status : String, code : String)


    @Query("SELECT COUNT(*) FROM clothes WHERE itemCode = :code")
    suspend fun checkCode(code: String) : Int

    @Query("SELECT DISTINCT clothingType FROM clothes ORDER BY storageTime")
    fun getDistinctClothingType() : Flow<List<String>>

    @Query("SELECT DISTINCT dominantColor FROM clothes ORDER BY storageTime")
    fun getDistinctDominantColor() : Flow<List<String>>

    @Query("SELECT DISTINCT supplierName FROM clothes ORDER BY storageTime")
    fun getDistinctSupplierName() : Flow<List<String>>



}