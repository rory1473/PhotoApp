package com.example.photoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoapp.R
import com.example.photoapp.adapters.PhotoRecyclerViewAdapter
import com.example.photoapp.datahandling.Photo
import com.example.photoapp.datahandling.PhotoDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.fragment_photo.*


class PhotoFragment : Fragment(){
    //declare class variables
    private val TAG = "PhotoFragment"
    private var listener: OnFragmentInteractionListener? = null
    private val progressBar: ProgressBar? = null
    private var progressStatus = 0
    private val handler = Handler()
    private var imageList = listOf<Photo>()
    private lateinit var db: PhotoDatabase
    lateinit var recyclerView: RecyclerView
    lateinit var title: TextView
    var allPhotos = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_photo, container, false)
        //set bottom navigation as visible
        val bottomNavigationView = activity!!.findViewById(R.id.nav_view) as BottomNavigationView
        bottomNavigationView.visibility = View.VISIBLE

        //set progress bar and circular progress bar
        val progressBar = fragView.findViewById(R.id.progressBar) as ProgressBar
        progressBar.visibility = View.INVISIBLE
        //set progress bar to run while loading images
        Thread(Runnable{
            run{
                while (progressStatus < 50) {
                    progressStatus += 1

                    handler.post{
                        run{
                            progressBar.progress = progressStatus
                        }
                    }
                    try {
                        Thread.sleep(50)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    //set circle progress bar as invisible when complete
                    if(progressStatus == 49){
                        if(progressBarCircle != null) {
                        progressBarCircle.visibility = View.INVISIBLE
                    }}

                }
        }}).start()

        //back button to return to last fragment on back stack
        val backButton = fragView.findViewById(R.id.back_btn) as FloatingActionButton
        backButton.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
            backButton.isEnabled = false
        }
        //get boolean value out of bundle set whether to show all photos or an album
        val arg = arguments
        allPhotos = arg!!.getBoolean("allPhotos")

        recyclerView =  fragView.findViewById(R.id.recyclerView) as RecyclerView
        title =  fragView.findViewById(R.id.title) as TextView

        //button to go to CameraFragment
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

        if (allPhotos) {
            title.text = getString(R.string.all_photos)
            //coroutine reads all images in database and sends list to recycler view
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    imageList = db.photoDAO().getAllImages()
                }
                Log.i(TAG, imageList.toString())
                val layoutManager = GridLayoutManager(context!!, 3)
                recyclerView.layoutManager = layoutManager     //LinearLayoutManager(activity1)
                val recyclerViewAdapter = PhotoRecyclerViewAdapter(context!!, imageList)
                recyclerView.adapter = recyclerViewAdapter
            }

            //val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)
            //viewModel.getAllImagesLive().observe(this, Observer<List<Photo>> {
            //    imageList = it


       } else{
            //read album data from bundle
            val arg = arguments
            val albumID = arg!!.getInt("ID")
            val albumName = arg.getString("name")
            title.text = albumName
            //coroutine finds all images from selected album and sends list in recycler view
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    imageList = db.photoDAO().getImageByAlbum(albumID)
                }
                Log.i(TAG, imageList.toString())
                val layoutManager = GridLayoutManager(context!!, 3)
                recyclerView.layoutManager = layoutManager
                val recyclerViewAdapter = PhotoRecyclerViewAdapter(context!!, imageList)
                recyclerView.adapter = recyclerViewAdapter
            }
        }
        //    val viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)
                //viewModel.getImageByAlbum(albumID).observe(this, Observer<List<Photo>> {
            //  imageList = it
    }


    override fun onResume() {
        super.onResume()
        //timer to remove progress bar on resume to fix displaying continuously after back navigation
        Handler().postDelayed({
            if(progressBarCircle != null) {
                progressBarCircle.visibility = View.INVISIBLE
            }
        }, 3000)

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
        fun newInstance() = PhotoFragment()

    }
}
