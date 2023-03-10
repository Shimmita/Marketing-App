package com.shimitadouglas.marketcm.adapter_my_posts

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ActivityUpdateImage
import com.shimitadouglas.marketcm.mains.ProductsHome
import com.shimitadouglas.marketcm.modal_data_myposts.DataClassMyPosts
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts.Companion.CollectionPost
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty

@Suppress("Deprecation")
class MyAdapterMyPosts(
    var context: Context, var arrayList: ArrayList<DataClassMyPosts>, var section: String
) : RecyclerView.Adapter<MyAdapterMyPosts.MyViewHolder>() {
    //get the UID
    val uniqueUID = FirebaseAuth.getInstance().uid
    //init general view containing ProgressD
    private val viewGeneralProgress: View =
        LayoutInflater.from(context).inflate(R.layout.general_progress_dialog_view, null, false)

    //progressDialog Object
    val progressDialog: ProgressDialog = ProgressDialog(context)

    init {
        progressDialog.setContentView(viewGeneralProgress)
        progressDialog.setTitle("Deleting")
        progressDialog.setIcon(R.drawable.ic_delete) //default icon that is changed as per the context
        progressDialog.setCancelable(false)
    }
    //

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //holder operations
        holder.apply {
            //obtain the returned timerStampController
            val timerControllerID = arrayList[position].timerControlID
            //
            val productImageUri = arrayList[position].imageProduct
            //
            //
            val productPrice = arrayList[position].price
            //
            textViewMyProductID.text = "code: " + arrayList[position].productID
            textViewCategory.text = "type: " + arrayList[position].category
            textViewProductPhone.text = "phone: " + arrayList[position].phone
            textViewMyProductPlace.text = "place:" + arrayList[position].university
            textViewMyProductTitle.text = arrayList[position].title
            textViewMyProductDescription.text = "info: " + arrayList[position].description
            textViewMyProductDate.text = "date: " + arrayList[position].date +" 12hrs"
            //loading the images images using the glide library
            Glide.with(context).load(arrayList[position].imageOwner).into(circleImageViewOwner)
            Glide.with(context).load(arrayList[position].imageProduct).into(imageViewMyProduct)
            //
            if (section.contains("delete")) {
                //make the button delete visible since section is delete
                //gone visibility is update button
                btnUpdateMyPost.visibility = View.GONE
                //
                //visible is btn delete
                btnDeleteMyPost.visibility = View.VISIBLE
                //
                //code begins listener on the delete
                btnDeleteMyPost.setOnClickListener {
                    //call fun delete operations
                    holder.funDeletePostOperations(timerControllerID, productImageUri)

                }
                //

            } else if (section.contains("update")) {
                //make button update visible since section is update
                //gone is visibility
                btnDeleteMyPost.visibility = View.GONE
                //
                //visible is button update
                btnUpdateMyPost.visibility = View.VISIBLE
                //

                //code begins listener on update btn
                btnUpdateMyPost.setOnClickListener {
                    //call fun update operations
                    holder.funUpdatePostOperations(timerControllerID, productImageUri, productPrice)
                    //
                }
                //
            }
        }
        //
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.my_post_data_view, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun funDeletePostOperations(timerControllerID: String?, productImageUri: String?) {
            //show the progress dialog hear
            progressDialog.create()
            progressDialog.show()
            //

            //two delete must happen
            //1.delete from my post collection(UID/timerStampID)
            //2.delete from public posts (collectionPost/uid)
            //3.delete image from the store to remove redundancy items

            //deleting from myPost repo
            val store = FirebaseFirestore.getInstance()

            if (uniqueUID != null) {
                if (timerControllerID != null) {
                    store.collection(uniqueUID).document(timerControllerID).delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                //call fun delete from the public repo
                                funDeleteMyPostFromPublicRepo(timerControllerID, productImageUri)
                                //
                            } else if (!it.isSuccessful) {
                                //show toast of failure
                                Toasty.custom(
                                    context,
                                    "encountered an error while deleting",
                                    R.drawable.ic_warning,
                                    R.color.dim_foreground_material_light,
                                    Toasty.LENGTH_SHORT,
                                    true,
                                    true
                                ).show()
                                //

                                //dismiss the progress Dialog and then return to the homeProducts due to the failure
                                //no more operations can be performed
                                progressDialog.apply {
                                    //
                                    dismiss()
                                    //
                                    val intentToHome = Intent(context, ProductsHome::class.java)
                                    intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intentToHome)


                                }
                                //

                            }
                        }
                }
            }
            //code ends
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun funUpdatePostOperations(
            timerControllerID: String?, productImageUri: String?, productPrice: String?
        ) {

            //code begins
            //updating the image in the store will involve
            //1.update of the image in the storage with a new image
            //2.update of the image uri in the map of my personal repo
            //3.update of the image uri in the public repository

            //of the other items credentials will be of ease update of only the store map data both on
            //my personal repo and the public repo

            //
            var selected = ""
            //
            //alert the user and ask what update she wants to make on the product
            val arrayOfUpdateOptions = arrayOf(
                "update product image",
                "update product phone",
                "update product place",
                "update product price"
            )
            val alertUpdateDialog = MaterialAlertDialogBuilder(context)
            alertUpdateDialog.setTitle("update window")
            alertUpdateDialog.background =
                context.resources.getDrawable(R.drawable.general_alert_dg, context.theme)
            alertUpdateDialog.setSingleChoiceItems(arrayOfUpdateOptions, 2) { _, which ->
                selected = arrayOfUpdateOptions[which]
            }
            alertUpdateDialog.setPositiveButton("update") { dialog, _ ->
                if (selected.isEmpty()) {
                    Toast.makeText(context, "select an option", Toast.LENGTH_SHORT).show()
                } else if (selected.isNotEmpty()) {
                    //call function to perform the selected function
                    funPerformOperationSelected(
                        selected, productImageUri, timerControllerID, productPrice
                    )
                    //
                }

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertUpdateDialog.setNegativeButton("dismiss") { dialog, _ ->

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            alertUpdateDialog.create().show()
            //
            //
            //code ends
        }

        var circleImageViewOwner: CircleImageView
        var imageViewMyProduct: ImageView
        var textViewMyProductTitle: TextView
        var textViewMyProductDate: TextView
        var textViewMyProductDescription: TextView
        var textViewMyProductPlace: TextView
        var textViewMyProductID: TextView
        var textViewCategory: TextView
        var textViewProductEmail: TextView
        var textViewProductPhone: TextView
        var btnUpdateMyPost: AppCompatButton
        var btnDeleteMyPost: AppCompatButton

        init {
            circleImageViewOwner = itemView.findViewById(R.id.myImage)
            imageViewMyProduct = itemView.findViewById(R.id.myProductImage)
            textViewMyProductDate = itemView.findViewById(R.id.myProductDatePost)
            textViewMyProductDescription = itemView.findViewById(R.id.myProductDescription)
            textViewMyProductTitle = itemView.findViewById(R.id.myProductTitle)
            textViewMyProductPlace = itemView.findViewById(R.id.myProductPlace)
            textViewMyProductID = itemView.findViewById(R.id.myProductUniqueID)
            textViewCategory = itemView.findViewById(R.id.myProductCategory)
            textViewProductEmail = itemView.findViewById(R.id.myProductEmail)
            textViewProductPhone = itemView.findViewById(R.id.myProductPhone)
            btnDeleteMyPost = itemView.findViewById(R.id.btnDeleteMyProduct)
            btnUpdateMyPost = itemView.findViewById(R.id.btnUpdateMyProduct)

            //getting the email of the current user
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            textViewProductEmail.text = "email: $currentUserEmail"
            //

        }
    }

    private fun funPerformOperationSelected(
        selected: String,
        productImageUri: String?,
        timerControllerID: String?,
        productPrice: String?
    ) {
        //code begins
        if (selected.contains("image", true)) {
            funUpdateImageProduct(productImageUri, timerControllerID)
        } else if (selected.contains("phone", true)) {
            funUpdatePhone(timerControllerID)
        } else if (selected.contains("place")) {
            funUpdatePlace(timerControllerID)
        } else if (selected.contains("price")) {
            funUpdatePrice(timerControllerID, productPrice)
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funUpdatePrice(timerControllerID: String?, productPrice: String?) {
        //code begins
        //1.update the price in the private-repo(UID/timerStampID/data)
        //2.update the price in the public repo(CollectionPost/timerStampID/data)
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_phone_place_price_update, null, false)
        val editTextPrice: EditText = view.findViewById(R.id.edtProductPriceUpdate)

        val editTextPlace: EditText = view.findViewById(R.id.edtProductPlaceUpdate)
        val editTextPhone: EditText = view.findViewById(R.id.edtProductPhoneUpdate)

        //invisible the phone number and the place edt since it's no required here
        editTextPhone.visibility = View.GONE
        editTextPlace.visibility = View.GONE
        //
        //visible the price edt
        editTextPrice.visibility = View.VISIBLE
        //

        //create an alert that will display the phone
        MaterialAlertDialogBuilder(context).setTitle("price entry\n(currently KES $productPrice)")
            .setCancelable(false)
            .setBackground(
                context.resources.getDrawable(
                    R.drawable.general_alert_dg,
                    context.theme
                )
            )
            .setIcon(R.drawable.ic_info).setView(view).setPositiveButton("update") { dialog, _ ->

                //extract the price entered
                val priceEntered = editTextPrice.text.toString()
                //
                //call function update price
                funBeginUpdatePrice(priceEntered, timerControllerID)
                //

                //dismiss the dialog
                dialog.dismiss()
                //

            }.setNegativeButton("wait") { dialog, _ ->
                //dismiss the dialog to avoid the RT errors
                dialog.dismiss()
                //
            }.create().show()
        //


        //
    }

    private fun funBeginUpdatePrice(priceEntered: String, timerControllerID: String?) {
        //code begins
        //check the legitimacy of the price entered
        if (priceEntered.isEmpty()) {
            Toast.makeText(context, "null values are not allowed", Toast.LENGTH_SHORT).show()
        } else if (priceEntered.isNotEmpty()) {
            if (priceEntered.length >= 4 && !priceEntered.contains(",")) {
                Toast.makeText(context, "separate the price with a comma (,)", Toast.LENGTH_LONG)
                    .show()
            } else {
                //everything ok lets post
                funContinuePriceUpdate(priceEntered, timerControllerID)
                //
            }
        }
        //
        //code ends
    }

    private fun funContinuePriceUpdate(priceEntered: String, timerControllerID: String?) {

        //display the progressD
        progressDialog.create()
        progressDialog.show()
        progressDialog.setIcon(R.drawable.ic_update_green)
        progressDialog.setTitle("Updating")
        //

        //code begins
        val uniqueUID = FirebaseAuth.getInstance().uid
        val keyPrice = "price"
        val mapData = hashMapOf(keyPrice to priceEntered)
        //path private-repo(UID/timerStampID)
        val storePrivateStorePrice = FirebaseFirestore.getInstance()
        if (timerControllerID != null) {
            if (uniqueUID != null) {
                storePrivateStorePrice.collection(uniqueUID).document(timerControllerID).update(
                    mapData as Map<String, Any>
                ).addOnCompleteListener {
                    if (it.isSuccessful) {

                        //begin update of the price for the public-repo
                        val storePublicPriceUpdate = FirebaseFirestore.getInstance()
                        storePrivateStorePrice.collection(CollectionPost)
                            .document(timerControllerID).update(
                                mapData as Map<String, Any>
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    //overall price update process was successful
                                    Toasty.custom(
                                        context,
                                        "congratulations price updated successfully",
                                        R.drawable.ic_nike_done,
                                        R.color.dim_foreground_material_light,
                                        Toasty.LENGTH_SHORT,
                                        true,
                                        true
                                    ).show()
                                    //
                                    //dismiss the progressD and return homeProducts
                                    progressDialog.apply {
                                        //
                                        dismiss()
                                        //
                                        //dismiss the progress and return to home
                                        val intentToHome = Intent(context, ProductsHome::class.java)
                                        intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        context.startActivity(intentToHome)
                                        //
                                    }
                                } else if (!it.isSuccessful) {
                                    //price update failed to update in the public-repo
                                    Toasty.custom(
                                        context,
                                        "price update failed!",
                                        R.drawable.ic_warning,
                                        R.color.dim_foreground_material_light,
                                        Toasty.LENGTH_SHORT,
                                        true,
                                        true
                                    ).show()
                                    //

                                    //dismiss the progressD and return homeProducts
                                    progressDialog.apply {
                                        //
                                        dismiss()
                                        //
                                        //dismiss the progress and return to home
                                        val intentToHome = Intent(context, ProductsHome::class.java)
                                        intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        context.startActivity(intentToHome)
                                        //
                                    }
                                }
                            }
                        //
                    } else if (!it.isSuccessful) {
                        //price update in the private-repo failed
                        Toasty.custom(
                            context,
                            "price update failed!",
                            R.drawable.ic_warning,
                            R.color.dim_foreground_material_light,
                            Toasty.LENGTH_SHORT,
                            true,
                            true
                        ).show()                        //
                        //dismiss the progressD and return homeProducts
                        progressDialog.apply {
                            //
                            dismiss()
                            //
                            //dismiss the progress and return to home
                            val intentToHome = Intent(context, ProductsHome::class.java)
                            intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intentToHome)
                            //
                        }
                    }
                }
            }
        }
        //code ends
    }

    private fun funUpdateImageProduct(productImageUri: String?, timerControllerID: String?) {
        //code begins
        //check if storage permissions are enabled using the dexter
        Dexter.withContext(context).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                //show modal for updating the image
                //start a new activity involving the change of the image product only
                //pass old image uri and the keyTimer to check on the
                val bundle = Bundle()
                bundle.putString("key_old_image", productImageUri)
                bundle.putString("key_timer_stamp", timerControllerID)
                val intentToClassUpdateImageProduct =
                    Intent(context, ActivityUpdateImage::class.java)
                //put the bundle in the intent
                intentToClassUpdateImageProduct.putExtras(bundle)
                //

                //start the intent migration to the second class
                context.startActivity(intentToClassUpdateImageProduct)
                //
                //
            }


            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?, p1: PermissionToken?
            ) {
                //
                Toasty.custom(
                    context,
                    "enable the required permissions to continue",
                    R.drawable.ic_info,
                    R.color.transparrent,
                    Toasty.LENGTH_LONG,
                    true,
                    true
                ).show()
                //
            }

        }).check()
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funUpdatePhone(timerControllerID: String?) {
        //code begins
        //updating the phone will involve ;
        //1.update of the phone data in the personal repo
        //2.update of the phone in public repo
        //load a view resource to display alert that shows input dialog for number
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_phone_place_price_update, null, false)
        val editTextNumber: EditText = view.findViewById(R.id.edtProductPhoneUpdate)

        val editTextPrice: EditText = view.findViewById(R.id.edtProductPriceUpdate)
        val editTextPlace: EditText = view.findViewById(R.id.edtProductPlaceUpdate)
        //invisible price and place edt are not required here
        editTextPlace.visibility = View.GONE
        editTextPrice.visibility = View.GONE
        //
        //make the number edt visible
        editTextNumber.visibility = View.VISIBLE
        //

        //create an alert that will display the phone
        MaterialAlertDialogBuilder(context).setTitle("phone number entry")
            .setBackground(
                context.resources.getDrawable(
                    R.drawable.general_alert_dg,
                    context.theme
                )
            )

            .setIcon(R.drawable.ic_info).setView(view).setPositiveButton("update") { dialog, _ ->

                //extract the number entered
                val numberEntered = editTextNumber.text.toString()
                //
                //call fun to update phoneNumber
                funBeginUpdatingPhone(numberEntered, timerControllerID)
                //
                //dismiss
                dialog.dismiss()
                //
            }.setNegativeButton("wait") { dialog, _ ->
                //dismiss the dialog to avoid the RT errors
                dialog.dismiss()
                //
            }.create().show()
        //

        //code ends
    }

    private fun funBeginUpdatingPhone(numberEntered: String, timerControllerID: String?) {
        //code begins
        //check the legitimacy of the number entered
        if (numberEntered.isEmpty()) {
            Toast.makeText(context, "null values not allowed", Toast.LENGTH_SHORT).show()
        } else if (numberEntered.isNotEmpty()) {
            if (numberEntered.length > 10) {
                //number too long
                Toast.makeText(context, "number too long", Toast.LENGTH_SHORT).show()
                //
            } else if (numberEntered.length < 10) {
                Toast.makeText(context, "number too short", Toast.LENGTH_SHORT).show()
            } else {

                //show the progress dialog here with update icon
                progressDialog.create()
                progressDialog.show()
                progressDialog.setTitle("Updating")
                progressDialog.setIcon(R.drawable.ic_update_green)
                //

                //number input is okay continue with update process
                //path to the private repo(UID/timerID/data)
                //path to the public repo(CollectionProduct/timerStampID/data)
                val uniqueUID = FirebaseAuth.getInstance().uid
                val keyPhone = "phone"
                val mapData = hashMapOf(keyPhone to numberEntered)
                val storePersonalRepoPhoneUpdate = FirebaseFirestore.getInstance()
                if (timerControllerID != null) {
                    if (uniqueUID != null) {
                        storePersonalRepoPhoneUpdate.collection(uniqueUID)
                            .document(timerControllerID).update(
                                mapData as Map<String, Any>
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    //number update private repo successful
                                    //begin update to the public repo (CollectionPost/timerStamp)
                                    val publicRepoNumberUpdate = FirebaseFirestore.getInstance()
                                    publicRepoNumberUpdate.collection(CollectionPost)
                                        .document(timerControllerID).update(
                                            mapData as Map<String, Any>
                                        ).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                //congrats process ended successfully
                                                Toasty.custom(
                                                    context,
                                                    "congratulations number updated successfully",
                                                    R.drawable.ic_nike_done,
                                                    R.color.dim_foreground_material_light,
                                                    Toasty.LENGTH_SHORT,
                                                    true,
                                                    true
                                                ).show()

                                                //dismiss the progressD and return homeProducts
                                                progressDialog.apply {
                                                    //
                                                    dismiss()
                                                    //
                                                    //dismiss the progress and return to home
                                                    val intentToHome =
                                                        Intent(context, ProductsHome::class.java)
                                                    intentToHome.flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    intentToHome.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                    context.startActivity(intentToHome)
                                                    //
                                                }

                                                //
                                            } else if (!it.isSuccessful) {
                                                //failed to update the number in the public repo
                                                Toasty.custom(
                                                    context,
                                                    "phone number update failed!",
                                                    R.drawable.ic_warning,
                                                    R.color.dim_foreground_material_light,
                                                    Toasty.LENGTH_SHORT,
                                                    true,
                                                    true
                                                ).show()
                                                //


                                                //dismiss the progressD and return homeProducts
                                                progressDialog.apply {
                                                    //
                                                    dismiss()
                                                    //
                                                    //dismiss the progress and return to home
                                                    val intentToHome =
                                                        Intent(context, ProductsHome::class.java)
                                                    intentToHome.flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    intentToHome.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                    context.startActivity(intentToHome)
                                                    //
                                                }
                                            }
                                        }
                                    //
                                } else if (!it.isSuccessful) {
                                    //update private repo is a failure
                                    Toasty.custom(
                                        context,
                                        "phone number update failed!",
                                        R.drawable.ic_warning,
                                        R.color.dim_foreground_material_light,
                                        Toasty.LENGTH_SHORT,
                                        true,
                                        true
                                    ).show()

                                    //dismiss the progressD and return homeProducts
                                    progressDialog.apply {
                                        //
                                        dismiss()
                                        //
                                        //dismiss the progress and return to home
                                        val intentToHome = Intent(context, ProductsHome::class.java)
                                        intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        context.startActivity(intentToHome)
                                        //
                                    }
                                    //
                                }
                            }
                    }
                }
                //
            }
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funUpdatePlace(timerControllerID: String?) {
        //code begins
        //place update involved:
        //1.update the number in the private-repo(UID/timerStampID/data)
        //2.update the number in the public-repo(CollectionPost/timerStampID/data)
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_phone_place_price_update, null, false)
        val editTextNumber: EditText = view.findViewById(R.id.edtProductPhoneUpdate)
        val editTextPlace: EditText = view.findViewById(R.id.edtProductPlaceUpdate)
        val editTextPrice: EditText = view.findViewById(R.id.edtProductPriceUpdate)

        //invisible the phone number and price since it's no required here
        editTextNumber.visibility = View.GONE
        editTextPrice.visibility = View.GONE
        //

        //visible the place edit since it's required here
        editTextPlace.visibility = View.VISIBLE
        //

        //create an alert that will display the phone
        MaterialAlertDialogBuilder(context).setTitle("place entry")
            .setBackground(
                context.resources.getDrawable(
                    R.drawable.general_alert_dg,
                    context.theme
                )
            )

            .setIcon(R.drawable.ic_info).setView(view).setPositiveButton("update") { dialog, _ ->

                //extract the place entered
                val placeEntered = editTextPlace.text.toString()
                //
                //call function to update the place
                funBeginUpdatingPlace(placeEntered, timerControllerID)
                //dismiss the dialog
                dialog.dismiss()
                //

            }.setNegativeButton("wait") { dialog, _ ->
                //dismiss the dialog to avoid the RT errors
                dialog.dismiss()
                //
            }.create().show()
        //

        //code ends
    }

    private fun funBeginUpdatingPlace(placeEntered: String, timerControllerID: String?) {

        //code begins
        //check legitimacy of the place entered
        if (placeEntered.isEmpty()) {
            Toast.makeText(context, "null values not allowed!", Toast.LENGTH_SHORT).show()
        } else if (placeEntered.isNotEmpty()) {

            //show the progressD with icon
            progressDialog.create()
            progressDialog.show()
            progressDialog.setTitle("Updating")
            progressDialog.setIcon(R.drawable.ic_update_green)
            //

            //continue
            val uniqueUID = FirebaseAuth.getInstance().uid
            val keyPlace = "university"
            val mapData = hashMapOf(keyPlace to placeEntered)
            //update private-repo
            val storePrivatePlaceUpdate = FirebaseFirestore.getInstance()
            if (timerControllerID != null) {
                if (uniqueUID != null) {
                    storePrivatePlaceUpdate.collection(uniqueUID).document(timerControllerID)
                        .update(
                            mapData as Map<String, Any>
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {

                                //begin updating the place in the public-repo(collection/timerStampID)
                                val storePublicRepoPlaceUpdate = FirebaseFirestore.getInstance()
                                storePublicRepoPlaceUpdate.collection(
                                    CollectionPost
                                ).document(timerControllerID).update(mapData as Map<String, Any>)
                                    .addOnCompleteListener {
                                        //
                                        if (it.isSuccessful) {
                                            //place update was successful
                                            Toasty.custom(
                                                context,
                                                "congratulations place updated successfully",
                                                R.drawable.ic_nike_done,
                                                R.color.dim_foreground_material_light,
                                                Toasty.LENGTH_SHORT,
                                                true,
                                                true
                                            ).show()
                                            //
                                            //dismiss the progressD and return homeProducts
                                            progressDialog.apply {
                                                //
                                                dismiss()
                                                //
                                                //dismiss the progress and return to home
                                                val intentToHome =
                                                    Intent(context, ProductsHome::class.java)
                                                intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startActivity(intentToHome)
                                                //
                                            }
                                        } else if (!it.isSuccessful) {
                                            //failed to update the place in the public repository
                                            Toasty.custom(
                                                context,
                                                "place update failed!",
                                                R.drawable.ic_warning,
                                                R.color.dim_foreground_material_light,
                                                Toasty.LENGTH_SHORT,
                                                true,
                                                true
                                            ).show()
                                            //
                                            //dismiss the progressD and return homeProducts
                                            progressDialog.apply {
                                                //
                                                dismiss()
                                                //
                                                //dismiss the progress and return to home
                                                val intentToHome =
                                                    Intent(context, ProductsHome::class.java)
                                                intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startActivity(intentToHome)
                                                //
                                            }
                                        }
                                        //
                                    }
                                //

                            } else if (!it.isSuccessful) {
                                //failed to update private repo-place
                                Toasty.custom(
                                    context,
                                    "place update failed!",
                                    R.drawable.ic_warning,
                                    R.color.dim_foreground_material_light,
                                    Toasty.LENGTH_SHORT,
                                    true,
                                    true
                                ).show()
                                //

                                //dismiss the progressD and return homeProducts
                                progressDialog.apply {
                                    //
                                    dismiss()
                                    //
                                    //dismiss the progress and return to home
                                    val intentToHome = Intent(context, ProductsHome::class.java)
                                    intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intentToHome)
                                    //
                                }
                            }
                        }
                }
            }
            //
        }
        //code ends
    }

    private fun funDeleteMyPostFromPublicRepo(
        timerControllerID: String,
        productImageUri: String?,
    ) {
        //code begins
        val storePublic = FirebaseFirestore.getInstance()
        storePublic.collection(CollectionPost).document(timerControllerID).delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //call fun to delete the image data from the storage using the
                    //file uri we got while posting the image thus no need for path using timeID
                    funDeleteProductImageFromStorage(productImageUri)
                    //

                } else {
                    //toast an error to the user
                    Toasty.custom(
                        context,
                        "encountered an error while deleting",
                        R.drawable.ic_warning,
                        R.color.dim_foreground_material_light,
                        Toasty.LENGTH_SHORT,
                        true,
                        true
                    ).show()
                    //
                    progressDialog.apply {
                        //
                        dismiss()
                        //
                        //dismiss the progress and return to home
                        val intentToHome = Intent(context, ProductsHome::class.java)
                        intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intentToHome)
                        //
                    }
                }
            }
        //
    }


    private fun funDeleteProductImageFromStorage(productImageUri: String?) {
        //code begins
        val storage = FirebaseStorage.getInstance()
        if (productImageUri != null) {
            storage.getReferenceFromUrl(productImageUri).delete().addOnCompleteListener {
                if (it.isSuccessful) {
                    //toast successfully deleted and the make the user return home
                    Toasty.custom(
                        context,
                        "deleted successfully",
                        R.drawable.ic_nike_done,
                        R.color.dim_foreground_material_light,
                        Toasty.LENGTH_SHORT,
                        true,
                        true
                    ).show()
                    //
                    progressDialog.apply {
                        //dismiss the progress dialog
                        dismiss()
                        //

                        //back home by the user
                        val intentToHome = Intent(context, ProductsHome::class.java)
                        intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intentToHome)

                        //
                    }

                } else if (!it.isSuccessful) {
                    //show the error to the user of fail to delete the file from the public repository
                    Toasty.custom(
                        context,
                        "encountered an error while deleting",
                        R.drawable.ic_warning,
                        R.color.dim_foreground_material_light,
                        Toasty.LENGTH_SHORT,
                        true,
                        true
                    ).show()
                    //

                    //migrate home products since the user cannot continue anywhere
                    val intentToHome = Intent(context, ProductsHome::class.java)
                    intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intentToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intentToHome)

                    //
                }
            }
        }
        //code ends
    }
}