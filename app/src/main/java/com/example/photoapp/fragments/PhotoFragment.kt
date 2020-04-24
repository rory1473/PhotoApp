package com.example.photoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.adapters.AlbumRecyclerViewAdapter
import com.example.photoapp.adapters.PhotoRecyclerViewAdapter
import com.example.photoapp.adapters.ViewModel
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class PhotoFragment : Fragment(){

    private var listener: OnFragmentInteractionListener? = null
    private var imageList = listOf<Photo>()
    private lateinit var db: PhotoDatabase
    lateinit var recyclerView: RecyclerView
    lateinit var title: TextView
    var allPhotos = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_photo, container, false)

        val arg = arguments
        allPhotos = arg!!.getBoolean("allPhotos")

        recyclerView =  fragView.findViewById(R.id.recyclerView) as RecyclerView


            title =  fragView.findViewById(R.id.title) as TextView


        val btn1 = fragView.findViewById(R.id.btn1) as FloatingActionButton
        btn1.setOnClickListener{
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

        if (allPhotos) {
            title.text = "All Photos"

            val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)

            viewModel.getAllImages().observe(this, Observer<List<Photo>> {
                imageList = it

                Log.i("DDDDDD", imageList.toString())
                val layoutManager = GridLayoutManager(context!!, 2)
                recyclerView.layoutManager = layoutManager     //LinearLayoutManager(activity1)
                val recyclerViewAdapter = PhotoRecyclerViewAdapter(context!!, imageList)
                recyclerView.adapter = recyclerViewAdapter

            })

            //lifecycleScope.launch {
             //   withContext(Dispatchers.IO) {
             //       imageList = db.photoDAO().getAllImages()
             //   }

             //   Log.i("DDDDDD", imageList.toString())
              //  val layoutManager = GridLayoutManager(context!!, 2)
              //  recyclerView.layoutManager = layoutManager     //LinearLayoutManager(activity1)
              //  val recyclerViewAdapter = PhotoRecyclerViewAdapter(context!!, imageList)
              //  recyclerView.adapter = recyclerViewAdapter



       } else{
            val arg = arguments
            val albumID = arg!!.getInt("ID")
            val albumName = arg.getString("name")
            title.text = albumName

            val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)

            viewModel.getImageByAlbum(albumID).observe(this, Observer<List<Photo>> {
                imageList = it

                Log.i("DDDDDD", imageList.toString())
                val layoutManager = GridLayoutManager(context!!, 2)
                recyclerView.layoutManager = layoutManager
                val recyclerViewAdapter = PhotoRecyclerViewAdapter(context!!, imageList)
                recyclerView.adapter = recyclerViewAdapter

        //    lifecycleScope.launch {
        //        withContext(Dispatchers.IO) {
        //            imageList = db.photoDAO().getImageByAlbum(albumID)
         //       }
//
        //        Log.i("DDDDDD", imageList.toString())
        //        val layoutManager = GridLayoutManager(context!!, 2)
        //        recyclerView.layoutManager = layoutManager     //LinearLayoutManager(activity1)
        //        val recyclerViewAdapter = PhotoRecyclerViewAdapter(context!!, imageList)
        //        recyclerView.adapter = recyclerViewAdapter
//
         //   }
        })
        }

    }


    //fun getAlbum(photos: Boolean){
       // allPhotos = photos
    // }
    //








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
        fun newInstance() = PhotoFragment()

    }
}
