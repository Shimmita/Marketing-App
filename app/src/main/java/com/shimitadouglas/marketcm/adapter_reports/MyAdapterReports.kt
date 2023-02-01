package com.shimitadouglas.marketcm.adapter_reports

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.controlPanel.Administration
import com.shimitadouglas.marketcm.modal_data_reports.DataClassReport

class MyAdapterReports(var arrayListReports: ArrayList<DataClassReport>, var context: Context) :
    RecyclerView.Adapter<MyAdapterReports.MyViewHolder>() {

    //defining key and data that will be used to migrate to fragment details in case of
    //button view is clicked
    private val stringDataMigration = "fragment_details"
    private val keyMigration = "migration"

    //
    //extra data for productController
    private val keyProductControllerID = "productControlID"
    private var stringProductController = ""

    //
    //extra data for suspect details
    private val keySuspectID = "suspectID"
    private var stringSuspectID = ""

    //
    //extra data for victim details
    private val keyVictimID = "victimID"
    private var stringVictimID = ""
    //

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewProductClaimID: TextView
        var textViewClaimDate: TextView
        var textViewProductCode: TextView
        var textViewProductControlID: TextView
        var textViewProductMessage: TextView
        var textViewProductSuspectID: TextView
        var textViewProductVictimID: TextView
        var appCompatButtonViewDetails: AppCompatButton

        init {
            textViewClaimDate = itemView.findViewById(R.id.tvClaimDate)
            textViewProductClaimID = itemView.findViewById(R.id.tvProductClaimID)
            textViewProductControlID = itemView.findViewById(R.id.tvProductControlID)
            textViewProductMessage = itemView.findViewById(R.id.tvProductMessage)
            textViewProductSuspectID = itemView.findViewById(R.id.tvProductSuspectID)
            textViewProductVictimID = itemView.findViewById(R.id.tvProductVictimID)
            textViewProductCode = itemView.findViewById(R.id.tvProductCode)
            appCompatButtonViewDetails = itemView.findViewById(R.id.btnViewDetails)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_reports_data_view, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            val productClaimDate = arrayListReports[position].claimDate
            val productCode = arrayListReports[position].productCode
            val productControlID = arrayListReports[position].productControlUID
            val productSuspectID = arrayListReports[position].productSuspectID
            val productVictimID = arrayListReports[position].productVictimID
            val productMessage = arrayListReports[position].productMessage
            val productClaimID = arrayListReports[position].ProductClaimID

            textViewClaimDate.text = "ClaimDate:\n$productClaimDate"
            textViewProductCode.text = "\nProductCode:\n$productCode"
            textViewProductClaimID.text = "\nProductClaimID:\n$productClaimID"
            textViewProductControlID.text = "\nProductControlID:\n$productControlID"
            textViewProductSuspectID.text = "\nProductSuspectID:\n$productSuspectID"
            textViewProductVictimID.text = "\nProductVictimID:\n$productVictimID"
            textViewProductMessage.text = "\nMessage:\n$productMessage"

            //setting listener to the button
            appCompatButtonViewDetails.setOnClickListener {
                //code begins
                val intentHomeParentAdmin = Intent(context, Administration::class.java)
                intentHomeParentAdmin.putExtra(keyMigration, stringDataMigration)
                intentHomeParentAdmin.putExtra(keyProductControllerID, productControlID)
                intentHomeParentAdmin.putExtra(keySuspectID, productSuspectID)
                intentHomeParentAdmin.putExtra(keyVictimID, productVictimID)
                context.startActivity(intentHomeParentAdmin)
                //code ends
            }
            //

        }
    }

    override fun getItemCount(): Int {
        return arrayListReports.size
    }

}