package com.example.photoapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.example.photoapp.R
import io.fotoapparat.Fotoapparat
import kotlinx.android.synthetic.main.fragment_photo.*


class PhotoFragment : Fragment(){

    private var listener: OnFragmentInteractionListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_photo, container, false)

        val btn1 = fragView.findViewById(R.id.btn1) as Button

        btn1.setOnClickListener{
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val fragment = CameraFragment.newInstance()
            transaction.replace(R.id.page_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
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
        fun newInstance() = PhotoFragment()

    }
}
