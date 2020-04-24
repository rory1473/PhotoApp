package com.example.photoapp.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.lifecycle.lifecycleScope


class MoveRecyclerViewAdapter(private val c: Context, private val albums: List<Album>): RecyclerView.Adapter<MoveRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(c).inflate(R.layout.single_move, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val getAlbum = albums[position].name
        holder.textView.text = getAlbum
        Log.i("Album", getAlbum)

        holder.textView.setOnClickListener {
            //lifecycleScope.launch {
            //var albumID: Long? = null
            //val newAlbum = Album(name= album)
            //   withContext(Dispatchers.IO) {
            //albumID = db.photoDAO().insertAlbum(newAlbum)

        }
    }


    override fun getItemCount(): Int {
        return albums.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textView = view.findViewById(R.id.textView) as TextView

    }


}