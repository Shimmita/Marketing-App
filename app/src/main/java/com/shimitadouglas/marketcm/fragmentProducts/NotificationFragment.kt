package com.shimitadouglas.marketcm.fragmentProducts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_normal_notification.MyAdapterNormalNotification
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassNormalNotification

class NotificationFragment : Fragment() {

    //init of the globals
    lateinit var viewNotification: View
    lateinit var recyclerViewNorm: RecyclerView
    lateinit var recyclerViewBig: RecyclerView
    lateinit var recyclerViewEnquiries: RecyclerView
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
        //init of the globals
        funInitGlobals()
        //
        //loads notification on big recycler view
        funLoadNotificationsOnBigRv()
        //
        //loads notification on norm recycler view
        funLoadNotificationOnNormRv()
        //
        //
        return viewNotification
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadNotificationOnNormRv() {
        //code begins
        val arraylistNormalNotification = arrayListOf<DataClassNormalNotification>()
        var max = 1
        while (max <= 100) {
            arraylistNormalNotification.add(
                DataClassNormalNotification(
                    "Account Closure",
                    "be notified to verify your account within two days." +
                            "accounts that have not undergone verification process through email verification are deemed to be ingenuity.",
                    "information message"
                )
            )
            max++
        }

        val adapterNorm = MyAdapterNormalNotification(arraylistNormalNotification)
        recyclerViewNorm.apply {
            adapter = adapterNorm
            layoutManager = LinearLayoutManager(requireActivity())
            adapterNorm.notifyDataSetChanged()
        }
        //code ends
    }

    private fun funLoadNotificationsOnBigRv() {
        //code begins
        //code begins

    }

    private fun funInitGlobals() {
        //code begins
        recyclerViewNorm = viewNotification.findViewById(R.id.rvNormalNotificationUserAccount)
        recyclerViewBig = viewNotification.findViewById(R.id.rvBigNotificationUserAccount)
        recyclerViewEnquiries = viewNotification.findViewById(R.id.rvEnquiriesNotification)
        //code ends
    }

    private fun updateTitle(title: String) {
        requireActivity().title = title

    }
}