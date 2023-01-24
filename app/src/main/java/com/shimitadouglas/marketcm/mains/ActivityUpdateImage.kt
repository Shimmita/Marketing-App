package com.shimitadouglas.marketcm.mains

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts.Companion.CollectionPost
import com.shimitadouglas.marketcm.utilities.FileSizeDeterminant
import es.dmoral.toasty.Toasty

class ActivityUpdateImage : AppCompatActivity() {
    lateinit var timerStampControllerValue: String
    lateinit var oldImageFetched: String
    lateinit var btnUpdateImage: AppCompatButton
    lateinit var imageView: ImageView
    lateinit var textViewDescription: TextView
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_update_product_image)
        //function check data
        funFetchIntentData()
        //fun init Globals
        funInitGlobals()
        //
        btnUpdateImage.setOnClickListener {
            //fun update operations
            funUpdateOperations()
            //
        }
        //

    }

    private fun funUpdateOperations() {

        //code begins
        val intentPickImage = Intent()
        intentPickImage.apply {
            action = Intent.ACTION_PICK
            type = "image/*"
        }
        //launch the intent
        galleryOperations.launch(intentPickImage)
        //
        //code ends
    }


    private fun funInitGlobals() {
        //code begins
        btnUpdateImage = findViewById(R.id.btnUpdateImage)
        imageView = findViewById(R.id.imgUpdateProduct)
        textViewDescription = findViewById(R.id.tvFileDescription)

        //init of the view resource file
        //create a progressD
        val viewProgressD = LayoutInflater.from(this@ActivityUpdateImage)
            .inflate(R.layout.general_progress_dialog_view, null, false)
        //
        //
        //progress Dialog init
        progressDialog = ProgressDialog(this@ActivityUpdateImage)
        progressDialog.setIcon(R.drawable.ic_update_green)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Updating")
        progressDialog.setContentView(viewProgressD)
        //code ends
    }

    private fun funFetchIntentData() {
        //code begins
        //init a bundle so that can store data of bundles returned by the intent w/c we placed during the intent launch
        //from the adapter
        val data = intent.extras
        val oldImageData = data?.getString("key_old_image")
        val timerData = data?.getString("key_timer_stamp")
        if (oldImageData != null) {
            if (oldImageData.isNotEmpty()) {
                if (timerData != null) {
                    if (timerData.isNotEmpty()) {
                        timerStampControllerValue = timerData
                        oldImageFetched = oldImageData
                    }

                }
            }
            //code ends

        }
    }

    @SuppressLint("SetTextI18n")
    private val galleryOperations =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.data
                if (data != null) {
                    val fileSize = FileSizeDeterminant(this@ActivityUpdateImage)
                    val returnedSize = fileSize.funGetSize(data)
                    val converter = 1024f
                    val maxSize = 2500
                    val size = returnedSize / converter
                    //limit the size of the image to be posted as always be 2.5MB or Below
                    if (size > 2500) {
                        textViewDescription.text =
                            "size of the image of your product is larger than the recommended !" +
                                    "\n\nImage size of your product:" +
                                    "\n\n${size}KB  -> ${size / converter}MB" +
                                    "\n\nRecommended size of image:\n\n2500KB -> ${maxSize / 1000f}MB" +
                                    "\n\nImage exceeded limit by:" +
                                    "\n\n${(size - maxSize) / converter}MB" +
                                    "\n\nConclusion:\n\nprovide an image less than ${maxSize / 1000f}MB"
                    } else {
                        //disable the button so that the user cannot interact with it
                        btnUpdateImage.isEnabled = false
                        //

                        //use the Glide Library to load the image onto the imageview
                        Glide.with(this@ActivityUpdateImage).load(data).into(imageView)
                        //anim image
                        imageView.startAnimation(
                            AnimationUtils.loadAnimation(
                                this@ActivityUpdateImage,
                                R.anim.slide_in_left
                            )
                        )

                        //
                        imageView.postDelayed({
                            val alertUserAcceptImage =
                                MaterialAlertDialogBuilder(this@ActivityUpdateImage)
                            alertUserAcceptImage.setIcon(R.drawable.ic_question)
                            alertUserAcceptImage.setTitle("Image Update")
                            alertUserAcceptImage.setMessage("update the image ?")
                            alertUserAcceptImage.setPositiveButton("yes") { dialog, _ ->
                                funBeginUpdateImage(data)
                                //
                                dialog.dismiss()
                                //
                            }
                            alertUserAcceptImage.setNegativeButton("wait") { dialog, _ ->

                                //enable the button so that the usr can interact wit it again
                                btnUpdateImage.isEnabled = true
                                //
                                //dialog dismiss
                                dialog.dismiss()
                                //
                            }
                            alertUserAcceptImage.create().show()
                        }, 2000)
                    }
                }

            } else if (it.resultCode == RESULT_CANCELED) {
                Toast.makeText(this@ActivityUpdateImage, "cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun funBeginUpdateImage(data: Uri) {
        //show the progress dialog here
        progressDialog.create()
        progressDialog.show()
        //

        //code begins
        if (oldImageFetched.isEmpty() or (timerStampControllerValue.isEmpty())) {
            Toast.makeText(
                this@ActivityUpdateImage,
                "operation is not permitted",
                Toast.LENGTH_LONG
            ).show()

            //dismiss the progressD and return home
            progressDialog.dismiss()
            val intentHomeProducts = Intent(this@ActivityUpdateImage, ProductsHome::class.java)
            startActivity(intentHomeProducts)
            this@ActivityUpdateImage.finishAffinity()
            //
        } else if (oldImageFetched.isNotEmpty() or (timerStampControllerValue.isNotEmpty())) {
            //old image uri is present and too the timerStampKe
            //updateImageStorage->update-personal-repo-image-uri->update-public-repo-image-uri
            val storageUpdateImage =
                FirebaseStorage.getInstance().getReferenceFromUrl(oldImageFetched)

            storageUpdateImage.putFile(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    //obtain the result and get downloadUri of new image
                    it.result.storage.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {

                            //obtain a result in uri to string then proceed to update the private repo image value
                            val currentImagePath = it.result.toString()
                            funUpdatePrivateRepoImagePath(currentImagePath)
                            //

                        } else if (!it.isSuccessful) {
                            //toast the error and make the user returned home immediately
                            Toast.makeText(
                                this@ActivityUpdateImage,
                                "operation failed try again",
                                Toast.LENGTH_LONG
                            ).show()
                            //

                            //dismiss the progressD and return home
                            progressDialog.dismiss()
                            val intentHomeProducts =
                                Intent(this@ActivityUpdateImage, ProductsHome::class.java)
                            startActivity(intentHomeProducts)
                            this@ActivityUpdateImage.finishAffinity()
                            //
                        }

                    }
                    //
                } else if (!it.isSuccessful) {
                    //toast the error to the user and return
                    Toast.makeText(
                        this@ActivityUpdateImage,
                        "operation failed try again",
                        Toast.LENGTH_LONG
                    ).show()

                    //dismiss the progressD and return home
                    progressDialog.dismiss()
                    val intentHomeProducts =
                        Intent(this@ActivityUpdateImage, ProductsHome::class.java)
                    startActivity(intentHomeProducts)
                    this@ActivityUpdateImage.finishAffinity()
                    //

                    return@addOnCompleteListener
                    //


                }
            }
            //
        }
        //
    }

    private fun funUpdatePrivateRepoImagePath(currentImagePath: String) {
        //code begins
        val keyImage = "imageProduct"
        val mapData = hashMapOf(keyImage to currentImagePath)
        //
        val uniUID = FirebaseAuth.getInstance().uid
        //path to private-repo-image-product(UID/timerStampID)
        val storePrivate = FirebaseFirestore.getInstance()
        if (uniUID != null) {
            storePrivate.collection(uniUID).document(timerStampControllerValue)
                .update(mapData as Map<String, Any>)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //fun proceed update of the image in the public repo
                        funUpdatePublicRepoImagePath(currentImagePath)
                        //
                    } else if (!it.isSuccessful) {
                        //toast the error to the user
                        Toast.makeText(
                            this@ActivityUpdateImage,
                            "operation failed try again",
                            Toast.LENGTH_LONG
                        ).show()
                        //

                        //dismiss the progressD and return home
                        progressDialog.dismiss()
                        val intentHomeProducts =
                            Intent(this@ActivityUpdateImage, ProductsHome::class.java)
                        startActivity(intentHomeProducts)
                        this@ActivityUpdateImage.finishAffinity()
                        //

                    }
                }
        }

        //code ends
    }

    private fun funUpdatePublicRepoImagePath(currentImagePath: String) {
        //code begins
        //path to the public-repo-image(CollectionProduct/timerStampId)
        val imagePathPublicRepo = "imageProduct"
        val mapData = hashMapOf(imagePathPublicRepo to currentImagePath)
        val storePublicRepo = FirebaseFirestore.getInstance()
        storePublicRepo.collection(CollectionPost).document(timerStampControllerValue).update(
            mapData as Map<String, Any>
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //toast to the user a successful update process has been made
                    Toasty.custom(
                        this@ActivityUpdateImage, "congratulations image updated successfully",
                        R.drawable.ic_nike_done,
                        R.color.dim_foreground_material_light, Toasty.LENGTH_SHORT, true, true
                    ).show()
                    //dismiss the progressD
                    progressDialog.dismiss()
                    //

                    //call fun with intent migration to the home products page
                    funReturnHomeProducts()
                    //
                } else if (!it.isSuccessful) {
                    //toast the error to the user and return
                    Toast.makeText(
                        this@ActivityUpdateImage,
                        "operation failed try again",
                        Toast.LENGTH_LONG
                    ).show()
                    //
                    //dismiss the progressD and return home
                    progressDialog.dismiss()
                    val intentHomeProducts =
                        Intent(this@ActivityUpdateImage, ProductsHome::class.java)
                    startActivity(intentHomeProducts)
                    this@ActivityUpdateImage.finishAffinity()
                    //

                }
            }
        //code ends
    }

    private fun funReturnHomeProducts() {
        //code begins

        val intentToHomeProducts = Intent(this@ActivityUpdateImage, ProductsHome::class.java)
        this@ActivityUpdateImage.startActivity(intentToHomeProducts)
        this@ActivityUpdateImage.finishAffinity()
        //
        //code ends
    }
}