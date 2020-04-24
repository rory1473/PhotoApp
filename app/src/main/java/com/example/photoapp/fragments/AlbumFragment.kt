package com.example.photoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.DialogInterface
import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.add_album.view.*


class AlbumFragment : Fragment(){

    private var listener: OnFragmentInteractionListener? = null
    private var albumList = listOf<Album>()
    private lateinit var db: PhotoDatabase
    lateinit var recyclerView: RecyclerView



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_album, container, false)

        recyclerView =  fragView.findViewById(R.id.recyclerView) as RecyclerView

        val allPhotos = fragView.findViewById(R.id.all_albums) as TextView
        allPhotos.setOnClickListener {
            val photos = true
            val photoFragment = PhotoFragment()
            val args = Bundle()
            args.putBoolean("allPhotos", photos)
            photoFragment.arguments = args
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.page_fragment, photoFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }








        val addBtn = fragView.findViewById(R.id.add_album_btn) as FloatingActionButton
        addBtn.setOnClickListener{
            val dialogView = LayoutInflater.from(context!!).inflate(R.layout.add_album, null)
            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .setTitle("Add An Album")

            val alert = builder.show()
            dialogView.add_btn.setOnClickListener {
                alert.dismiss()
                val album = dialogView.album_name.text.toString()

                lifecycleScope.launch {
                    var albumID: Long? = null
                    val newAlbum = Album(name= album)
                    withContext(Dispatchers.IO) {
                        albumID = db.photoDAO().insertAlbum(newAlbum)
                    }

                }
            }
            dialogView.cancel_btn.setOnClickListener {
                alert.dismiss()
            }

        }



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




    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)

        val activity1 = activity as Context

        db = PhotoDatabase.getDatabase(activity1)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                albumList = db.photoDAO().getAllAlbums()
            }

            Log.i("DDDDDD",albumList.toString())
            recyclerView.layoutManager = LinearLayoutManager(activity1)
            val recyclerViewAdapter = AlbumRecyclerViewAdapter(context!!,albumList)
            recyclerView.adapter = recyclerViewAdapter

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
        fun newInstance() = AlbumFragment()

    }
}
