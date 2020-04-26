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
import android.view.animation.AnimationUtils

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch

import java.io.File


class CameraFragment : Fragment() {
    //declare class variables
    private val TAG = "CameraFragment"
    private var listener: CameraFragmentListener? = null
    private var fotoapparat: Fotoapparat? = null
    private var fotoapparatState : FotoapparatState? = null
    private var cameraStatus : CameraState? = null
    private var flashState: FlashState? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_camera, container, false)
        //set bottom navigation as invisible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.INVISIBLE

        //define cameraView
        val cameraView = fragView.findViewById<CameraView>(R.id.cameraView)
        fotoapparat = Fotoapparat( context = context!!, view = cameraView)

        //back button returns to last fragment on stack
        val backButton = fragView.findViewById(R.id.back_button) as FloatingActionButton
        backButton.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotation))
        backButton.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
            backButton.isEnabled = false
        }

        //button calls takePhoto()
        val takePhoto = fragView.findViewById(R.id.take_photo) as FloatingActionButton
        takePhoto.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotation))
        takePhoto.setOnClickListener {
            takePhoto()
        }
        //button calls changeFlash()
        val flash = fragView.findViewById(R.id.flash) as FloatingActionButton
        flash.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotation))
        flash.setOnClickListener {
            changeFlash()
        }
        //button calls switchCamera()
        val switchCamera = fragView.findViewById(R.id.switch_camera) as FloatingActionButton
        switchCamera.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotation))
        switchCamera.setOnClickListener {
            switchCamera()
        }
        //default values of camera properties
        cameraStatus = CameraState.BACK
        flashState = FlashState.OFF
        fotoapparatState = FotoapparatState.OFF

        return fragView
    }

    private fun takePhoto(){
        //takes photo and saves to file, filename is passed to through interface MainActivity to be saved to database
        val stringLibrary = ('a'..'z').toList().toTypedArray()
        val random = (1..6).map { stringLibrary.random() }.joinToString("")
        val filename = random+".jpg"
        val sd = Environment.getExternalStorageDirectory().toString()+"/images/"
        val dest = File(sd, filename)

        fotoapparat?.takePicture()?.saveToFile(dest)

        //val path = File(Environment.getExternalStorageDirectory(),"newPhoto.jpg").toString()
       // Log.i("BBBBBBB", path)
       // val bitmap = BitmapFactory.decodeFile(path)
        //val stream = ByteArrayOutputStream()
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
        //val image = stream.toByteArray()

        listener?.photoInterface(random)
        Log.i(TAG, random)

    }


    private fun switchCamera(){
        //switches between front and back camera
        fotoapparat?.switchTo(
            lensPosition =  if (cameraStatus == CameraState.BACK) front() else back(),
            cameraConfiguration = CameraConfiguration()
        )
        if(cameraStatus == CameraState.BACK) {
            cameraStatus = CameraState.FRONT
        }
        else {cameraStatus = CameraState.BACK
        }
    }


    private fun changeFlash(){
        //turns flash on and off
        fotoapparat?.updateConfiguration(
            CameraConfiguration(
                flashMode = if(flashState == FlashState.TORCH) off() else torch()
            )
        )
        if(flashState == FlashState.TORCH) flashState = FlashState.OFF
        else flashState = FlashState.TORCH
    }



    override fun onStart() {
        super.onStart()
        //start fotoapparat
        fotoapparat!!.start()
    }
    override fun onStop() {
        super.onStop()
        //stops fotoapparat on fragment close
        fotoapparat!!.stop()

    }


    //set fotoapparat property options
    enum class CameraState{
        FRONT, BACK
    }
    enum class FlashState{
        TORCH, OFF
    }
    enum class FotoapparatState{
        ON, OFF
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
        fun photoInterface(newPhoto: String)

    }

    companion object {

        @JvmStatic
        fun newInstance() = CameraFragment()
    }
}