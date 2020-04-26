package com.example.photoapp.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.datahandling.Album
import com.example.photoapp.datahandling.PhotoDatabase
import com.example.photoapp.fragments.PhotoFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.delete.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class AlbumRecyclerViewAdapter(private val c: Context, private val albums: List<Album>): CoroutineScope, RecyclerView.Adapter<AlbumRecyclerViewAdapter.MyViewHolder>(){
    private var album: Album? = null
    var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(c).inflate(
                R.layout.single_album,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val getAlbum = albums[position].name
        holder.textView.text = getAlbum
        Log.i("Album", getAlbum)

        holder.textView.setOnClickListener{
            val photos = false
            val photoFragment = PhotoFragment()
            val args = Bundle()
            args.putInt("ID", albums[position].id)
            args.putString("name", albums[position].name)
            args.putBoolean("allPhotos", photos)
            photoFragment.arguments = args
            val transaction = (holder.itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.page_fragment, photoFragment)
            transaction.addToBackStack(null)
            transaction.commit()}


        holder.deleteBtn.setOnClickListener{
                val dialogView = LayoutInflater.from(c).inflate(R.layout.delete, null)
                val builder = AlertDialog.Builder(c)
                    .setView(dialogView)
                    .setTitle("Delete Album?")

                val alert = builder.show()
                dialogView.yes_btn.setOnClickListener {
                    alert.dismiss()
                    val db = PhotoDatabase.getDatabase(holder.textView.context as FragmentActivity)

                    launch {

                        withContext(Dispatchers.IO) {
                            album = db.photoDAO().getAlbumByID(albums[position].id)
                        }
                        var photoID: Int? = null
                        withContext(Dispatchers.IO) {
                            photoID = db.photoDAO().deleteAlbum(album!!)

                        }
                        Toast.makeText(holder.textView.context as FragmentActivity, "Album Deleted", Toast.LENGTH_LONG).show()
                    }
                }
                dialogView.no_btn.setOnClickListener {
                    alert.dismiss()
                }
        }
    }


    override fun getItemCount(): Int {
        return albums.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textView = view.findViewById(R.id.textView) as TextView
        val deleteBtn = view.findViewById(R.id.delete_btn) as FloatingActionButton
    }


}
