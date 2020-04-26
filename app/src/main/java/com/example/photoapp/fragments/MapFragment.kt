package com.example.photoapp.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import org.osmdroid.config.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.photoapp.R
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.io.File


class MapFragment : Fragment() {
    //declare class variables
    private val TAG = "MapFragment"
    private var listener: OnFragmentInteractionListener? = null
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    lateinit var markerGestureListener: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
    lateinit var mv: MapView
    private var imageList = listOf<Photo>()
    private lateinit var db: PhotoDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_map, container, false)
        //configuration for OSMDroid
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE

        //define map view
        val map = fragView.findViewById(R.id.map1) as MapView
        mv = map

        //call to set map and marker initialise functions
        markerInit()
        mapInit()

        ///val path = File(Environment.getExternalStorageDirectory().toString()+"/images/", "mpldam.jpg")
        //val bitmap = BitmapFactory.decodeFile(path.absolutePath)

        //val bitmapCompress = bitmap
        //val stream = ByteArrayOutputStream()
        // bitmapCompress.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        //val imageStream = stream.toByteArray()
        //val image = BitmapFactory.decodeByteArray(imageStream, 0, imageStream.size)
        //Bitmap.createScaledBitmap(image, 1,1, false)
        //val icon = BitmapDrawable(resources, image)
        //icon.setBounds(0, 0, 0 + icon.intrinsicWidth, 0 + icon.intrinsicHeight)
        val activity1 = activity as Context
        //read database for location of all photos
        db = PhotoDatabase.getDatabase(activity1)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                imageList = db.photoDAO().getAllImages()
            }
            //display each photo marker on map
            for (data in imageList) {
                val marker = ContextCompat.getDrawable(context!!, R.drawable.photo_marker)
                val photoLocation = OverlayItem(data.id.toString(), data.image, GeoPoint(data.latitude.toDouble(), data.longitude.toDouble()))
                items = ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), markerGestureListener)
                photoLocation.setMarker(marker)
                items.addItem(photoLocation)
                mv.overlays.add(items)
            }
        }
        //button takes you to camera fragment
        val cameraBtn = fragView.findViewById(R.id.camera_btn) as FloatingActionButton
        cameraBtn.setOnClickListener{
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val fragment = CameraFragment.newInstance()
            transaction.replace(R.id.page_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        return fragView
    }

    private fun mapInit(){
        //set touch controls and default view
        mv.setMultiTouchControls(true)
        val mvController = mv.controller
        mvController.setZoom(10)
        mvController.setCenter(GeoPoint( 50.909698, -1.404351))
    }


    private fun markerInit() {
        //on marker clicked actions
        markerGestureListener = object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {

            override fun onItemLongPress(i: Int, item: OverlayItem): Boolean {
                //show image name on long press
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                Log.i(TAG, item.snippet+".jpg")
                return true
            }

            override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {
                //on short press display dialog box with image inside
                Log.i(TAG, item.snippet+".jpg")
                val dialog = Dialog(context!!)
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.map_image)
                dialog.setTitle("Selected Image")
                dialog.setCancelable(true)

                val path = File(Environment.getExternalStorageDirectory().toString()+"/images/", item.snippet+".jpg")
                val bitmap = BitmapFactory.decodeFile(path.absolutePath)
                val image = dialog.findViewById(R.id.map_imageView) as ImageView
                image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200,200, false))
                image.setOnClickListener {
                    //on image click display selected image in image fragment
                    val imageFragment = ImageFragment()
                    val args = Bundle()
                    args.putString("image", item.snippet+".jpg")
                    args.putInt("imageID", item.title.toInt())
                    imageFragment.arguments = args
                    val transaction = activity!!.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.page_fragment, imageFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                    dialog.dismiss()
                }
                dialog.show()
                return true
            }
        }
    }







    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance() = MapFragment()

    }
}
