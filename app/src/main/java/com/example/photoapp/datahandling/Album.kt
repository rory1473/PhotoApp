package com.example.photoapp.datahandling

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="albums")

data class Album(@PrimaryKey(autoGenerate = true) val id: Int = 0, val name: String)