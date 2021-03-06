package com.example.photoapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.photoapp.datahandling.PhotoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Job


class MoveRecyclerViewAdapter (private val imageID: Int, private val c: Context, private val albums: List<Album>): CoroutineScope, RecyclerView.Adapter<MoveRecyclerViewAdapter.MyViewHolder>() {
    //declare class variables
    private val TAG = "MoveRecycler"
    private var image: Photo? = null
    private var job = Job()
    //define coroutine
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(c).inflate(R.layout.single_move, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //set album name as text view and on click listener to move album
        val getAlbum = albums[position].name
        holder.textView.text = getAlbum
        Log.i(TAG, getAlbum)
        val db = PhotoDatabase.getDatabase(holder.textView.context as FragmentActivity)
        holder.textView.setOnClickListener {
            //coroutine retrieves the image object from the ID passed in
            launch {
                withContext(Dispatchers.IO) {
                    image = db.photoDAO().getImageByID(imageID)
                }
                //checks if image exists and then sets the new album value and sends update query to database
                if(image != null) {
                    image!!.album = albums[position].id
                    var photoID: Int? = null
                    withContext(Dispatchers.IO) {
                    photoID = db.photoDAO().update(image!!)
                    }
                    Log.i(TAG, photoID.toString())
                Toast.makeText(holder.textView.context as FragmentActivity, "Image Moved to "+albums[position].name, Toast.LENGTH_LONG).show()
            }}

        }
    }


    override fun getItemCount(): Int {
        //get list size
        return albums.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //set views to be held in view holder
        val textView = view.findViewById(R.id.textView) as TextView

    }


}