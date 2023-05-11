package com.shimitadouglas.marketcm.adapter_all_users

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_data_profile.DataProfile
import de.hdodenhof.circleimageview.CircleImageView

class AdapterAllUsers(var context: Context, var arrayList: ArrayList<DataProfile>) :
    RecyclerView.Adapter<AdapterAllUsers.MyAdapter>() {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyAdapter, position: Int) {
        //code begins
        val user = arrayList[position]
        holder.apply {

            textViewName.text = "${user.FirstName} ${user.LastName}"
            textViewEmail.text = "email: " + user.Email
            textViewPhoneNumber.text = "email: " + user.PhoneNumber
            textViewCanPost.text = "post: " + user.canPost
            textViewRegistrationDate.text = "date: " + user.registrationDate
            textViewUniversity.text = "place: " + user.University
            //using the glide library to load the image onto the circle mageView
            Glide.with(context).load(user.ImagePath).into(circleImageView)
            //
        }
        //code ends
    }

    override fun getItemCount(): Int {
        //code begins
        return arrayList.size
        //code ends
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter {
        //code begins
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.data_layout_users, parent, false)
        return MyAdapter(view)
        //code ends
    }

    inner class MyAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleImageView: CircleImageView
        val textViewName: TextView
        val textViewEmail: TextView
        val textViewPhoneNumber: TextView
        val textViewUniversity: TextView
        val textViewRegistrationDate: TextView
        val textViewCanPost: TextView

        init {
            circleImageView = itemView.findViewById(R.id.circleImgUser)
            textViewName = itemView.findViewById(R.id.tvNameUser)
            textViewEmail = itemView.findViewById(R.id.tvEmailUser)
            textViewPhoneNumber = itemView.findViewById(R.id.tvPhoneNumberUser)
            textViewUniversity = itemView.findViewById(R.id.tvUniversityUser)
            textViewRegistrationDate = itemView.findViewById(R.id.tvUserRegistrationDate)
            textViewCanPost = itemView.findViewById(R.id.tvUserCanPost)
        }
    }
}