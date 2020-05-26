package com.example.photoapp.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.adapters.MoveRecyclerViewAdapter
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.delete.view.*
import kotlinx.android.synthetic.main.move_image.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageFragment : Fragment(){
    //declare class variables
    private val TAG = "ImageFragment"
    private var listener: OnFragmentInteractionListener? = null
    lateinit var recyclerView: RecyclerView
    private var albumList = listOf<Album>()
    private lateinit var db: PhotoDatabase
    private var image: Photo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_image, container, false)
        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE

        //back button returns to last fragment on stack
        val backButton = fragView.findViewById(R.id.back_btn) as FloatingActionButton
        backButton.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
            backButton.isEnabled = false
        }

        //read in values set in bundle
        val arg = arguments
        val getImage = arg!!.getString("image")
        val getImageID = arg.getInt("imageID")

        //read image file to decode as bitmap and set in image view
        val path = File(Environment.getExternalStorageDirectory().toString()+"/images/", getImage)
        val bitmap = BitmapFactory.decodeFile(path.absolutePath)
        val imageView = fragView.findViewById(R.id.imageFull) as ImageView
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 900,800, false))
        Log.i(TAG, bitmap.toString())

        db = PhotoDatabase.getDatabase(activity!!)

        //set move album button with dialog box on click
        val moveBtn = fragView.findViewById(R.id.moveBtn) as FloatingActionButton
        moveBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(context!!).inflate(R.layout.move_image, null)
            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .setTitle("Move To...")
            //coroutine reads list of album and displays them in a recycler view
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    albumList = db.photoDAO().getAllAlbums()
                }
                recyclerView =  dialogView.findViewById(R.id.recyclerView) as RecyclerView
                recyclerView.layoutManager = LinearLayoutManager(activity)
                val recyclerViewAdapter = MoveRecyclerViewAdapter(getImageID, context!!, albumList)
                recyclerView.adapter = recyclerViewAdapter
                val alert = builder.show()
                //cancel button closes dialog box
                dialogView.cancel_btn1.setOnClickListener {
                    alert.dismiss()
                }
            }
        }
        //delete button displays dialog asking if user wants to delete
        val deleteBtn = fragView.findViewById(R.id.delete_btn) as FloatingActionButton
        deleteBtn.setOnClickListener{
            val dialogView = LayoutInflater.from(context!!).inflate(R.layout.delete, null)
            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .setTitle("Delete Image?")
            val alert = builder.show()

            //if user clicks yes image object is read from database and deleted
            dialogView.yes_btn.setOnClickListener {
                alert.dismiss()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        image = db.photoDAO().getImageByID(getImageID)
                    }
                    //checks image exists
                    if(image != null) {
                    var photoID: Int? = null
                    withContext(Dispatchers.IO) {
                        photoID = db.photoDAO().delete(image!!)
                    }
                    Log.i(TAG, photoID.toString())
                    activity!!.supportFragmentManager.popBackStack()
                    Toast.makeText(activity, "Image Deleted", Toast.LENGTH_LONG).show()
                    //image is deleted from file directory
                    Handler().postDelayed({
                        path.delete()
                    }, 200)
                    }
                }
            }
            //close dialog box
            dialogView.no_btn.setOnClickListener {
                alert.dismiss()
            }
        }
        return fragView
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
        fun newInstance() = ImageFragment()

    }
}