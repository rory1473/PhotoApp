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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.photoapp.adapters.AlbumRecyclerViewAdapter
import com.example.photoapp.adapters.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.add_album.view.*


class AlbumFragment : Fragment(){
    //declare class variables
    private val TAG = "AlbumFragment"
    private var listener: OnFragmentInteractionListener? = null
    private var albumList = listOf<Album>()
    private lateinit var db: PhotoDatabase
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_album, container, false)
        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView =  fragView.findViewById(R.id.recyclerView) as RecyclerView

        //button to show all photos in PhotoFragment
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

        //add album button dialog box asks user to enter a name
        val addBtn = fragView.findViewById(R.id.add_album_btn) as FloatingActionButton
        addBtn.setOnClickListener{
            val dialogView = LayoutInflater.from(context!!).inflate(R.layout.add_album, null)
            val builder = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .setTitle("Add An Album")

            val alert = builder.show()
            //if text box is not empty new album is inserted into database
            dialogView.add_btn.setOnClickListener {
                val album = dialogView.album_name.text.toString()
                if (album.isEmpty()) {
                    dialogView.album_name.error = "Please Enter The Album Name"
                } else {
                    lifecycleScope.launch {
                        var albumID: Long? = null
                        val newAlbum = Album(name = album)
                        withContext(Dispatchers.IO) {
                            albumID = db.photoDAO().insertAlbum(newAlbum)
                        }
                    }
                    alert.dismiss()
                }
            }
            //close add album dialog
            dialogView.cancel_btn.setOnClickListener {
                alert.dismiss()
            }
        }

        //button to take user to CameraFragment
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

        //define view model
        val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)
        //read live data of albums in view model
        viewModel.getAllAlbumsLive().observe(this, Observer<List<Album>> {
            albumList = it
            //send list of albums to recycler adapter
            Log.i(TAG, albumList.toString())
            recyclerView.layoutManager = LinearLayoutManager(activity1)
            val recyclerViewAdapter = AlbumRecyclerViewAdapter(context!!, albumList)
            recyclerView.adapter = recyclerViewAdapter
        })
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
