package com.shimitadouglas.marketcm.adapter_enquiries_notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ProductsHome
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassEnquiryNotifications
import es.dmoral.toasty.Toasty

class MyAdapterEnquiriesNotification(
    var context: Context, var arrayList: ArrayList<DataClassEnquiryNotifications>
) : RecyclerView.Adapter<MyAdapterEnquiriesNotification.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageViewProductEnquired: ImageView
        var textViewProductEnquiredTitle: TextView
        var textViewEnquirerName: TextView
        var textViewEnquirerDate: TextView
        var textViewEnquirerEmail: TextView
        var textViewEnquirerPlace: TextView
        var textViewEnquirerPhone: TextView
        var cardViewEnquiry: CardView
        var buttonDeleteNotification: AppCompatButton

        init {
            imageViewProductEnquired = itemView.findViewById(R.id.imageProductEnquired)
            textViewEnquirerName = itemView.findViewById(R.id.enquirerName)
            textViewEnquirerDate = itemView.findViewById(R.id.dateEnquired)
            textViewEnquirerPlace = itemView.findViewById(R.id.placeEnquirer)
            textViewEnquirerEmail = itemView.findViewById(R.id.emailEnquirer)
            textViewProductEnquiredTitle = itemView.findViewById(R.id.enquiredProductName)
            textViewEnquirerPhone = itemView.findViewById(R.id.phoneEnquirer)
            buttonDeleteNotification = itemView.findViewById(R.id.btnDeleteEnquiry)
            cardViewEnquiry = itemView.findViewById(R.id.cardEnquiries)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //code begins
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.enquiries_notification_view, parent, false)
        return MyViewHolder(view)
        //code ends
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //check if there is a null data in the arrayList Of DataClass enquiries
        //if so continue to the next item leaving behind the null ones
        val enquiryObject = arrayList[position]
        if (enquiryObject.enquirerName == null || enquiryObject.enquirerEmail == null ||
            enquiryObject.enquirerPhone == null || enquiryObject.enquiredDate == null
        ) {
            holder.apply {
                //disable the visibility of the card since it's irrelevant to display it when it is null content
                cardViewEnquiry.visibility = View.GONE
            }
        } else {
            holder.apply {
                //visible the card since it contains contents that are not null
                cardViewEnquiry.visibility = View.VISIBLE


                //begin assigning the data to the views accordingly
                textViewProductEnquiredTitle.text = arrayList[position].productName
                textViewEnquirerDate.text = "Date:   " + arrayList[position].enquiredDate
                textViewEnquirerPhone.text = "email:   " + arrayList[position].enquirerEmail
                textViewEnquirerEmail.text = "phone:   " + arrayList[position].enquirerPhone
                textViewEnquirerPlace.text = "place:  " + arrayList[position].enquirerPlace
                textViewEnquirerName.text = "enquirer: " + arrayList[position].enquirerName

                //using the glide library to load the image onto the imageview
                Glide.with(context).load(arrayList[position].imageProduct)
                    .into(imageViewProductEnquired)
                //
                //setting listener on to the button for deleting the enquiry
                buttonDeleteNotification.setOnClickListener {
                    //call fun delete from the store
                    buttonDeleteNotification.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_in_left
                        )
                    )

                    //delay for 2sec and warn user  that if he continues the operation is irreversible
                    buttonDeleteNotification.postDelayed({
                        val sweetAlertDialogWarning =
                            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialogWarning.titleText = "Delete"
                        sweetAlertDialogWarning.contentText =
                            "delete the notification?"
                        sweetAlertDialogWarning.setCancelable(false)
                        sweetAlertDialogWarning.cancelText = "no"
                        sweetAlertDialogWarning.setConfirmClickListener {
                            //call the delete function here since user accepted the risk
                            val uniqueUIDUser = FirebaseAuth.getInstance().uid
                            val timerDocumentPath = arrayList[position].uniqueTimer

                            funDeleteEnquiry(uniqueUIDUser, timerDocumentPath)
                            //dismiss the dialog
                            it.dismiss()

                        }
                        sweetAlertDialogWarning.setCancelClickListener {
                            //dismiss the dialog
                            it.dismiss()

                        }
                        sweetAlertDialogWarning.create()
                        sweetAlertDialogWarning.show()

                    }, 1000)
                }
            }
        }

    }

    private fun funDeleteEnquiry(uniqueUIDUser: String?, timerDocumentPath: String?) {
        //code begins
        val store = FirebaseFirestore.getInstance()
        if (uniqueUIDUser != null) {
            if (timerDocumentPath != null) {
                store.collection(uniqueUIDUser).document(timerDocumentPath).delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //toast successfully
                            Toasty.success(context, "deleted successfully", Toasty.LENGTH_SHORT)
                                .show()
                            //migrate to Products main home for the changes to take place on the badge counter
                            //since fun to facilitate this action is in the Products home thus will be synced
                            context.startActivity(
                                Intent(context, ProductsHome::class.java)
                            )
                            //
                        } else if (!it.isSuccessful) {
                            //toast to the user failure
                            Toasty.custom(
                                context,
                                "delete failed!",
                                R.drawable.ic_warning,
                                R.color.background_floating_material_dark,
                                Toasty.LENGTH_LONG,
                                true,
                                true
                            ).show()
                            //
                        }
                    }
            }
        }
        //code ends
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }
}