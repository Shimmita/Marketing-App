package com.shimitadouglas.marketcm.fragment_admin_control_panel

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shimitadouglas.marketcm.R

class PostApk : Fragment() {
    companion object {
        const val collectionApk = "Apk"
        const val documentApk = "apk"
        private const val TAG = "PostApk"
    }

    //declaration of the globals
    lateinit var appCompatButtonPickApk: AppCompatButton
    lateinit var textViewDisplayUriPathApk: TextView
    lateinit var linearLayoutApk: LinearLayout
    lateinit var progressDialogPostApk: ProgressDialog

    //
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        val view: View = inflater.inflate(R.layout.post_apk_admin_fragment, container, true)
        appCompatButtonPickApk = view.findViewById(R.id.btnPickApk)
        textViewDisplayUriPathApk = view.findViewById(R.id.tvDisplayApkUriPath)
        linearLayoutApk = view.findViewById(R.id.linearPostApk)

        //set listener on the button pick apk
        appCompatButtonPickApk.setOnClickListener {
            //fun pick apk file
            funPickApkFile()
            //
        }

        //init
        funInitProgressD()
        //code ends
        return view
    }

    private fun funInitProgressD() {
        //code begins
        val viewProgression = LayoutInflater.from(this@PostApk.context)
            .inflate(R.layout.general_progress_dialog_view, null, false)
        progressDialogPostApk = ProgressDialog(this@PostApk.context)
        progressDialogPostApk.setView(viewProgression)
        progressDialogPostApk.setIcon(R.drawable.ic_upload)
        progressDialogPostApk.setCancelable(false)
        progressDialogPostApk.setTitle("latest apk version")
        progressDialogPostApk.setMessage("sending")
        //code ends
    }

    private fun funPickApkFile() {
        //code begins
        val intentPickApk = Intent()
        intentPickApk.type="apk/*"
        activityPickFileApk.launch(intentPickApk)

        //code ends
    }

    private val activityPickFileApk =
        requireActivity().registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.data
                if (data != null) {
                    //set the data to the tv
                    textViewDisplayUriPathApk.text = data.toString()
                    //
                    //toasty post the apk to the backend
                    Snackbar.make(linearLayoutApk, "post the apk?", Snackbar.LENGTH_INDEFINITE)
                        .setAction("yes") {
                            //call fun postApk
                            funPostApkBackend(data)
                            //
                        }.show()
                    //

                } else {
                    Toast.makeText(requireActivity(), "empty data!", Toast.LENGTH_SHORT).show()
                }
            } else if (it.resultCode == RESULT_CANCELED) {
                Toast.makeText(requireActivity(), "cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun funPostApkBackend(data: Uri) {
        //show the progressD
        progressDialogPostApk.create()
        progressDialogPostApk.show()
        //
        //code begins
        val stringChildApk = "latest_market_cm_apk"
        //post the data to the storage Backend and then obtain the download uri be posted to the fStore
        val storageRefApk = FirebaseStorage.getInstance().reference
        storageRefApk.child(stringChildApk).putFile(data).addOnCompleteListener {
            if (it.isSuccessful) {
                //update the message of the progressD
                progressDialogPostApk.setMessage("almost done")
                //
                //getting the download uri
                it.result.storage.downloadUrl.addOnCompleteListener {
                    if (!it.isSuccessful) {

                        val stringURi = it.result.toString()
                        //call function to save the data in store
                        funStoreUrl(stringURi)
                        //
                    } else if (!it.isSuccessful) {
                        //dismiss the progressD
                        progressDialogPostApk.dismiss()
                        //
                        Snackbar.make(
                            linearLayoutApk,
                            "failed download url",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("ok", null).show()
                    }
                }
                //

            } else if (!it.isSuccessful) {
                //dismiss the progressD
                progressDialogPostApk.dismiss()
                //
                //show snack
                Snackbar.make(linearLayoutApk, "failed to send apk!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", null).show()
                //
                return@addOnCompleteListener
            }
        }
        //code begins
    }

    private fun funStoreUrl(stringURi: String) {
        //code begins
        val keyData = "apkPath"
        val mapData = hashMapOf(
            keyData to stringURi
        )

        //init store
        val storeApkPath = FirebaseFirestore.getInstance()
        storeApkPath.collection(collectionApk).document(documentApk).set(mapData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //dismiss the progressD
                    progressDialogPostApk.dismiss()
                    //
                    Snackbar.make(
                        linearLayoutApk,
                        "successfully finished",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("ok", null).show()

                } else if (!it.isSuccessful) {
                    //disable the progressD
                    progressDialogPostApk.dismiss()
                    //
                    Snackbar.make(
                        linearLayoutApk,
                        "failed at fire_store path!",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("ok", null).show()

                    return@addOnCompleteListener
                }
            }
        //code end
    }
}