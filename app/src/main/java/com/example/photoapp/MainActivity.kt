package com.example.photoapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.room.Database
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.example.photoapp.fragments.CameraFragment
import com.example.photoapp.fragments.MapFragment
import com.example.photoapp.fragments.PhotoFragment
import io.fotoapparat.result.BitmapPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), MapFragment.OnFragmentInteractionListener, PhotoFragment.OnFragmentInteractionListener, CameraFragment.CameraFragmentListener  {

    private val fm = supportFragmentManager

    private lateinit var db: PhotoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

         db = PhotoDatabase.getDatabase(application)


        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        showCameraFragment()
    }



    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_photos -> {
                showPhotoFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                showMapFragment()
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }


    private fun showPhotoFragment() {

        val fragment = PhotoFragment.newInstance()
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun showMapFragment() {
        val transaction = fm.beginTransaction()
        val fragment = MapFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun showCameraFragment() {
        val transaction = fm.beginTransaction()
        val fragment = CameraFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun photoInterface(newPhoto: ByteArray){
        Log.i("CCCCCCC", newPhoto.toString())
        lifecycleScope.launch {
            var photoID: Long? = null
            val newImage = Photo(image= newPhoto)
            withContext(Dispatchers.IO) {
                photoID = db.photoDAO().insert(newImage)
            }

        }
    }


    override fun onFragmentInteraction(uri: Uri) {

    }









    override fun onStart() {
        super.onStart()
        getPermissions()
    }


    private fun getPermissions(){

        if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

        }
        else{
            requestPermissions(arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ), 0)
        }
    }



    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults: IntArray) {
        when(requestCode) {
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions()

                } else {
                    Toast.makeText(this, "Camera, file read and file write permissions required to use this app", Toast.LENGTH_LONG).show();
                }
            }
        }

    }




}