package com.example.photoapp.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import org.osmdroid.config.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.photoapp.R
import com.example.photoapp.adapters.MapItemOverlay
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.io.ByteArrayOutputStream
import java.io.File


class MapFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    lateinit var markerGestureListener: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
    lateinit var mv: MapView
    private var imageList = listOf<Photo>()
    private lateinit var db: PhotoDatabase


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_map, container, false)

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        val map = fragView.findViewById(R.id.map1) as MapView
        mv = map

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

        db = PhotoDatabase.getDatabase(activity1)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                imageList = db.photoDAO().getAllImages()
            }

            for (data in imageList) {

                val marker = ContextCompat.getDrawable(context!!, R.drawable.photo_marker)
                //val itemOverlay =  MapItemOverlay(marker!!, context!!)
                val photoLocation = OverlayItem("Photo", "Photo", GeoPoint(data.lat, data.long))
                //itemOverlay.addOverlay(photoLocation)
                //mv.overlays.add(itemOverlay)
                items = MapItemOverlay(data.image, context!!, activity!!, ArrayList<OverlayItem>(), markerGestureListener)
                //val curLocation = OverlayItem("gg", "gg", GeoPoint(50.909698, -1.404351))
                //val drawable = ContextCompat.getDrawable(context!!, gg)
                photoLocation.setMarker(marker)
                items.addItem(photoLocation)
                mv.overlays.add(items)
            }
        }



        return fragView
    }

    private fun mapInit(){

        mv.setBuiltInZoomControls(true)
        mv.setMultiTouchControls(true)

        val mvController = mv.controller
        mvController.setZoom(10)
        mvController.setCenter(GeoPoint( 50.909698, -1.404351))

    }


    private fun markerInit() {
        markerGestureListener = object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {

            override fun onItemLongPress(i: Int, item: OverlayItem): Boolean {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
        }



    }



    //fun showmarker(lat: Double, long: Double)
    //{
    //    val marker = ContextCompat.getDrawable(context!!, R.drawable.photo_marker )
//
     //   val itemOverlay =  MapItemOverlay(marker!!, context!!)
//
     //   val photoLocation = OverlayItem("Photo","Photo", GeoPoint(lat, long))
//
     //   itemOverlay.addOverlay(photoLocation)
     //   mv.overlays.add(itemOverlay)
    //}






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
