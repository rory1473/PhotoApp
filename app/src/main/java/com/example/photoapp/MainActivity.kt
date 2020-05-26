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
import android.view.LayoutInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.example.photoapp.fragments.*
import kotlinx.android.synthetic.main.permission_required.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity(), LocationListener, ImageFragment.OnFragmentInteractionListener, AlbumFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener, PhotoFragment.OnFragmentInteractionListener, CameraFragment.CameraFragmentListener  {
    //declare class variables
    private val TAG = "MainActivity"
    private val fm = supportFragmentManager
    private lateinit var db: PhotoDatabase
    var lat = 0.0
    var long = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        //set bottom navigation listener
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        //create file directory for photos
        val path = File(Environment.getExternalStorageDirectory().toString()+"/images")
        path.mkdirs()

        //define database
        db = PhotoDatabase.getDatabase(application)

        showAlbumFragment()
    }


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        //set navigation fragment calls
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


    private fun showMapFragment() {
        //set fragment as MapFragment
        val transaction = fm.beginTransaction()
        val fragment = MapFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun showAlbumFragment() {
        //set fragment as AlbumFragment
        val transaction = fm.beginTransaction()
        val fragment = AlbumFragment.newInstance()
        transaction.replace(R.id.page_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun photoInterface(newPhoto: String){
        //read in photo filename from CameraFragment and insert to database with current location
        if(newPhoto != null) {
            Log.i(TAG, newPhoto)
            lifecycleScope.launch {
               var photoID: Long? = null
               val newImage = Photo(image= newPhoto, album = -1, latitude = lat.toString(), longitude = long.toString())
               withContext(Dispatchers.IO) {
                   photoID = db.photoDAO().insert(newImage)
               }
               Log.i(TAG, photoID.toString())
            }
        }
    }

    //empty fragment interface required by main activity
    override fun onFragmentInteraction(uri: Uri) {
    }


    override fun onStart() {
        super.onStart()
        //call getPermissions() on startup
        getPermissions()
    }


    private fun getPermissions(){
        //check all permissions are granted and request location
        if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
        else{
            //request permissions
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ), 0)
        }
    }


    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults: IntArray) {
        //if all permissions are granted call getPermissions, otherwise alert user and loop back to getPermissions until user grants permissions
        when(requestCode) {
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    getPermissions()

                } else {
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.permission_required, null)
                    val builder = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("Permissions Needed")
                        .setMessage("You have denied permissions that are required to use this app, to use the app please grant them")
                        .setCancelable(false)
                    val alert = builder.show()
                    dialogView.ok_btn.setOnClickListener {
                        alert.dismiss()
                        getPermissions()
                    }
                }
            }
        }

    }


    override fun onLocationChanged(newLoc: Location) {
        //set class variables as current location
        Log.i(TAG+" latitude", newLoc.latitude.toString())
        Log.i(TAG+" longitude", newLoc.longitude.toString())
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