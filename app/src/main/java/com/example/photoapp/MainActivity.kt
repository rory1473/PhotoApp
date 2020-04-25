package com.example.photoapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.example.photoapp.fragments.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity(), LocationListener, ImageFragment.OnFragmentInteractionListener, AlbumFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener, PhotoFragment.OnFragmentInteractionListener, CameraFragment.CameraFragmentListener  {

    private val fm = supportFragmentManager
    private lateinit var db: PhotoDatabase
    var lat = 0.0
    var long = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val path = File(Environment.getExternalStorageDirectory().toString()+"/images")
        path.mkdirs()

         db = PhotoDatabase.getDatabase(application)


        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        showAlbumFragment()
    }



    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_photos -> {
                showAlbumFragment()
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

    private fun showAlbumFragment() {
        val transaction = fm.beginTransaction()
        val fragment = AlbumFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun photoInterface(newPhoto: String){
        Log.i("CCCCCCC", newPhoto)
        lifecycleScope.launch {
            var photoID: Long? = null
            val newImage = Photo(image= newPhoto, album = 0, latitude = lat.toString(), longitude = long.toString())
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
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
        else{
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ), 0)
        }
    }



    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults: IntArray) {
        when(requestCode) {
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions()

                } else {
                    Toast.makeText(this, "Camera, file read and file write permissions required to use this app", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    override fun onLocationChanged(newLoc: Location) {
        Log.i("latitude", newLoc.latitude.toString())
        Log.i("longitude", newLoc.longitude.toString())
        lat = newLoc.latitude
        long = newLoc.longitude
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(
            this, "Provider " + provider +
                    " disabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(
            this, "Provider " + provider +
                    " enabled", Toast.LENGTH_LONG
        ).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        Toast.makeText(
            this, "Status changed: $status",
            Toast.LENGTH_LONG
        ).show()
    }


}