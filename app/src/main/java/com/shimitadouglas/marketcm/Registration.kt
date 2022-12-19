package com.shimitadouglas.marketcm

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random
import kotlin.system.exitProcess

class Registration : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        val REQUEST_CODE_PICK_IMAGE = 1224;
        private const val TAG = "Registration"
        val ComRadeUser = "ComradeUsers"
    }

    //image uri path
    var uriPath: Uri? = null
    //

    //init of globals
    var university = arrayOf(
        "Maseno University",
        "Nairobi University",
        "Laikipia University",
        "Meru University",
        "Gretsa University",
        "DayStar University",
        "Garissa University",
        "Technical University Of Mombasa",
        "Pwani University",
        "Jomo Kenyatta University",
        "Kenyatta University",
        "Moi University",
        "Chuka University",
        "Kibabii University",
        "Saint Paul's University",
        "Maasai Mara University",
        "Alupe University",
        "Kisii University",
        "Adventist University Of Africa",
        "Africa International University",
        "Africa Nazarene University",
        "Amref International University",
        "Dedan Kimathi University",
        "Egerton University",
        "Great Lakes University",
        "International Leadership University",
        "Jaramogi Oginga Odinga University",
        "Kabarak University",
        "KAG University",
        "Karatina Universty",
        "KCA University",
        "Kenya Highlands University",
        "Kenya Methodist University",
        "Kirinyaga University",
        "Kirir Women's University",
        "Lukenya University",
        "Machakos University",
        "Management University Of Africa",
        "Masinde Muliro University",
        "Mount Kenya University",
        "Multimedia University ",
        "Murang'a University",
        "Pan Africa Christian University",
        "Pioneer International University",
        "RAF International University",
        "Riara University",
        "Rongo University",
        "Scott Christian University",
        "South Eastern Kenya University",
        "Taita Taveta Universty",
        "Strathmore University",
        "Technical university Of Kenya",
        "Catholic University Of Eastern Africa",
        "East African University",
        "Presbyterian University",
        "Umma University",
        "United States International university",
        "Baraton University",
        "Embu University",
        "Kabianga University",
        "Zetech University",
        "Uzima University",
        "University Of Eldoret",
        "Turkana University",
        "Tom Mboya University",
        "Tharaka University",
        "Tangaza University",
        "Koitaleel Samoei University",
        "Kaimosi Univesity",
        "SEKU University",
        "Bomet University",
        "Co-operative University Of Kenya",
        "Marist International University",
        "Management University of Africa"
    )


    //init globals
    lateinit var linearLayoutParentRegistration: LinearLayout
    lateinit var spinnerUniversity: Spinner
    lateinit var btnRegistration: MaterialButton
    lateinit var editTextFirstName: TextInputEditText
    lateinit var editTextLastName: TextInputEditText
    lateinit var editTextEmail: TextInputEditText
    lateinit var editTextPhone: TextInputEditText
    lateinit var circProfileImage: CircleImageView
    lateinit var editPassword: TextInputEditText

    //val holding the spinner
    lateinit var spinner_returned: String
    //

    //
    lateinit var array_adapter: ArrayAdapter<String>
    //

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        //call function fullscreen
        funFullScreen()
        //
        //call functionButton
        funInitGlobals()
        //
        //animate parent
        funAnimParent()
        //

        //setting onclick listener on the imageview and button login
        btnRegistration.setOnClickListener {

            //code begins

            funRegistrationBegin()
            //code ends
        }

        circProfileImage.setOnClickListener {

            //start intent pick image
            val intentPickImage = Intent()
            intentPickImage.action = Intent.ACTION_PICK
            intentPickImage.type = "image/*"

            //launching the gallery activity
            galleryLaunch.launch(intentPickImage)

            //

        }


    }

    private fun funRegistrationBegin() {

        //code begins
        //checking the legitimacy of the credentials entered by the user
        val data_first_name = editTextFirstName.text.toString()
        val las_name_data = editTextLastName.text.toString()
        val email_data = editTextEmail.text.toString()
        val phone_data = editTextPhone.text.toString()
        val password_data = editPassword.text.toString()
        //to obtain uni selection check the returned value from the spinner
        val spinnerDataUniversity = spinner_returned.toString()
        //

        if (TextUtils.isEmpty(data_first_name)) {
            editTextFirstName.error = "field is mandatory!"
        } else if (TextUtils.isEmpty(las_name_data)) {
            editTextLastName.error = "field is mandatory!"

        } else if (TextUtils.isEmpty(email_data)) {
            editTextEmail.error = "field is mandatory!"

        } else if (TextUtils.isEmpty(phone_data)) {
            editTextPhone.error = "field is mandatory!"

        } else if (TextUtils.isEmpty(spinnerDataUniversity)) {
            Toast.makeText(this@Registration, "University Not Selected", Toast.LENGTH_LONG).show()
        } else if (phone_data.length < 10) {
            editTextPhone.error = "number less than 10 digits!"
        } else if (TextUtils.isEmpty(password_data)) {
            editPassword.error = "account password is missing!"
        } else if (uriPath == null) {

            //create a snack
            Snackbar.make(
                linearLayoutParentRegistration,
                "profile picture is missing!",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Ok,fix") {
                //setting the border color of the image to magenta for easier evaluation
                circProfileImage.borderColor = resources.getColor(R.color.red, theme)

                //animate the image
                circProfileImage.startAnimation(
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
            //call function proceed registration
            //parameters passed to the function=email,password,phone,university,firstname,lastname,image url
            funCreateUserNew(
                email_data,
                password_data,
                data_first_name,
                las_name_data,
                phone_data,
                spinnerDataUniversity,
                uriPath!!
            );
            //
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
        val baseInit = FirebaseAuth.getInstance()
        //creating a progress dialog to sho progression of registration
        val createNewUserProgressDialog = ProgressDialog(this@Registration)
        createNewUserProgressDialog.setTitle("Registering $dataFirstName")
        createNewUserProgressDialog.setMessage("starting registration...")
        createNewUserProgressDialog.setCancelable(false)
        createNewUserProgressDialog.show()
        createNewUserProgressDialog.create()
        //todo:find out tackling deprecation of progressDialog
        //
        //creating the new user from the instance
        baseInit.createUserWithEmailAndPassword(emailData, passwordData).addOnCompleteListener {
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
        //creating a firebase instance then acquiring a uniqueUID for differentiation of users Under One Collection Of Document ComradeUsers
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        //creating instance of fStore
        val fStoreUsers = FirebaseFirestore.getInstance();

        //creating the keys for the user data in respective mapping to the data
        val keyEmail = "Email"
        val keyPassword = "Password"
        val keyFirstName = "FirstName"
        val keyLastName = "LastName"
        val keyUniversity = "University"
        val keyPhone = "PhoneNumber"
        val keyImageUri = "ImagePath"
        //

        //creating the hashmap for the data be stored in fireStore
        val mapUserData = hashMapOf<String, String>(
            keyEmail to emailData,
            keyPassword to passwordData,
            keyFirstName to dataFirstName,
            keyLastName to lasNameData,
            keyPhone to phoneData,
            keyUniversity to spinnerDataUniversity,
            keyImageUri to uriPath.toString()
        )
        //

        //saving the user data to the server
        currentUID?.let {
            fStoreUsers.collection(ComRadeUser).document(
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
        //code begins
        val fabUserEmail= FirebaseAuth.getInstance().currentUser?.email
        val fabStorageInt = FirebaseStorage.getInstance().getReference("$ComRadeUser/$currentUID/${fabUserEmail}")
        //putting the uri path to the fabStorage
        uriPath?.let {
            fabStorageInt.putFile(it).addOnCompleteListener(this@Registration,
                OnCompleteListener { it ->
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
                        ).setAction("what then?", View.OnClickListener {
                            Snackbar.make(
                                linearLayoutParentRegistration,
                                "check your internet connection",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }).show()
                        //

                    }

                })
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
            fStoreUsers.collection(ComRadeUser).document(currentUID)
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
                        ).setAction("what then?", View.OnClickListener {
                            Snackbar.make(
                                linearLayoutParentRegistration,
                                "check your internet connection",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }).show()
                    }


                }
        }

        //code ends
    }

    private fun callFunctionShowSuccessReg() {
        //code begins
        val alertSuccess = MaterialAlertDialogBuilder(this@Registration)
        alertSuccess.setTitle("CONGRATULATIONS")
        alertSuccess.setIcon(R.drawable.ic_nike_done)
        alertSuccess.setCancelable(false)
        alertSuccess.setMessage("you have now successfully registered with us @ Comrade Market (Market CM).Login to your account using your EMAIL and PASSWORD")
        alertSuccess.setPositiveButton(
            "Login",
            DialogInterface.OnClickListener { dialogInterface, i ->
                //start activity migration to Login==Main
                val intentMainLogin = Intent(this@Registration, MainActivity::class.java)
                intentMainLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intentMainLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentMainLogin)
                finish()
                //
                //dismiss the dialog
                dialogInterface.dismiss()
                //
            })
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
                    circProfileImage.setImageURI(uriPath)
                    //
                    //snack the image a success
                    Snackbar.make(
                        linearLayoutParentRegistration,
                        "profile picture updated successfully",
                        Snackbar.LENGTH_INDEFINITE
                    ).setTextColor(
                        Color.parseColor("#ffffff")
                    ).setBackgroundTint(resources.getColor(R.color.black, theme)).setAction("Ok") {
                        //animate the profile
                        circProfileImage.startAnimation(
                            AnimationUtils.loadAnimation(
                                this@Registration, R.anim.rotate
                            )
                        )
                        //
                    }.show()
                    //

                }
                if (it.resultCode == RESULT_CANCELED) {

                    //image cancelled
                    Toast.makeText(
                        this@Registration, "the process has been cancelled!", Toast.LENGTH_LONG
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun funInitGlobals() {
        //code begins
        spinnerUniversity = findViewById(R.id.registrationSpinnerUniversity)
        btnRegistration = findViewById(R.id.btnRegister)
        editTextFirstName = findViewById(R.id.registrationFirstName)
        editTextEmail = findViewById(R.id.registrationEmail)
        editTextPhone = findViewById(R.id.registrationPhoneNumber)
        editTextLastName = findViewById(R.id.registrationLastName)
        linearLayoutParentRegistration = findViewById(R.id.parentLinearRegistration)
        circProfileImage = findViewById(R.id.circleProfileImage)
        editPassword = findViewById(R.id.registrationPassword)
        //
        array_adapter = ArrayAdapter(
            this@Registration, android.R.layout.simple_selectable_list_item, university
        )
        //sorting the elements in the spinner
        array_adapter.setNotifyOnChange(true)
        array_adapter.sort(Comparator.naturalOrder())
        spinnerUniversity.adapter = array_adapter

        //setting onItemSelectedListener on the spinner
        spinnerUniversity.onItemSelectedListener = this
        //

        //code ends


    }

    private fun funFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            spinner_returned = university[p2]

        }
        Log.d(TAG, "onItemSelected: university:$spinner_returned")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}