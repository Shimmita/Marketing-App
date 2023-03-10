package com.shimitadouglas.marketcm.mains

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.sharedPreferenceName
import com.shimitadouglas.marketcm.modal_sheets.ModalPrivacyMarket
import com.shimitadouglas.marketcm.notifications.BigPictureNotificationMostLogin
import com.shimitadouglas.marketcm.utilities.FileSizeDeterminant
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

class Registration : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        private const val TAG = "Registration"
        const val ComradeUser = "Comrade Users"
    }

    //image uri path
    private var uriPath: Uri? = null
    //

    //init of globals
    private var universityRegistrationList = arrayOf<String>()


    //init globals
    private lateinit var linearLayoutParentRegistration: LinearLayout
    private lateinit var spinnerUniversity: Spinner
    private lateinit var btnRegistration: MaterialButton
    private lateinit var editTextFirstName: TextInputEditText
    private lateinit var editTextLastName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var circleProfileImage: CircleImageView
    private lateinit var editPassword: TextInputEditText

    //val holding the spinner
    private lateinit var spinnerReturned: String
    //

    //
    private lateinit var stringArrayAdapter: ArrayAdapter<String>
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        //call functionButton
        funInitGlobals()
        //animate parent
        funAnimParent()
        //

        //setting onclick listener on the imageview and button login
        btnRegistration.setOnClickListener {

            //code begins
            funRegistrationBegin()
            //code ends
        }

        circleProfileImage.setOnClickListener {

            //check if the permission read_write external storage are granted or null
            Dexter.withContext(this@Registration)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        //permissions allowed (granted) launch the gallery activity
                        //start intent pick image
                        val intentPickImage = Intent()
                        intentPickImage.action = Intent.ACTION_PICK
                        intentPickImage.type = "image/*"

                        //launching the gallery activity
                        galleryLaunch.launch(intentPickImage)
                        //
                        //
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        //permission no granted @rationale dialog show why the permissions required are mandatory
                        funShowAlertPermissionRationale()
                        //
                    }
                }).check()
            //

        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funShowAlertPermissionRationale() {
        //code begins
        //show  the alert of provided by the rationale dialog
        val alertPermissionRationale = MaterialAlertDialogBuilder(this@Registration)
        alertPermissionRationale.setTitle("Permissions")
        alertPermissionRationale.setIcon(R.drawable.ic_info)
        alertPermissionRationale.setMessage(
            "Market CM requires that the requested permissions are necessary for it to function properly." +
                    " grant the permissions to use the application"
        )
        alertPermissionRationale.background =
            resources.getDrawable(R.drawable.material_six, theme)
        alertPermissionRationale.setCancelable(false)
        alertPermissionRationale.setPositiveButton("do") { dialog, _ ->
            //start the intent of launching the settings for app info
            val intentSettingsApp = Intent()
            intentSettingsApp.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            //
            dialog.dismiss()
            //
        }
        alertPermissionRationale.create()
        alertPermissionRationale.show()

        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funRegistrationBegin() {

        //code begins
        //checking the legitimacy of the credentials entered by the user
        val dataFirstName = editTextFirstName.text.toString()
        val lasNameData = editTextLastName.text.toString()
        val emailData = editTextEmail.text.toString()
        val phoneData = editTextPhone.text.toString()
        val passwordData = editPassword.text.toString()
        //to obtain uni selection check the returned value from the spinner
        val spinnerDataUniversity = spinnerReturned
        //

        if (TextUtils.isEmpty(dataFirstName)) {
            editTextFirstName.error = "field is mandatory!"
        } else if (TextUtils.isEmpty(lasNameData)) {
            editTextLastName.error = "field is mandatory!"

        } else if (TextUtils.isEmpty(emailData)) {
            editTextEmail.error = "field is mandatory!"

        } else if (TextUtils.isEmpty(phoneData)) {
            editTextPhone.error = "field is mandatory!"

        } else if (TextUtils.isEmpty(spinnerDataUniversity)) {
            Toast.makeText(this@Registration, "University Not Selected", Toast.LENGTH_LONG).show()
        } else if (phoneData.length < 10) {
            editTextPhone.error = "number less than 10 digits!"
        } else if (TextUtils.isEmpty(passwordData)) {
            editPassword.error = "account password is missing!"
        } else if (passwordData.length < 6) {
            editPassword.error = "password must be at least 6 characters long"
        } else if (uriPath == null) {

            //create a snack
            Snackbar.make(
                linearLayoutParentRegistration,
                "profile picture is missing!",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Ok,fix") {
                //setting the border color of the image to magenta for easier evaluation
                circleProfileImage.borderColor = resources.getColor(R.color.white, theme)

                //animate the image
                circleProfileImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@Registration, R.anim.abc_fade_out
                    )
                )

                //
            }.setBackgroundTint(
                resources.getColor(
                    R.color.bright_foreground_inverse_material_dark, theme
                )
            ).show()
            //

        } else {

            //ensure that the user agrees to the terms and conditions
            var selected = ""
            val arrayOfTerms = resources.getStringArray(R.array.terms_and_conditions)
            val alertUSerTermsAndConditions = MaterialAlertDialogBuilder(this@Registration)
            alertUSerTermsAndConditions.setIcon(R.drawable.cart)
            alertUSerTermsAndConditions.background =
                resources.getDrawable(R.drawable.material_11, theme)
            alertUSerTermsAndConditions.setTitle("Terms And Conditions")
            alertUSerTermsAndConditions.setSingleChoiceItems(arrayOfTerms, 2) { _, which ->
                selected = arrayOfTerms[which]
            }
            alertUSerTermsAndConditions.setPositiveButton("register") { dialog, _ ->

                //check if is null the value of the selected item
                if (selected.isNotEmpty()) {
                    if (selected.equals("i agree terms and conditions", true)) {
                        //user agreed to the terms proceed registration
                        //call function proceed registration
                        //parameters passed to the function=email,password,phone,university,firstname,lastname,image url
                        funCreateUserNew(
                            emailData,
                            passwordData,
                            dataFirstName,
                            lasNameData,
                            phoneData,
                            spinnerDataUniversity,
                            uriPath!!
                        )
                        //

                        //dismiss the dialog
                        dialog.dismiss()
                        //
                    } else if (selected.equals("i disagree terms and conditions", true)) {
                        funToastyFail("registration process cannot proceed!")
                        //user disagreed just dismiss the dialog
                        dialog.dismiss()
                        //
                    }

                } else if (selected.isEmpty()) {
                    funToastyFail("select an option!")
                }
                //
            }
            alertUSerTermsAndConditions.setNegativeButton("terms") { dialog, _ ->

                //show the terms and conditions to the user
                val modalSheetPrivacyAndTerms = ModalPrivacyMarket("policy")
                modalSheetPrivacyAndTerms.show(
                    supportFragmentManager,
                    "privacy_policy_terms_registration"
                )
                //
                dialog.dismiss()
            }
            alertUSerTermsAndConditions.create()
            alertUSerTermsAndConditions.show()

        }
        //
        //code ends

    }


    private fun funCreateUserNew(
        emailData: String,
        passwordData: String,
        dataFirstName: String,
        lasNameData: String,
        phoneData: String,
        spinnerDataUniversity: String,
        uriPath: Uri
    ) {

        //code begins
        //creating instance of firebase
        val fBaseInit = FirebaseAuth.getInstance()
        //creating a progress dialog to sho progression of registration
        val createNewUserProgressDialog = ProgressDialog(this@Registration)
        createNewUserProgressDialog.setTitle("Registering $dataFirstName")
        createNewUserProgressDialog.setMessage("starting registration...")
        createNewUserProgressDialog.setCancelable(false)
        createNewUserProgressDialog.show()
        createNewUserProgressDialog.create()

        //creating the new user from the instance
        fBaseInit.createUserWithEmailAndPassword(emailData, passwordData).addOnCompleteListener {
            //if user is created successfully
            if (it.isSuccessful) {
                //updating the details of progressDialog
                createNewUserProgressDialog.setMessage("data uploading...")
                //
                //create function storing the data to fireStore then image cloudStore
                funStoreDataFireStore(
                    emailData,
                    passwordData,
                    dataFirstName,
                    lasNameData,
                    phoneData,
                    spinnerDataUniversity,
                    uriPath,
                    createNewUserProgressDialog
                )
                //
            }
            //user account creation was a fail
            else if (!it.isSuccessful) {
                //dismiss the progress dialog
                createNewUserProgressDialog.dismiss()
                //
                //call function show failure of registration
                funFailRegistrationAlert(it, dataFirstName)
                //
            }

            //

        }
        //
        //
        //code ends

    }

    @SuppressLint("SimpleDateFormat")
    private fun funStoreDataFireStore(
        emailData: String,
        passwordData: String,
        dataFirstName: String,
        lasNameData: String,
        phoneData: String,
        spinnerDataUniversity: String,
        uriPath: Uri,
        createNewUserProgressDialog: ProgressDialog
    ) {
        //code begins
        //creating instances for finding date so that the dat of registration be obtained
        val timeUsingCalendar = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val formattedTime = dateFormat.format(timeUsingCalendar)
        //

        //creating a firebase instance then acquiring a uniqueUID for differentiation of users Under One Collection Of Document ComradeUsers
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        //creating instance of fStore
        val fStoreUsers = FirebaseFirestore.getInstance()

        //creating the keys for the user data in respective mapping to the data
        val keyEmail = "Email"
        val keyPassword = "Password"
        val keyFirstName = "FirstName"
        val keyLastName = "LastName"
        val keyUniversity = "University"
        val keyPhone = "PhoneNumber"
        val keyImageUri = "ImagePath"
        val keyRegistrationDate = "registrationDate"
        val keyCanPost = "canPost"
        //

        //separate declaration of can post to the market is yes
        val dataCanPost = "true"
        //

        //creating the hashmap for the data be stored in fireStore
        val mapUserData = hashMapOf(
            keyEmail to emailData,
            keyPassword to passwordData,
            keyFirstName to dataFirstName,
            keyLastName to lasNameData,
            keyPhone to phoneData,
            keyUniversity to spinnerDataUniversity,
            keyImageUri to uriPath.toString(),
            keyRegistrationDate to formattedTime,
            keyCanPost to dataCanPost
        )
        //

        //saving the user data to the server
        currentUID?.let {
            fStoreUsers.collection(ComradeUser).document(
                it
            )
        }?.set(mapUserData)?.addOnCompleteListener {
            //successfully saved the user data to fStore
            if (it.isSuccessful) {
                //update the message of the progress dialog
                createNewUserProgressDialog.setMessage("data validation...")
                //create a function that will take keyImageUri in order to update the hashMapData with downloadable uri at fStore
                //specifically @keyImageUri
                funFabStorageImage(createNewUserProgressDialog, keyImageUri, currentUID)
                //
            }
            //error occurred while saving the data to fStore
            else if (!it.isSuccessful) {
                //dismiss the progressDialog
                createNewUserProgressDialog.dismiss()
                //
                //alertUser of Data Entry Failure Using the SnackBar
                funSnackBarAlertFail()
                //

            }

        }
        //
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funFabStorageImage(
        createNewUserProgressDialog: ProgressDialog,
        keyImageUri: String,
        currentUID: String
    ) {
        //code begins   "$ComRadeUser/$currentUID/${fabUserEmail}
        val fabUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val fabStorageInt =
            fabUserEmail?.let {
                FirebaseStorage.getInstance().reference.child(ComradeUser).child(currentUID).child(
                    it
                )
            }
        //putting the uri path to the fabStorage
        if (fabStorageInt != null) {
            uriPath?.let {
                fabStorageInt.putFile(it).addOnCompleteListener(
                    this@Registration
                ) { it ->
                    if (it.isSuccessful) {
                        //task is successful thus update the message of the progressDialog
                        createNewUserProgressDialog.setMessage("finalising...")
                        //
                        //get the download uri of the saved image then update the fStore uri path
                        fabStorageInt.downloadUrl.addOnSuccessListener {
                            //update the status of the progress dialog
                            createNewUserProgressDialog.setMessage("almost done...")
                            //create a function to update the fStore Path With this new URi downloadable
                            val uriDownLoadAble = it.toString()
                            funUpdateFStore(
                                uriDownLoadAble,
                                keyImageUri,
                                createNewUserProgressDialog
                            )
                            //
                            //

                        }.addOnFailureListener {

                            //dismiss the progress dialog
                            createNewUserProgressDialog.dismiss()
                            //

                            //failed to obtain the download Uri alert
                            val alertUriFailure = MaterialAlertDialogBuilder(this@Registration)
                            alertUriFailure.setMessage("we encountered an error while completing your registration please try again later thank you")
                            alertUriFailure.setIcon(
                                resources.getDrawable(
                                    R.drawable.android,
                                    theme
                                )
                            )
                            alertUriFailure.show()
                            alertUriFailure.create()
                            //
                        }
                        //
                        //

                    }
                    //failed to upload the image to the fabStorage
                    else {
                        //dismiss progressDialog
                        createNewUserProgressDialog.dismiss()
                        //
                        //show snackBar Of this error
                        Snackbar.make(
                            linearLayoutParentRegistration,
                            "we encountered an error while uploading the data!",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("what then?") {
                            Snackbar.make(
                                linearLayoutParentRegistration,
                                "check your internet connection",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }.show()
                        //

                    }

                }
            }
        }
        //
        //code ends
    }

    private fun funUpdateFStore(
        uriDownLoadAble: String,
        keyImageUri: String,
        createNewUserProgressDialog: ProgressDialog
    ) {

        //code begins
        //creating a firebase instance then acquiring a uniqueUID for differentiation of users Under One Collection Of Document ComradeUsers
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        //creating instance of fStore
        val fStoreUsers = FirebaseFirestore.getInstance()
        //
        val mapUpdate = hashMapOf(keyImageUri to uriDownLoadAble)
        //starting the process of updating the fStore part keyImageUri
        if (currentUID != null) {
            fStoreUsers.collection(ComradeUser).document(currentUID)
                .update(mapUpdate as Map<String, String>).addOnCompleteListener {
                    if (it.isSuccessful) {

                        //dismiss the progress dialog of completed successful registration
                        createNewUserProgressDialog.dismiss()
                        //
                        //successfully Registered the User
                        callFunctionShowSuccessReg()
                        //

                    }

                    //overall failure
                    else if (!it.isSuccessful) {

                        //dismiss the progressBar
                        createNewUserProgressDialog.dismiss()
                        //
                        //show snackBar Of this error
                        Snackbar.make(
                            linearLayoutParentRegistration,
                            "we encountered an error while uploading the data!",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("what then?") {
                            Snackbar.make(
                                linearLayoutParentRegistration,
                                "check your internet connection",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }.show()
                    }


                }
        }

        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun callFunctionShowSuccessReg() {
        //code begins
        val alertSuccess = MaterialAlertDialogBuilder(this@Registration)
        alertSuccess.setTitle("CONGRATULATIONS")
        alertSuccess.setIcon(R.drawable.ic_nike_done)
        alertSuccess.setCancelable(false)
        alertSuccess.background = resources.getDrawable(R.drawable.material_seven, theme)
        alertSuccess.setMessage(
            "You have successfully registered with Comrade Market(Market CM)." +
                    "\n\nLogin to your account using your" +
                    " \nEMAIL and PASSWORD"
        )
        alertSuccess.setPositiveButton(
            "Login"
        ) { dialogInterface, _ ->
            //the user account is currently logged in. better log him out
            val firebaseSignOut = FirebaseAuth.getInstance()
            firebaseSignOut.signOut()

            //show big notification of congratulations
            //big pic test
            val arrayByName = arrayOf(
                "douglasshimmita",
                "shimmitadouglas",
                "douglasshimita3@gmail.com",
                "shimitadouglas@gmail.com"
            )
            val arrayBy = arrayOf("developed by", "powered by", "moulded by", "made by")
            val random = Random.nextInt(4)

            val bigPictureNotificationMostLogin = BigPictureNotificationMostLogin(
                this@Registration,
                BitmapFactory.decodeResource(resources, R.drawable.cart),
                "Welcome",
                "registration process was successful",
                R.drawable.ic_cart, "${arrayBy[random]}:${arrayByName[random]}",
                "Congratulations"
            )
            bigPictureNotificationMostLogin.funCreateBigPictureNotification()


            //dismiss the dialog
            dialogInterface.dismiss()
            //
            if ((FirebaseAuth.getInstance().currentUser) != null) {
                //clear all the data saved in the shared preference
                val sharedPreferences =
                    getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                //sign ou the current user
                FirebaseAuth.getInstance().signOut()
                //
            }

            //finish the app and let the user login via the notification
            finish()
            //
        }
        alertSuccess.show()
        alertSuccess.create()
        //code ends

        //todo:here was final

    }


    private fun funSnackBarAlertFail() {
        //code begins
        Snackbar.make(
            linearLayoutParentRegistration,
            "registration failed while uploading your data!",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("try again") {
            Snackbar.make(
                linearLayoutParentRegistration,
                "check your internet connection",
                Snackbar.LENGTH_SHORT
            ).show()
        }.show()      //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funFailRegistrationAlert(it: Task<AuthResult>, dataFirstName: String) {

        //function random salutation
        val randomGen = Random.nextInt(2)
        val greetingArray = arrayOf("Hello,", "Dear,", "Hi,")
        //
        //show alert that failed is registration
        val alertDialogFailure = MaterialAlertDialogBuilder(this@Registration)
        alertDialogFailure.setTitle("Registration Failed")
        alertDialogFailure.setIcon(resources.getDrawable(R.drawable.ic_warning, theme))
        alertDialogFailure.setMessage(
            "${greetingArray[randomGen]} $dataFirstName\nyour registration process was not successful due to:\n\n" + it.exception?.message.toString() +
                    "\n" +
                    "\nRecommendation:\nplease try again later "
        )
        alertDialogFailure.setPositiveButton(
            "try again"
        ) { dialogInterface, _ ->
            //dismiss dialog
            dialogInterface.dismiss()
            //
            //code ends
        }
        alertDialogFailure.setNegativeButton(
            "quit all"
        ) { dialogInterface, _ ->

            //code begins
            //terminate the overall process
            finish()
            dialogInterface.dismiss()
            exitProcess(0)
            //
            //code ends
        }
        alertDialogFailure.show()
        //
    }


    private val galleryLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //code begin
            if (it.data?.data != null) {
                //image was picked successfully
                if (it.resultCode == RESULT_OK) {
                    //set the image to the circle profile
                    uriPath = it.data!!.data!!

                    //check the size of the image that was picked should be less than 2MB
                    val initClassSize = FileSizeDeterminant(this)
                    val imageSizeBytes = initClassSize.funGetSize(uriPath)
                    val converter = 1024f
                    val imageSizeKB = imageSizeBytes / converter
                    val limitKB = 2500
                    if (imageSizeKB > limitKB) {
                        //alert user file size too big
                        Toasty.custom(
                            this@Registration,
                            "pick an image less than 2.5MB\n" +
                                    "Your image is ${imageSizeKB / converter}MB !",
                            R.drawable.ic_info,
                            R.color.colorWhite,
                            Toasty.LENGTH_LONG,
                            true,
                            false
                        ).show()
                        //
                    } else {
                        //code begins
                        //file size is okay
                        circleProfileImage.setImageURI(uriPath)
                        //
                        //snack the image a success
                        Snackbar.make(
                            linearLayoutParentRegistration,
                            "profile picture updated successfully",
                            Snackbar.LENGTH_INDEFINITE
                        ).setTextColor(
                            Color.parseColor("#ffffff")
                        ).setBackgroundTint(resources.getColor(R.color.black, theme))
                            .setAction("Ok") {
                                //animate the profile
                                circleProfileImage.startAnimation(
                                    AnimationUtils.loadAnimation(
                                        this@Registration, R.anim.rotate
                                    )
                                )

                                //make it visible the button register
                                btnRegistration.apply {
                                    visibility = View.VISIBLE
                                }
                                //
                            }.show()
                        //


                        //
                        //code ends
                    }
                    //

                }
                if (it.resultCode == RESULT_CANCELED) {

                    //image cancelled
                    Toast.makeText(
                        this@Registration, "process has been cancelled!", Toast.LENGTH_LONG
                    ).show()

                }
            } else {
                //data is null from uri hence its an error
                Toast.makeText(
                    this@Registration, "unknown error occurred", Toast.LENGTH_LONG
                ).show()
            }
            //code end
        }

    private fun funAnimParent() {
        //code start
        val layoutControl =
            LayoutAnimationController(AnimationUtils.loadAnimation(this@Registration, R.anim.down))
        layoutControl.apply {
            order = LayoutAnimationController.ORDER_NORMAL
            delay = 1.0f
        }

        linearLayoutParentRegistration.layoutAnimation = layoutControl
        linearLayoutParentRegistration.startLayoutAnimation()

        //code ends
    }

    private fun funInitGlobals() {
        //code begins
        spinnerUniversity = findViewById(R.id.registrationSpinnerUniversity)
        btnRegistration = findViewById(R.id.btnRegister)
        editTextFirstName = findViewById(R.id.registrationFirstName)
        editTextEmail = findViewById(R.id.registrationEmail)
        editTextPhone = findViewById(R.id.registrationPhoneNumber)
        editTextLastName = findViewById(R.id.registrationLastName)
        linearLayoutParentRegistration = findViewById(R.id.parentLinearRegistration)
        circleProfileImage = findViewById(R.id.circleProfileImage)
        editPassword = findViewById(R.id.registrationPassword)
        //

        //inflating the university list from the string resources
        universityRegistrationList = resources.getStringArray(R.array.universities_ke)
        //sort the universities naturally
        universityRegistrationList.sort()
        //

        stringArrayAdapter = ArrayAdapter(
            this@Registration,
            android.R.layout.simple_selectable_list_item,
            universityRegistrationList
        )
        //sorting the elements in the spinner
        stringArrayAdapter.setNotifyOnChange(true)
        stringArrayAdapter.sort(Comparator.naturalOrder())
        spinnerUniversity.adapter = stringArrayAdapter

        //setting onItemSelectedListener on the spinner
        spinnerUniversity.onItemSelectedListener = this
        //

        //code ends


    }



    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            spinnerReturned = universityRegistrationList[p2]

        }
        Log.d(TAG, "onItemSelected: university:$spinnerReturned")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    //function Toasty Fail
    private fun funToastyFail(message: String) {
        Toasty.custom(
            this@Registration,
            message,
            R.drawable.ic_warning,
            R.color.androidx_core_secondary_text_default_material_light,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }

    //function Toasty Successful
    private fun funToastyShow(s: String) {
        Toasty.custom(
            this@Registration,
            s,
            R.drawable.ic_nike_done,
            R.color.colorWhite,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }
}