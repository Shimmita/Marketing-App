package com.shimitadouglas.marketcm.adapter_products_posted

import android.annotation.SuppressLint
import android.content.Context
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
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_data_posts.DataClassProductsData
import de.hdodenhof.circleimageview.CircleImageView

class MyAdapterProducts(var products: ArrayList<DataClassProductsData>, var context: Context) :
    RecyclerView.Adapter<MyAdapterProducts.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //create a view that will be returned
        val viewMyViewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_layout_home_products, parent, false)
        //\
        return MyViewHolder(viewMyViewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //
        holder.apply {
            textViewTitleProduct.text = products[position].keyTitleProduct
            textViewOwner.text = "Owner: " + products[position].keyProductOwner
            textViewDescription.text = "Info:  " + products[position].keyDescription
            textViewCategoryType.text = "Type:  " + products[position].keyCategory
            textViewProductID.text = "Code:  " + products[position].keyProductID
            textViewVicinity.text = "Place:  " + products[position].keyVicinity
            textViewDate.text = "Date:   " + products[position].keyDate

            //using the glide library to set the images
            Glide.with(context).load(products[position].keyImageProduct).into(imageViewProduct)
            Glide.with(context).load(products[position].keyCircleImageOwner)
                .into(circleImageViewOwner)
            //

            //setting onclick on the btn enquire
            buttonEnquire.setOnClickListener {

                //animate the card
                cardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_avg))
                //
                Handler(Looper.myLooper()!!).postDelayed({
                    //call function to perform enquiries
                    holder.funEnquiriesOperations()
                    //
                }, 1300)
                //
            }

            //setting onclick on the product image
            imageViewProduct.setOnClickListener {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.object_image_clicked_view, null, false)
                val imageObject: ImageView = view.findViewById(R.id.imageObjectClicked)
                Glide.with(context).load(products[position].keyImageProduct).into(imageObject)
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
                Glide.with(context).load(products[position].keyCircleImageOwner).into(imageObject)
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
        fun funEnquiriesOperations() {
            //code begins
            Toast.makeText(context, "carry out enquiry operations", Toast.LENGTH_SHORT).show()
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

}