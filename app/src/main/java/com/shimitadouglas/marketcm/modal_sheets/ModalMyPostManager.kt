package com.shimitadouglas.marketcm.modal_sheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_my_posts.MyAdapterMyPosts
import com.shimitadouglas.marketcm.modal_data_myposts.DataClassMyPosts
import es.dmoral.toasty.Toasty

class ModalMyPostManager(accessSection: String) : BottomSheetDialogFragment() {
    //store the access value type  passed to the class for defining the behaviours of the buttons delete and delete
    private val section = accessSection
    //

    companion object {
        private const val TAG = "ModalMyPostManager"
    }

    lateinit var recyclerView: RecyclerView
    lateinit var arrayList: ArrayList<DataClassMyPosts>

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        val view: View = inflater.inflate(R.layout.mypost_manager_layout_view, container, false)
        //
        recyclerView = view.findViewById(R.id.rvMyPostManager)
        //fun load myPost data from the cloud
        funLoadMyPostCloud()
        //
        //toast the section in context
        if (section.contains("update")) {
            Toasty.custom(
                requireActivity(), "keep your products updated", R.drawable.ic_update,
                R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
            ).show()
        } else if (section.contains("delete")) {
            Toasty.custom(
                requireActivity(), "remove unnecessary products", R.drawable.ic_delete,
                R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
            ).show()
        } else {
            Toasty.custom(
                requireActivity(), "my recent posts", R.drawable.ic_cart,
                R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
            ).show()
        }
        //
        return view
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadMyPostCloud() {
        //code begins
        //obtain the uniqueID
        val uniqueUID = FirebaseAuth.getInstance().uid
        //
        //the path to the my post is specific included document path(UID/combinationUIDTimer/data)
        val store = uniqueUID?.let {
            FirebaseFirestore.getInstance().collection(it).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    arrayList = arrayListOf()
                    //current user repository contains data
                    for (data in it.documents) {
                        val classFormatted: DataClassMyPosts? =
                            data.toObject(DataClassMyPosts::class.java)

                        if (classFormatted != null) {
                            arrayList.add(classFormatted)
                        }
                    }

                    //perform recyclerOperations
                    recyclerView.apply {

                        val adapterRecyclerMyPosts =
                            MyAdapterMyPosts(requireActivity(), arrayList, section)
                        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerView.adapter = adapterRecyclerMyPosts
                        adapterRecyclerMyPosts.notifyDataSetChanged()
                    }
                    //
                } else return@addOnSuccessListener

            }
        }


        //code ends
    }
}





