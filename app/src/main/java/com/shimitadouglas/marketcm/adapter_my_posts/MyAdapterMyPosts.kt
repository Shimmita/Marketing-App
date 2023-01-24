package com.shimitadouglas.marketcm.adapter_my_posts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.shimitadouglas.marketcm.modal_data_myposts.DataClassMyPosts
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts.Companion.CollectionPost
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty

class MyAdapterMyPosts(
    var context: Context,
    var arrayList: ArrayList<DataClassMyPosts>,
    var section: String
) :
    RecyclerView.Adapter<MyAdapterMyPosts.MyViewHolder>() {
    //get the UID
    val uniqueUID = FirebaseAuth.getInstance().uid
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
            textViewMyProductID.text = "code: " + arrayList[position].productID
            textViewCategory.text = "type: " + arrayList[position].category
            textViewProductPhone.text = "phone: " + arrayList[position].phone
            textViewMyProductPlace.text = "place:" + arrayList[position].university
            textViewMyProductTitle.text = arrayList[position].title
            textViewMyProductDescription.text = "info: " + arrayList[position].description
            textViewMyProductDate.text = "date: " + arrayList[position].date
            //loading the images images using the glide library
            Glide.with(context).load(arrayList[position].imageOwner)
                .into(circleImageViewOwner)
            Glide.with(context).load(arrayList[position].imageProduct)
                .into(imageViewMyProduct)
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
                    //
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
                    holder.funUpdatePostOperations(timerControllerID, productImageUri)
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
                                Toasty.custom(
                                    context,
                                    "encountered an error while deleting",
                                    R.drawable.ic_warning,
                                    R.color.dim_foreground_material_light,
                                    Toasty.LENGTH_SHORT,
                                    true,
                                    true
                                ).show()
                            }
                        }
                }
            }
            //code ends
        }

        fun funUpdatePostOperations(timerControllerID: String?, productImageUri: String?) {
            //code begins
            //updating the image in the store will involve
            //1.update of the image in the storage with a new image
            //2.update of the image uri in the map of my personal repo
            //3.update of the image uri in the public repository

            //of the other items credentials will be of ease update of only the fstore map data both on
            //my personal repo and the public repo

            //
            var selected = ""
            //
            //alert the user and ask what update she wants to make on the product
            val arrayOfUpdateOptions = arrayOf(
                "update product image",
                "update product email",
                "update product phone",
                "update product place"
            )
            val alertUpdateDialog = MaterialAlertDialogBuilder(context)
            alertUpdateDialog.setTitle("Update Window")
            alertUpdateDialog.setSingleChoiceItems(arrayOfUpdateOptions, 3) { _, which ->
                selected = arrayOfUpdateOptions[which]
            }
            alertUpdateDialog.setPositiveButton("update") { dialog, _ ->
                if (selected.isEmpty()) {
                    Toast.makeText(context, "select an option", Toast.LENGTH_SHORT).show()
                } else if (selected.isNotEmpty()) {
                    //call function to perform the selected function
                    funPerformOperationSelected(selected, productImageUri, timerControllerID)
                    //
                }
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
        timerControllerID: String?
    ) {
        //code begins
        if (selected.contains("image", true)) {
            funUpdateImageProduct(productImageUri, timerControllerID)
        } else if (selected.contains("phone", true)) {
            funUpdatePhone(timerControllerID)
        } else if (selected.contains("email")) {
            funUpdateEmail(timerControllerID)
        } else if (selected.contains("place")) {
            funUpdatePlace(timerControllerID)
        }
        //code ends
    }

    private fun funUpdateImageProduct(productImageUri: String?, timerControllerID: String?) {
        //code begins
        //check if storage permissions are enabled using the dexter
        Dexter.withContext(context).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                //show modal for updating the image
                //start a new activity involving the change of the image product only
                //pass old image uri and the keyTimer to check on the
                val bundle=Bundle()
                bundle.putString("key_old_image",productImageUri)
                bundle.putString("key_timer_stamp",timerControllerID)
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
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
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

    private fun funUpdatePhone(timerControllerID: String?) {
        //code begins
        //updating the phone will involve ;
        //1.update of the phone data in the personal repo
        //2.update of the phone in public repo
        val keyPhone="phone"
        val storePersonalRepoPhoneUpdate=FirebaseFirestore.getInstance()
        
        //code ends
    }

    private fun funUpdateEmail(timerControllerID: String?) {
        //code begins

        //code ends
    }

    private fun funUpdatePlace(timerControllerID: String?) {
        //code begins

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
                    Toasty.custom(
                        context, "encountered an error while deleting", R.drawable.ic_warning,
                        R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
                    ).show()
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
                    Toasty.custom(
                        context, "deleted successfully", R.drawable.ic_nike_done,
                        R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
                    ).show()
                } else if (!it.isSuccessful) {
                    Toasty.custom(
                        context, "encountered an error while deleting", R.drawable.ic_warning,
                        R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
                    ).show()
                }
            }
        }
        //code ends
    }
}