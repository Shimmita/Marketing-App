package com.shimitadouglas.marketcm.fragment_admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_reports.MyAdapterReports
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.CollectionCounterfeit
import com.shimitadouglas.marketcm.modal_data_reports.DataClassReport
import es.dmoral.toasty.Toasty
import kotlin.concurrent.thread

class ReportsAdmin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init view
        val view: View = inflater.inflate(R.layout.fragment_admin_reports, container, false)
        //init of the recyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.rvReports)
        //perform data fetch then add the results on to the recycler
        thread {
            funPerformDataFetchRV(recyclerView)
        }
        //return the view
        return view
        //
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funPerformDataFetchRV(recyclerView: RecyclerView) {
        //code begins
        val arrayListHoldReports = arrayListOf<DataClassReport>()
        val storeReports = FirebaseFirestore.getInstance()
        storeReports.collection(CollectionCounterfeit).get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result.documents) {
                    val classFilter: DataClassReport? = data.toObject(DataClassReport::class.java)
                    if (classFilter != null) {
                        arrayListHoldReports.add(classFilter)
                    }
                }

            } else if (!it.isSuccessful) {
                Toasty.custom(
                    requireActivity(),
                    "server not responding",
                    R.drawable.ic_warning,
                    R.color.androidx_core_secondary_text_default_material_light,
                    Toasty.LENGTH_SHORT,
                    true,
                    true
                ).show()

                return@addOnCompleteListener
            }

            if (arrayListHoldReports.isNotEmpty()) {
                val adapterReports = MyAdapterReports(arrayListHoldReports, requireActivity())
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = adapterReports
                    adapterReports.notifyDataSetChanged()
                }
            } else if (arrayListHoldReports.isEmpty()) {
                Toasty.custom(
                    requireActivity(),
                    "empty data returned!",
                    R.drawable.ic_warning,
                    R.color.androidx_core_secondary_text_default_material_light,
                    Toasty.LENGTH_SHORT,
                    true,
                    true
                ).show()
            }
        }
        //code ends
    }
}