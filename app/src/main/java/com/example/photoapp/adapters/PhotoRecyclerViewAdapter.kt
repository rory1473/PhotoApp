package com.example.photoapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.fragments.ImageFragment
import java.io.File


class PhotoRecyclerViewAdapter(private val c: Context, private val images: List<Photo>): RecyclerView.Adapter<PhotoRecyclerViewAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(c).inflate(R.layout.single_photo, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        

        val getImage = images[position].image+".jpg"
        val getID = images[position].id

        val path = File(Environment.getExternalStorageDirectory().toString()+"/images/", getImage)

        val bitmap = BitmapFactory.decodeFile(path.absolutePath)


        //val bitmapCompress = bitmap
        //val stream = ByteArrayOutputStream()
       // bitmapCompress.compress(Bitmap.CompressFormat.JPEG, 20, stream)
        //val imageStream = stream.toByteArray()
        //val image = BitmapFactory.decodeByteArray(imageStream, 0, imageStream.size)
        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200,200, false))
        Log.i("FILE", getImage)

        holder.imageView.setOnClickListener{

            val imageFragment = ImageFragment()
            val args = Bundle()
            args.putString("image", getImage)
            args.putInt("imageID", getID)
            imageFragment.arguments = args
            val transaction = (holder.itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.page_fragment, imageFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }






    override fun getItemCount(): Int {
        return images.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val imageView = view.findViewById(R.id.imageView) as ImageView

    }


}






