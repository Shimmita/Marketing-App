package com.shimitadouglas.marketcm.adapter_big_notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassBigNotifications

class MyAdapterBigNotification(
    var arrayList: ArrayList<DataClassBigNotifications>,
    var context: Context
) :
    RecyclerView.Adapter<MyAdapterBigNotification.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var message: TextView
        var summary: TextView
        var image: ImageView
        var type: TextView

        init {

            title = itemView.findViewById(R.id.titleBigNotification)
            message = itemView.findViewById(R.id.messageBigTextNotification)
            summary = itemView.findViewById(R.id.summaryTextBigNotification)
            image = itemView.findViewById(R.id.imageBigNotification)
            type = itemView.findViewById(R.id.BigNotificationType)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.big_notification_view, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            title.text = arrayList[position].title
            message.text = arrayList[position].message
            summary.text = arrayList[position].summary
            type.text = arrayList[position].notType

            //loading the image using the glide library
            Glide.with(context).load(arrayList[position].image).into(image)
            //
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}