package com.example.nalasbusinesstracker.room.data_classes

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Clothes(
    @PrimaryKey
    val itemCode: String,
    val clothingType: String,
    val dominantColor: String,
    val purchasePrice: Double,
    val sellingPrice : Double,
    val purchaseDate: Long,
    val currentStatus: String,
    val imageReference: String,
    val clothingSize : Double,
    val supplierName: String? = null,
    val storageTime : Long = System.currentTimeMillis()
) : Parcelable
