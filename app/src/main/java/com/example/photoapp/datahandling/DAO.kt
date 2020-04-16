package com.example.photoapp.datahandling

import androidx.room.*

@Dao
interface PhotoDAO {

    @Query("SELECT * FROM images WHERE id=:id")
    fun getImageById(id: Long): Photo?

    @Query("SELECT * FROM images")
    fun getAllImages(): List<Photo>

    @Insert
    fun insert(images: Photo) : Long

    @Update
    fun update(images: Photo) : Int

    @Delete
    fun delete(images: Photo) : Int
}