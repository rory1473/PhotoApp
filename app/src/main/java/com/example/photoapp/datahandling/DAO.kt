package com.example.photoapp.datahandling

import androidx.room.*

@Dao
interface PhotoDAO {

    @Query("SELECT * FROM images WHERE album=:album")
    fun getImageByAlbum(album: Int): List<Photo>

    @Query("SELECT * FROM images")
    fun getAllImages(): List<Photo>

    @Insert
    fun insert(images: Photo) : Long

    @Update
    fun update(images: Photo) : Int

    @Delete
    fun delete(images: Photo) : Int



    @Query("SELECT * FROM albums")
    fun getAllAlbums(): List<Album>

    @Insert
    fun insertAlbum(albums: Album) : Long

    @Update
    fun updateAlbum(albums: Album) : Int

    @Delete
    fun deleteAlbum(albums: Album) : Int


}