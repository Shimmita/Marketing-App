package com.shimitadouglas.marketcm.fragmentProducts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_big_notifications.MyAdapterBigNotification
import com.shimitadouglas.marketcm.adapter_enquiries_notification.MyAdapterEnquiriesNotification
import com.shimitadouglas.marketcm.adapter_normal_notification.MyAdapterNormalNotification
import com.shimitadouglas.marketcm.fragment_admin.MessageAdmin.Companion.BigPicture
import com.shimitadouglas.marketcm.fragment_admin.MessageAdmin.Companion.BigText
import com.shimitadouglas.marketcm.fragment_admin.MessageAdmin.Companion.Normal
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassBigNotifications
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassEnquiryNotifications
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
        //
        val storeEnquiries = FirebaseFirestore.getInstance()
        if (uniqueUID != null) {
            storeEnquiries.collection(uniqueUID).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    //creating an arraylist of enquiry class
                    val arraylistEnquiries = arrayListOf<DataClassEnquiryNotifications>()
                    arraylistEnquiries.clear()
                    //
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

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadNotificationOnNormRv() {
        //code begins
        //creating arrayList for for normal notifications
        val arraylistNormalNotification = arrayListOf<DataClassNormalNotification>()
        arraylistNormalNotification.clear()
        //
        val storeNormal =
            FirebaseFirestore.getInstance().collection(Normal).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    //normal notifications present posted by admin
                    for (data in it.documents) {
                        val classNormal: DataClassNormalNotification? =
                            data.toObject(DataClassNormalNotification::class.java)
                        if (classNormal != null) {
                            arraylistNormalNotification.add(classNormal)
                        }
                    }
                    //applying data on the rv Normal
                    recyclerViewNorm.apply {
                        val adapterNormal = MyAdapterNormalNotification(arraylistNormalNotification)
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = adapterNormal
                        adapterNormal.notifyDataSetChanged()
                    }
                    //

                }
            }
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadNotificationsOnBigRv() {
        //code begins
        //creating an arrayList  to hold big notifications
        var arrayListBigNotification = arrayListOf<DataClassBigNotifications>()
        arrayListBigNotification.clear()
        val storeBigPicture = FirebaseFirestore.getInstance().collection(BigPicture)
        val storeBigText = FirebaseFirestore.getInstance()

        //load big text if it contains some data in it
        storeBigText.collection(BigText).get().addOnSuccessListener {
            if (!it.isEmpty) {
                //there is big text notification sent by an admin
                for (data in it.documents) {
                    val classBigText: DataClassBigNotifications? =
                        data.toObject(DataClassBigNotifications::class.java)
                    if (classBigText != null) {
                        arrayListBigNotification.add(classBigText)
                    }
                }
                //perform rv operations
                val adapterBig=MyAdapterBigNotification(arrayListBigNotification,requireActivity())
                recyclerViewBig.apply {
                    layoutManager=LinearLayoutManager(requireActivity())
                    adapter=adapterBig
                    adapterBig.notifyDataSetChanged()
                }
                //

            }
        }
        //

        //load bigPic also if it contains some data in it
        storeBigPicture.get().addOnSuccessListener {
            if (!it.isEmpty)
            {
                //big picture contains data
                for (data in it.documents)
                {
                    val classBigPic: DataClassBigNotifications? =data.toObject(DataClassBigNotifications::class.java)
                    if (classBigPic != null) {
                        arrayListBigNotification.add(classBigPic)
                    }
                }
                //perform rv operations
                val adapterBig=MyAdapterBigNotification(arrayListBigNotification,requireActivity())
                recyclerViewBig.apply {
                    layoutManager=LinearLayoutManager(requireActivity())
                    adapter=adapterBig
                    adapterBig.notifyDataSetChanged()
                }
                //
                //
            }
        }
        //

        //code ends

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