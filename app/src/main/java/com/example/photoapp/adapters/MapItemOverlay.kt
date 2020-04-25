package com.example.photoapp.adapters

import android.app.Activity
import org.osmdroid.api.IMapView
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import org.osmdroid.views.overlay.OverlayItem
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import android.view.Window
import android.widget.ImageView
import com.example.photoapp.R
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import java.io.File


class MapItemOverlay(val imageName: String, val context: Context, activity: Activity, markerGestureListener: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>): ItemizedIconOverlay<OverlayItem>(activity, ArrayList<OverlayItem>(), markerGestureListener)//ItemizedOverlay<OverlayItem>(defaultMarker)//(val activity: Activity, ArrayList<OverlayItem>()){//(defaultMarker, DefaultResourceProxyImpl(mContext))
{
    private val mOverlays = ArrayList<OverlayItem>()



    override fun onTap(index: Int): Boolean {
        val item = mOverlays[index]

        Log.d("Title", item.title)
        Log.d("Snippet", item.snippet)
        Log.d("Id", item.uid)


        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setContentView(R.layout.map_image)
        //dialog.setTitle("This is my custom dialog box")

        dialog.setCancelable(true)
        //there are a lot of settings, for dialog, check them all out!


        val path = File(Environment.getExternalStorageDirectory().toString()+"/images/", imageName+".jpg")
        val bitmap = BitmapFactory.decodeFile(path.absolutePath)
        val image = dialog.findViewById(R.id.map_imageView) as ImageView
        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200,200, false))

        image.setOnClickListener {
            Log.d("Clicked", "more info")
        }
        //now that the dialog is set up, it's time to show it
        dialog.show()

        return true
    }




}
