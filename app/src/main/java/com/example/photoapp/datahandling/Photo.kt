package com.example.photoapp.datahandling

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="images")

data class Photo(@PrimaryKey(autoGenerate = true) val id: Int = 0,  val image: String, var album: Int, var latitude: String, var longitude: String)













