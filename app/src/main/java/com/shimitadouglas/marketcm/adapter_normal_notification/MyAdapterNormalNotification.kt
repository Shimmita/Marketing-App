package com.shimitadouglas.marketcm.adapter_normal_notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassNormalNotification

class MyAdapterNormalNotification(var arrayList: ArrayList<DataClassNormalNotification>) :
    RecyclerView.Adapter<MyAdapterNormalNotification.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var message: TextView
        var type: TextView

        init {
            title = itemView.findViewById(R.id.titleNormalNotification)
            message = itemView.findViewById(R.id.messageNormalNotification)
            type = itemView.findViewById(R.id.typeNormalNotification)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //code begins
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.normal_notification_view, parent, false)

        //
        return MyViewHolder(view)
        //code ends
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            title.text = arrayList[position].keyTitle
            message.text = arrayList[position].keyMessage
            type.text = arrayList[position].keyType


        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}