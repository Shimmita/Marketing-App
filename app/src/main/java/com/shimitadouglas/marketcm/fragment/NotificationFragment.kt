package com.shimitadouglas.marketcm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shimitadouglas.marketcm.R

class NotificationFragment : Fragment() {

    //init of the globals
    lateinit var viewNotification: View
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        //init of the view
        viewNotification = inflater.inflate(R.layout.notification_fragment, container, false)
        //update the tile of the
        val title = "Notification"
        updateTitle(title)
        //


        //
        return viewNotification
        //code ends
    }

    private fun updateTitle(title: String) {
        requireActivity().title = title
    }
}