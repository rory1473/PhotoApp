package com.example.photoapp.fragments

import android.content.Context
import org.osmdroid.config.Configuration
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoapp.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem


class MapFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    lateinit var items: ItemizedIconOverlay<OverlayItem>
    lateinit var markerGestureListener: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>
    lateinit var mv: MapView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_map, container, false)

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        val map = fragView.findViewById(R.id.map1) as MapView
        mv = map


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
        fun newInstance() =
            MapFragment().apply {

            }
    }
}
