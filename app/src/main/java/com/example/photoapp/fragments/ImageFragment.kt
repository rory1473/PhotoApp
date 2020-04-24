package com.example.photoapp.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.adapters.MoveRecyclerViewAdapter
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.move_image.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageFragment : Fragment(){

    private var listener: OnFragmentInteractionListener? = null
    lateinit var recyclerView: RecyclerView
    private var albumList = listOf<Album>()
    private lateinit var db: PhotoDatabase
    lateinit var gg: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_image, container, false)



        val arg = arguments
        val getImage = arg!!.getString("image")
        val getImageID = arg!!.getInt("imageID")


        val path = File(Environment.getExternalStorageDirectory().toString()+"/images/", getImage)
        val bitmap = BitmapFactory.decodeFile(path.absolutePath)

        val imageView = fragView.findViewById(R.id.imageFull) as ImageView
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200,200, false))



        val moveBtn = fragView.findViewById(R.id.moveBtn) as FloatingActionButton
        moveBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(context!!).inflate(R.layout.move_image, null)
            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .setTitle("Move To...")


            db = PhotoDatabase.getDatabase(activity!!)

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    albumList = db.photoDAO().getAllAlbums()
                }
                recyclerView =  dialogView.findViewById(R.id.recyclerView) as RecyclerView
                recyclerView.layoutManager = LinearLayoutManager(activity)
                val recyclerViewAdapter =
                    MoveRecyclerViewAdapter(getImageID, context!!, albumList)
                recyclerView.adapter = recyclerViewAdapter
                val alert = builder.show()

                dialogView.cancel_btn1.setOnClickListener {
                    alert.dismiss()
                }




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