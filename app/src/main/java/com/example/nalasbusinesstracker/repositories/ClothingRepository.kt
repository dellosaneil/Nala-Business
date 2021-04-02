package com.example.nalasbusinesstracker.repositories

import com.example.nalasbusinesstracker.room.dao.ClothesDao
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class ClothingRepository @Inject constructor(private val clothingDao : ClothesDao) {

    suspend fun insertClothing(clothing : Clothes) = clothingDao.insertClothing(clothing)
    fun retrieveAllClothing() = clothingDao.retrieveAllClothes()
    suspend fun deleteClothing(clothing: Clothes) = clothingDao.deleteClothing(clothing)
    fun queryClothes(type : String, color : String, status : String, code :String) = clothingDao.queryClothes(type, color, status, code)
    suspend fun checkCode(code : String)  = clothingDao.checkCode(code)
    suspend fun updateStatus(status: String, code : String) = clothingDao.updateStatus(status, code)

    fun getDistinctClothingType() = clothingDao.getDistinctClothingType()
    fun getDistinctDominantColor() = clothingDao.getDistinctDominantColor()
    fun getDistinctSupplierName() = clothingDao.getDistinctSupplierName()






}
