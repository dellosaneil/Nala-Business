package com.example.nalasbusinesstracker.data_classes

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Clothes(
    @PrimaryKey
    val itemCode: String,
    val clotheType: String,
    val dominantColor: String,
    val purchasePrice: Double,
    val purchaseDate: Long,
    val currentStatus: String,
    val imageReference: String,
    val clotheSize : Double,
    val supplierName: String? = null,
    val storageTime : Long = System.currentTimeMillis()
) : Parcelable
