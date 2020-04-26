package com.example.photoapp.adapters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase

class ViewModel(app: Application): AndroidViewModel(app) {

    //declares variables
    var db = PhotoDatabase.getDatabase(app)
    private var albums: LiveData<List<Album>>
    private var images: LiveData<List<Photo>>

    init {
        //variables are assigned to DAO functions
        albums = db.photoDAO().getAllAlbumsLive()
        images = db.photoDAO().getAllImagesLive()
    }

    fun getAllAlbumsLive(): LiveData<List<Album>> {
        //function called by album fragment to return live album data
        return albums
    }

    fun getAllImagesLive(): LiveData<List<Photo>> {
        return images
    }


}