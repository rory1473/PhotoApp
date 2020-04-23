package com.example.photoapp.datahandling

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.fotoapparat.result.BitmapPhoto

@Entity(tableName="images")

data class Photo(@PrimaryKey(autoGenerate = true) val id: Int = 0,  val image: String)




    //, val album: Int









