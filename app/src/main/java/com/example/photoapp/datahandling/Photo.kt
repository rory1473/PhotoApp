package com.example.photoapp.datahandling

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.fotoapparat.result.BitmapPhoto

@Entity(tableName="images")

data class Photo(@PrimaryKey(autoGenerate = true)val id: Long = 0, val image: BitmapPhoto, val album: Int)







