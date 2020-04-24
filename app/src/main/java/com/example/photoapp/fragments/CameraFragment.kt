package com.example.photoapp.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.photoapp.R
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.fotoapparat.Fotoapparat
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.view.CameraView
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

import android.widget.ImageView

import java.io.File


class CameraFragment : Fragment() {
    private var listener: CameraFragmentListener? = null
    var fotoapparat: Fotoapparat? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_camera, container, false)

        val take_photo = fragView.findViewById(R.id.take_photo) as FloatingActionButton

        val cameraView = fragView.findViewById<CameraView>(R.id.cameraView1)

        fotoapparat = Fotoapparat( context = context!!, view = cameraView)



        take_photo.setOnClickListener {
            takePhoto()
        }

        return fragView
    }



    private fun takePhoto(){

        val stringLibrary = ('a'..'z').toList().toTypedArray()
        val random = (1..6).map { stringLibrary.random() }.joinToString("")

        val filename = random+".jpg"
        val sd = Environment.getExternalStorageDirectory().toString()+"/images/"
        val dest = File(sd, filename)

        fotoapparat?.takePicture()?.saveToFile(dest)




        val path = File(Environment.getExternalStorageDirectory(),"newPhoto.jpg").toString()
       // Log.i("BBBBBBB", path)
        val bitmap = BitmapFactory.decodeFile(path)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
        val image = stream.toByteArray()

        listener?.photoInterface(random)
        Log.i("AAAAAAA", random)


    }



    override fun onStart() {
        super.onStart()
        fotoapparat!!.start()
    }
    override fun onStop() {
        super.onStop()
        fotoapparat!!.stop()
    }





















    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CameraFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }




    interface CameraFragmentListener {
        fun photoInterface(newPhoto: String) //ByteArray

    }



    companion object {

        @JvmStatic
        fun newInstance() = CameraFragment()
    }
}
