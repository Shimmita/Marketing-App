package com.shimitadouglas.marketcm.fragment_admin

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.adapter_big_notifications.MyAdapterBigNotification
import com.shimitadouglas.marketcm.adapter_normal_notification.MyAdapterNormalNotification
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassBigNotifications
import com.shimitadouglas.marketcm.modal_data_notifications.DataClassNormalNotification

class MessageAdmin : Fragment(), AdapterView.OnItemSelectedListener {
    companion object {
        val BigText: String = "BigText"
        val BigPicture: String = "BigPicture"
        val Normal: String = "Normal"
    }

    var uriImage: Uri? = null
    private var imageView: ImageView? = null
    var spinnerMessageType: Spinner? = null
    var checkBox: CheckBox? = null
    var linearLayoutMessageConsole: LinearLayout? = null
    var editTextMessageTitle: EditText? = null
    var editTextSummaryText: EditText? = null
    var editTextMessage: EditText? = null
    var recyclerViewBigNotifications: RecyclerView? = null
    var recyclerViewNormalNotification: RecyclerView? = null

    private lateinit var messageTypeSpinner: String


    //val array of typeMessage
    private val arrayNotificationType = arrayOf(
        "BigPicture Notification",
        "BigText Notification",
        "Normal Notification"
    )
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        val viewMessages: View = inflater.inflate(R.layout.message_admin, container, false)
        //
        checkBox = viewMessages.findViewById(R.id.cbControl)
        linearLayoutMessageConsole = viewMessages.findViewById(R.id.linearController)
        editTextMessageTitle = viewMessages.findViewById(R.id.edtTitleMessage)
        editTextSummaryText = viewMessages.findViewById(R.id.edtSummaryText)
        recyclerViewBigNotifications =
            viewMessages.findViewById(R.id.rvNormalNotification) as RecyclerView
        recyclerViewNormalNotification =
            viewMessages.findViewById(R.id.rvBigNotifications) as RecyclerView
        val buttonSendNotification =
            viewMessages.findViewById(R.id.btnSendNotification) as AppCompatButton
        editTextMessage = viewMessages.findViewById(R.id.edtMessage) as EditText
        spinnerMessageType = viewMessages.findViewById(R.id.spinnerMessageType) as Spinner
        imageView = viewMessages.findViewById(R.id.imageViewNotification)
        //
        //setting onclick listener on the imageView
        imageView?.setOnClickListener {

            //code begins
            val intent = Intent()
            intent.type = "image/*"

            intent.action = Intent.ACTION_PICK
            galleryActivity.launch(intent)

            //code ends
        }
        //

        //init adapter for the spinner notification type
        var adapterNotification = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_list_item_1,
            arrayNotificationType
        )
        //
        //setting listener on the spinner
        spinnerMessageType!!.onItemSelectedListener = this@MessageAdmin
        spinnerMessageType?.adapter = adapterNotification
        //
        //checkBox listener
        checkBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                linearLayoutMessageConsole?.apply {
                    visibility = View.GONE
                }
            } else if (!isChecked) {
                linearLayoutMessageConsole?.apply {
                    visibility = View.VISIBLE
                }
            }
        }
        //

        //setting listener on the send btn
        buttonSendNotification.setOnClickListener {
            //begin sending the notification to the cloud
            funAlertAdminWhatNotification()
            //
        }

        //load data onto the rv normal
        funLoadNotificationNormal()
        //
        //load not onto the rv big
        funLoadNotificationBig()
        //
        return viewMessages
        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadNotificationBig() {
        //code begins
        //creating an arrayList  to hold big notifications
        var arrayListBigNotification = arrayListOf<DataClassBigNotifications>()
        arrayListBigNotification.clear()
        val storeBigPicture = FirebaseFirestore.getInstance().collection(BigPicture)
        val storeBigText = FirebaseFirestore.getInstance()

        //load big text if it contains some data in it
        storeBigText.collection(BigText).get().addOnSuccessListener {
            if (!it.isEmpty) {
                //there is big text notification sent by an admin
                for (data in it.documents) {
                    val classBigText: DataClassBigNotifications? =
                        data.toObject(DataClassBigNotifications::class.java)
                    if (classBigText != null) {
                        arrayListBigNotification.add(classBigText)
                    }
                }
                //perform rv operations
                val adapterBig =
                    MyAdapterBigNotification(arrayListBigNotification, requireActivity())
                recyclerViewBigNotifications?.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = adapterBig
                    adapterBig.notifyDataSetChanged()
                }
                //

            }
        }
        //

        //load bigPic also if it contains some data in it
        storeBigPicture.get().addOnSuccessListener {
            if (!it.isEmpty) {
                //big picture contains data
                for (data in it.documents) {
                    val classBigPic: DataClassBigNotifications? = data.toObject(
                        DataClassBigNotifications::class.java
                    )
                    if (classBigPic != null) {
                        arrayListBigNotification.add(classBigPic)
                    }
                }
                //perform rv operations
                val adapterBig =
                    MyAdapterBigNotification(arrayListBigNotification, requireActivity())
                recyclerViewBigNotifications?.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = adapterBig
                    adapterBig.notifyDataSetChanged()
                }
                //
                //
            }
        }
        //


        //code ends
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun funLoadNotificationNormal() {
        //code begins
        //code begins
        //creating arrayList for for normal notifications
        val arraylistNormalNotification = arrayListOf<DataClassNormalNotification>()
        arraylistNormalNotification.clear()
        //
        val storeNormal =
            FirebaseFirestore.getInstance().collection(Normal)

        storeNormal.get().addOnSuccessListener {
            if (!it.isEmpty) {
                //normal notifications present posted by admin
                for (data in it.documents) {
                    val classNormal: DataClassNormalNotification? =
                        data.toObject(DataClassNormalNotification::class.java)
                    if (classNormal != null) {
                        arraylistNormalNotification.add(classNormal)
                    }
                }
                //applying data on the rv Normal
                recyclerViewNormalNotification?.apply {
                    val adapterNormal = MyAdapterNormalNotification(arraylistNormalNotification)
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = adapterNormal
                    adapterNormal.notifyDataSetChanged()
                }
                //

            }
        }
        //code ends
    }

    private fun funAlertAdminWhatNotification() {
        //code begins
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Notification Type")
            .setMessage("what type of notification?")
            .setCancelable(false)
            .setPositiveButton("BPic") { dialog, _ ->
                //
                val notType = "information"
                funPostBigPictureNotification(notType)
                //

                //dismiss the dialog to avoid RT exceptions
                dialog.dismiss()
                //
            }
            .setNeutralButton("BText") { dialog, _ ->

                //
                val notType = "information"
                funPostBigTextNotification(notType)
                //
                //dismiss the dialog
                dialog.dismiss()
                //
            }
            .setNegativeButton("Normal") { dialog, _ ->
                //
                val notType = "information"
                funPostNormalNotification(notType)
                //
                //dismiss the dialog
                dialog.dismiss()
                //
            }
            .create()
            .show()
        //
    }

    private fun funPostBigPictureNotification(notType: String) {
        //code begins
        val title = editTextMessageTitle?.text.toString()
        val message = editTextMessage?.text.toString()
        val summary = editTextSummaryText?.text.toString()
        if (title.isNotEmpty() || message.isNotEmpty() || summary.isNotEmpty()) {
            //fun begin posting BigPicture Notification
            funBeginPostingBigPictureNotification(title, message, summary, uriImage, notType)
            //
        } else if (title.isEmpty() || message.isEmpty() || summary.isEmpty()) {
            //null values not allowed
            Toast.makeText(requireActivity(), "missing field detected", Toast.LENGTH_SHORT)
                .show()
            //
        }
    }

    private fun funBeginPostingBigPictureNotification(
        title: String,
        message: String,
        summary: String,
        uriImage: Uri?,
        notType: String
    ) {
        //code begins
        //pg
        val pg = ProgressDialog(requireActivity())
        pg.setCancelable(false)
        pg.setTitle("Big Picture")
        pg.setMessage("uploading...")
        pg.create()
        pg.show()
        //
        val uniqueUID = FirebaseAuth.getInstance().uid
        //post to the fStorage to make the process xof Obtaining the Image from the FireFireStore
        val firebaseStorage = FirebaseStorage.getInstance().reference
        if (uniqueUID != null) {
            if (uriImage != null) {
                firebaseStorage.child(BigPicture).child(uniqueUID).putFile(uriImage)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //update the pg
                            pg.setMessage("validating...")
                            //
                            //obtain the download uri from the storage bucket
                            it.result.storage.downloadUrl.addOnSuccessListener {
                                //task successfull
                                pg.setMessage("completing...")
                                val stringUri = it.toString()
                                //call a function to post to the Store of the Backend cloud
                                funPostFstore(stringUri, title, message, summary, notType, pg)
                                //

                            }
                                .addOnFailureListener {
                                    //dismiss the progress dialog
                                    pg.dismiss()
                                    //
                                    //display the error to the administrator
                                    Toast.makeText(
                                        requireActivity(),
                                        "error occurred\n${it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    //
                                }
                            //
                        } else if (it.isSuccessful) {
                            //dismiss the pg
                            pg.dismiss()
                            //
                            //display the error to the administrator
                            Toast.makeText(
                                requireActivity(),
                                "error occurred\n${it.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            //
                        }
                    }
            }
        }
        //
        //code ends
    }

    private fun funPostFstore(
        stringUri: String,
        title: String,
        message: String,
        summary: String,
        notType: String,
        pg: ProgressDialog
    ) {

        //code begins
        val uniqueUID = FirebaseAuth.getInstance().uid
        //creating the keys for the map
        val keyUri = "image"
        val keyTitle = "title"
        val keyMessage = "message"
        val keySummary = "summary"
        val keyType = "notType"
        //creating a hashMap for storage
        val hashMapData = hashMapOf<String, String>(
            keyTitle to title,
            keyMessage to message,
            keySummary to summary,
            keyUri to stringUri,
            keyType to notType
        )
        //
        //begin posting to the google backend
        val firebaseFirestore = FirebaseFirestore.getInstance().collection(BigPicture)
        if (uniqueUID != null) {
            firebaseFirestore.document(uniqueUID).set(hashMapData).addOnCompleteListener {

                if (it.isSuccessful) {
                    //post successfully posted
                    pg.dismiss()
                    //
                    Toast.makeText(requireActivity(), "posted successFully", Toast.LENGTH_LONG)
                        .show()
                } else if (it.isSuccessful) {
                    //pg dismiss
                    pg.dismiss()
                    //
                    Toast.makeText(
                        requireActivity(),
                        "error\n${it.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        //
        //code ends
    }

    private fun funPostNormalNotification(notType: String) {
        //code begins
        val title = editTextMessageTitle?.text.toString()
        val message = editTextMessage?.text.toString()
        if (title.isNotEmpty() or (message.isNotEmpty())) {

            //begin posting normal notification
            funBeginPostingNormal(title, message, notType)
            //
        } else {
            //null values not allowed
            Toast.makeText(requireActivity(), "missing field detected", Toast.LENGTH_SHORT).show()
            //
        }

        //code ends
    }

    private fun funBeginPostingNormal(title: String, message: String, notType: String) {
        //pg
        val pg = ProgressDialog(requireActivity())
        pg.setMessage("posting...")
        pg.setCancelable(false)
        pg.create()
        pg.show()
        //
        //code begins
        //get uniqueID
        val uniqueUID = FirebaseAuth.getInstance().uid
        //map of the data
        val keyTitle = "title"
        val keyMessage = "message"
        val keyType = "notType"
        val hashMapData = hashMapOf<String, String>(
            keyTitle to title,
            keyMessage to message,
            keyType to notType
        )
        //
        //init the collection

        //post to the backend
        uniqueUID?.let {
            FirebaseFirestore.getInstance().collection(Normal).document(
                it
            )
        }?.set(hashMapData)?.addOnCompleteListener {
            if (it.isSuccessful) {
                //normal text posted successfully
                pg.dismiss()
                //
                //toast post was successful
                Toast.makeText(requireActivity(), "posted successfully", Toast.LENGTH_LONG).show()
                //
            } else if (!it.isSuccessful) {
                //posting failed
                pg.dismiss()
                //
                //toast error
                Toast.makeText(
                    requireActivity(),
                    "error:\n${it.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
                //
            }
        }
        //

        //
        //code
    }

    private fun funPostBigTextNotification(notType: String) {
        //code begins
        val title = editTextMessageTitle?.text.toString()
        val message = editTextMessage?.text.toString()
        val summary = editTextSummaryText?.text.toString()
        if (message != null) {
            if (title.isNotEmpty() || message.isNotEmpty() || summary.isNotEmpty()) {
                //begin posting the BigText Notification
                funBeginPostingBigTextNotification(title, message, summary, notType, uriImage)
                //
            } else if (title.isEmpty() || message.isEmpty() || summary.isEmpty()) {
                //null values not allowed
                Toast.makeText(requireActivity(), "missing field detected", Toast.LENGTH_SHORT)
                    .show()
                //
            }

        }
        //code ends
    }

    private fun funBeginPostingBigTextNotification(
        title: String,
        message: String,
        summary: String,
        notType: String,
        uriImage: Uri?
    ) {

        //code begins
        //pg
        val pg = ProgressDialog(requireActivity())
        pg.setCancelable(false)
        pg.setTitle("BigText")
        pg.setMessage("posting")
        pg.create()
        pg.show()
        //
        //obtain unique id
        val uniqueUID = FirebaseAuth.getInstance().uid
        //post the icon first then ease getting the download uri
        val firebaseStorage =
            uriImage?.let {
                if (uniqueUID != null) {
                    FirebaseStorage.getInstance().reference.child(BigText).child(uniqueUID).putFile(
                        it
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //validate the pg
                            pg.setMessage("completing")
                            //
                            //obtain the download uri
                            it.result.storage.downloadUrl.addOnSuccessListener {
                                //validate the pg with validating
                                pg.setMessage("updating...")
                                //
                                val stringUri = it.toString()
                                //call function and pass the image uri to it the post to the fireStore
                                funUploadToFireStoreBigText(
                                    stringUri,
                                    title,
                                    message,
                                    summary,
                                    notType,
                                    pg
                                )
                                //

                            }
                                .addOnFailureListener {
                                    //dismiss the pg
                                    pg.dismiss()
                                    //
                                    //show the exception
                                    Toast.makeText(
                                        requireActivity(),
                                        "error\n${it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    //
                                }
                            //
                        } else if (!it.isSuccessful) {
                            //dismiss the pg
                            pg.dismiss()
                            //
                            //file storage error
                            Toast.makeText(
                                requireActivity(),
                                "error\n${it.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            //
                        }
                    }
                }
            }
        //
        //
        //code ends
    }

    private fun funUploadToFireStoreBigText(
        stringUri: String,
        title: String,
        message: String,
        summary: String,
        notType: String,
        pg: ProgressDialog
    ) {
        //code begins
        val uniqueUID = FirebaseAuth.getInstance().uid
        //keys to the hashmap data
        val keyTitle = "title"
        val keyMessage = "message"
        val keySummary = "summary"
        val keyType = "notType"
        val keyUri = "image"
        //hash Mapping the data
        val hashMap = hashMapOf<String, String>(
            keyTitle to title,
            keyMessage to message,
            keySummary to summary,
            keyType to notType,
            keyUri to stringUri
        )
        //fireStore Data update
        val firebaseFireStore = FirebaseFirestore.getInstance().collection(BigText)
        //
        if (uniqueUID != null) {
            firebaseFireStore.document(uniqueUID).set(hashMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    //dismiss the pg
                    pg.dismiss()
                    //

                    Toast.makeText(requireActivity(), "Posted Successfully", Toast.LENGTH_LONG)
                        .show()
                } else if (!it.isSuccessful) {
                    //dismiss tge pg
                    pg.dismiss()
                    //
                    //toast the failure to the admin owner of the mobile application marketing
                    Toast.makeText(
                        requireActivity(),
                        "error\n${it.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        //code ends
    }


    private val galleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data?.data != null) {
                uriImage = it.data!!.data
                //set the image on the imageview
                imageView?.setImageURI(uriImage)
                //
            }
        }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            messageTypeSpinner = parent.selectedItem.toString()
        }


        //control the type of the message console control items being sown
        if (messageTypeSpinner.contains("picture", true)) {
            //
            funBigPictureNotification()
            //

        } else if (messageTypeSpinner.contains("Text", true)) {
            //
            funBigTextNotification()
            //
        } else if (messageTypeSpinner.contains("normal", true)) {
            //
            funNormalNotification()
            //

        }
        //
    }

    private fun funNormalNotification() {
        //code begins
        //disable the image image view and the summary text not requires
        imageView?.apply {
            visibility = View.GONE
        }

        editTextSummaryText?.apply {
            visibility = View.GONE
        }
        //code ends
    }

    private fun funBigTextNotification() {

        //code begins
        //enable image view and the  summary text
        imageView?.apply {
            visibility = View.VISIBLE
        }

        editTextSummaryText?.apply {
            visibility = View.VISIBLE
        }
        //code begins
    }

    private fun funBigPictureNotification() {
        //code begins
        //enable the imageview and the  and the summary text
        imageView?.apply {
            visibility = View.VISIBLE
        }
        editTextSummaryText?.apply {
            visibility = View.VISIBLE
        }
        //
        //code ends
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }
}