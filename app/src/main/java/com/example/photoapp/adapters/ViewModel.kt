package com.example.photoapp.adapters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase

class ViewModel(app: Application): AndroidViewModel(app) {

    var db = PhotoDatabase.getDatabase(app)
    private var albums: LiveData<List<Album>>
    private var images: LiveData<List<Photo>>
    private var imageByAlbum: LiveData<List<Photo>>
    private var id = 0

    init {
        albums = db.photoDAO().getAllAlbumsLive()
        images = db.photoDAO().getAllImages()
        imageByAlbum = db.photoDAO().getImageByAlbum(id)
    }

    //fun insert(album: Album) {
     //   db.photoDAO().insertAlbum(album)
   // }

    fun getAllAlbumsLive(): LiveData<List<Album>> {
        return albums
    }

    fun getAllImages(): LiveData<List<Photo>> {

        return images
    }

    fun getImageByAlbum(albumID: Int): LiveData<List<Photo>> {
        id = albumID
        return imageByAlbum
    }
}