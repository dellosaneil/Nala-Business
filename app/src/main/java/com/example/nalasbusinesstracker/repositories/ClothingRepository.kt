package com.example.nalasbusinesstracker.repositories

import com.example.nalasbusinesstracker.room.dao.ClothesDao
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import javax.inject.Inject

class ClothingRepository @Inject constructor(private val clothingDao : ClothesDao) {

    suspend fun insertClothing(clothing : Clothes) = clothingDao.insertClothing(clothing)
    suspend fun retrieveAllClothing() = clothingDao.retrieveAllClothes()
    suspend fun deleteClothing(clothing: Clothes) = clothingDao.deleteClothing(clothing)
    suspend fun queryClothes(type : String, color : String, status : String, code :String) = clothingDao.queryClothes(type, color, status, code)



}
