package com.example.photoapp.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Photo


class RecyclerViewAdapter(private val c: Context, private val images: List<Photo>): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(c).inflate(R.layout.single_photo, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       lateinit var bitmap: Bitmap
       for(i in images){
           val getImage = images[position].image
           bitmap = BitmapFactory.decodeByteArray(getImage, 0, getImage.size)

       }

        //val path = images[position]
        holder.imageView.setImageBitmap(bitmap)

        //holder.imageView.setOnClickListener{}
    }


    override fun getItemCount(): Int {
        return images.size
    }

class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val imageView = view.findViewById(R.id.imageView) as ImageView

}


}