package com.shimitadouglas.marketcm.adapter_products_posted

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.sharedPreferenceName
import com.shimitadouglas.marketcm.modal_data_posts.DataClassProductsData
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import java.util.*

class MyAdapterProducts(var products: ArrayList<DataClassProductsData>, var context: Context) :
    RecyclerView.Adapter<MyAdapterProducts.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //create a view that will be returned
        val viewMyViewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_layout_home_products, parent, false)
        //
        return MyViewHolder(viewMyViewHolder)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //
        holder.apply {
            textViewTitleProduct.text = products[position].title
            textViewOwner.text = "Owner: " + products[position].Owner
            textViewDescription.text = "Info:  " + products[position].description
            textViewCategoryType.text = "Type:  " + products[position].category
            textViewProductID.text = "Code:  " + products[position].productID
            textViewVicinity.text = "Place:  " + products[position].university
            textViewDate.text = "Date:   " + products[position].date
            buttonEnquire.text = "Enquire @KES " + products[position].price

            //using the glide library to set the images
            Glide.with(context).load(products[position].imageProduct).into(imageViewProduct)
            Glide.with(context).load(products[position].imageOwner)
                .into(circleImageViewOwner)
            //

            //setting onclick on the btn enquire
            buttonEnquire.setOnClickListener {


                //animate the card
                cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_avg))
                //
                Handler(Looper.myLooper()!!).postDelayed({
                    //creating the date instance from the Calendar
                    val timeUsingCalendar = Calendar.getInstance().time
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                    val formattedTime = dateFormat.format(timeUsingCalendar)
                    //
                    //obtain owner uniqueUID
                    val uniqueIDOwnerProduct = products[position].userID
                    //
                    //obtaining enquirer name from the sharedPreference
                    val sharedPreferences =
                        context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
                    val fName = sharedPreferences.getString("firstname", "")
                    val lName = sharedPreferences.getString("lastname", "")
                    val uni = sharedPreferences.getString("university", "")
                    val email = sharedPreferences.getString("email", "")
                    val phone = sharedPreferences.getString("phone", "")
                    //
                    //call function to perform enquiries
                    val titleEnquiredProduct = textViewTitleProduct.text.toString()
                    val imageEnquiredProduct = products[position].imageProduct
                    val enquirerName = "$fName $lName"
                    val placeEnquirer = uni
                    val emailEnquirer = email
                    val phoneEnquirer = phone
                    holder.funEnquiriesOperations(
                        titleEnquiredProduct,
                        imageEnquiredProduct,
                        enquirerName,
                        placeEnquirer,
                        emailEnquirer,
                        phoneEnquirer,
                        formattedTime,
                        uniqueIDOwnerProduct
                    )
                    //
                }, 1200)

            }

            //setting onclick on the product image
            imageViewProduct.setOnClickListener {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.object_image_clicked_view, null, false)
                val imageObject: ImageView = view.findViewById(R.id.imageObjectClicked)
                Glide.with(context).load(products[position].imageProduct).into(imageObject)
                val alert = MaterialAlertDialogBuilder(context)
                alert.setView(view)
                alert.create()
                alert.show()
            }
            //

            //setting onclick on the owner image
            circleImageViewOwner.setOnClickListener {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.object_image_clicked_view, null, false)
                val imageObject: ImageView = view.findViewById(R.id.imageObjectClicked)
                Glide.with(context).load(products[position].imageOwner).into(imageObject)
                val alert = MaterialAlertDialogBuilder(context)
                alert.setView(view)
                alert.create()
                alert.show()
            }
            //

        }
        //
    }


    override fun getItemCount(): Int {
        //return the size of the array (Products)
        return products.size
        //
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun funEnquiriesOperations(
            titleEnquiredProduct: String,
            imageEnquiredProduct: String?,
            enquirerName: String,
            placeEnquirer: String?,
            emailEnquirer: String?,
            phoneEnquirer: String?,
            formattedTime: String,
            uniqueIDOwnerProduct: String?
        ) {
            //code begins
            //alert the user of enquiry notification
            val alertEnquiry = MaterialAlertDialogBuilder(context)
            alertEnquiry.setTitle("Enquiry Notification")
            alertEnquiry.setIcon(R.drawable.ic_info)
            alertEnquiry.background =
                context.resources.getDrawable(R.drawable.material_seven, context.theme)
            alertEnquiry.setMessage(
                "enquiry notification will be sent to:\n\n(${textViewOwner.text})\n\n" +
                        "That you are interested to bargain or purchase:\n\n(${textViewTitleProduct.text})"
            )
            alertEnquiry.setCancelable(false)
            alertEnquiry.setPositiveButton("Accept") { dialog, _ ->
                //call fun to send enquiries to the cloud
                funSendEnquiriesCloud(
                    titleEnquiredProduct,
                    imageEnquiredProduct,
                    enquirerName,
                    placeEnquirer,
                    emailEnquirer,
                    phoneEnquirer,
                    formattedTime,
                    uniqueIDOwnerProduct
                )
                //

                //dismiss the dialog to avoid RT Exception
                dialog.dismiss()
                //
            }
            alertEnquiry.setNegativeButton("Decline") { dialog, _ ->

                //dismiss the dg
                dialog.dismiss()
                //
            }
            alertEnquiry.create().show()
            //code ends
        }


        //obtaining the views from data layout
        var imageViewProduct: ImageView
        var circleImageViewOwner: CircleImageView
        var textViewTitleProduct: TextView
        var textViewOwner: TextView
        var textViewDescription: TextView
        var textViewCategoryType: TextView
        var textViewProductID: TextView
        var textViewVicinity: TextView
        var textViewDate: TextView
        var buttonEnquire: Button
        var cardView: CardView

        //init of the views
        init {
            imageViewProduct = item.findViewById(R.id.imgProduct)
            circleImageViewOwner = item.findViewById(R.id.circleProductOwnerImage)
            textViewTitleProduct = item.findViewById(R.id.tvTitleProduct)
            textViewOwner = item.findViewById(R.id.tvOwnerProduct)
            textViewDescription = item.findViewById(R.id.tvDescriptionProduct)
            textViewCategoryType = item.findViewById(R.id.tvCategoryProduct)
            textViewProductID = item.findViewById(R.id.tvProductID)
            textViewVicinity = item.findViewById(R.id.tvVicinityProduct)
            textViewDate = item.findViewById(R.id.tvDate)
            buttonEnquire = item.findViewById(R.id.btnEnquire)
            cardView = item.findViewById(R.id.cardAdapterProducts)
        }

        //
    }

    private fun funSendEnquiriesCloud(
        titleEnquiredProduct: String,
        imageEnquiredProduct: String?,
        enquirerName: String,
        placeEnquirer: String?,
        emailEnquirer: String?,
        phoneEnquirer: String?,
        formattedTime: String,
        uniqueIDOwnerProduct: String?
    ) {

        //code begins
        //the path to the enquiries (UIDProductOwner/timeMillis)
        //store the time in millis to be able dto control the enquiry
        val timeInMillis = System.currentTimeMillis().toString()
        //creating the keys for the data to be stored in the map
        val keyTitleProduct = "productName"
        val keyImageProduct = "imageProduct"
        val keyEnquirer = "enquirerName"
        val keyEnquirerPlace = "enquirerPlace"
        val keyEnquirerEmail = "enquirerEmail"
        val keyEnquirerPhone = "enquirerPhone"
        val keyEnquiredDate = "enquiredDate"
        val keyTimeController="uniqueTimer"
        //creating a hashmap to store data key value pairs
        val hashMapEnquiries = hashMapOf(
            keyTitleProduct to titleEnquiredProduct,
            keyImageProduct to imageEnquiredProduct,
            keyEnquirer to enquirerName,
            keyEnquirerPlace to placeEnquirer,
            keyEnquirerEmail to emailEnquirer,
            keyEnquirerPhone to phoneEnquirer,
            keyEnquiredDate to formattedTime,
            keyTimeController to timeInMillis

        )
        //
        val storeCloudBackend = FirebaseFirestore.getInstance()
        if (uniqueIDOwnerProduct != null) {
            storeCloudBackend.collection(uniqueIDOwnerProduct).document(timeInMillis)
                .set(hashMapEnquiries).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //sent successfully
                        Toast.makeText(context, "enquiry sent successfully", Toast.LENGTH_LONG)
                            .show()
                        //
                    } else if (!it.isSuccessful) {
                        //failed to send enquiry to the backend cloud
                        Toast.makeText(
                            context,
                            "failed to send an enquiry connection issues!",
                            Toast.LENGTH_LONG
                        ).show()
                        //
                    }
                }
        }
        //code ends
    }

}