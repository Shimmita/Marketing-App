package com.shimitadouglas.marketcm.fragmentProducts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_enquiries_notification.MyAdapterEnquiriesNotification
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassEnquiryNotifications

class NotificationFragment : Fragment() {

    //init of the globals
    lateinit var viewNotification: View
    lateinit var recyclerViewEnquiries: RecyclerView
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
        //loads enquiry notifications on the rvEnquiries
        funLoadEnquiriesOnEnqRv()
        //
        return viewNotification
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadEnquiriesOnEnqRv() {
        //code begins
        //val uniqueUID
        val uniqueUID = FirebaseAuth.getInstance().uid
        val storeEnquiries = FirebaseFirestore.getInstance()
        if (uniqueUID != null) {
            storeEnquiries.collection(uniqueUID).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    //creating an arraylist of enquiry class
                    val arraylistEnquiries = arrayListOf<DataClassEnquiryNotifications>()
                    arraylistEnquiries.clear()
                    //data present(enquiries)
                    for (enquiry in it.documents) {
                        val enquiriesClass: DataClassEnquiryNotifications? =
                            enquiry.toObject(DataClassEnquiryNotifications::class.java)
                        if (enquiriesClass != null) {
                            arraylistEnquiries.add(enquiriesClass)
                        }
                    }

                    //adapter
                    val adapterEnquiries =
                        MyAdapterEnquiriesNotification(requireActivity(), arraylistEnquiries)
                    //setting adapter and layout on rvEnquiries
                    recyclerViewEnquiries.apply {
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = adapterEnquiries
                        adapterEnquiries.notifyDataSetChanged()
                    }
                    //
                } else if (it.isEmpty) {
                    //no data (enquiries)
                    return@addOnSuccessListener
                }
            }
        }
        //code ends
    }


    private fun funInitGlobals() {
        //code begins
        recyclerViewEnquiries = viewNotification.findViewById(R.id.rvEnquiriesNotification)
        //code ends
    }

    private fun updateTitle(title: String) {
        requireActivity().title = title

    }
}