package com.example.photoapp.datahandling


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhotoDAO {

    @Query("SELECT * FROM images WHERE album=:album")
    fun getImageByAlbum(album: Int): LiveData<List<Photo>>

    @Query("SELECT * FROM images WHERE id=:id")
    fun getImageByID(id: Int): Photo?

    @Query("SELECT * FROM images")
    fun getAllImages(): List<Photo>

    @Query("SELECT * FROM images")
    fun getAllImagesLive(): LiveData<List<Photo>>

    @Insert
    fun insert(images: Photo) : Long

    @Update
    fun update(images: Photo) : Int

    @Delete
    fun delete(images: Photo) : Int



    @Query("SELECT * FROM albums")
    fun getAllAlbums(): List<Album>

    @Query("SELECT * FROM albums")
    fun getAllAlbumsLive(): LiveData<List<Album>>

    @Insert
    fun insertAlbum(albums: Album) : Long

    @Update
    fun updateAlbum(albums: Album) : Int

    @Delete
    fun deleteAlbum(albums: Album) : Int


}