package com.shimitadouglas.marketcm

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shimitadouglas.marketcm.Registration.Companion.ComRadeUser
import com.shimitadouglas.marketcm.fragment.HomeFragment
import com.shimitadouglas.marketcm.fragment.NotificationFragment
import com.shimitadouglas.marketcm.fragment.PostFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random
import kotlin.system.exitProcess

class ProductsHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "ProductsHome"
    }

    //declaration of the globals
    lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var navView: NavigationView
    lateinit var botomNav: BottomNavigationView
    lateinit var viewHeader: View

    //
    //declaration of the items of the header
    lateinit var headerVerificationEmail: TextView
    lateinit var headerTitleUsername: TextView
    lateinit var headerUniversity: TextView
    lateinit var headerEmail: TextView
    lateinit var headerPhoneNumber: TextView
    lateinit var headerImage: CircleImageView
    lateinit var headerButtonUpdate: AppCompatButton
    lateinit var headerButtonVerifyEmail: AppCompatButton
    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_home)
        //function fullScreenEnabled
        funFullScreen()
        //functionInitGlobals and drawers
        funInitGlobals()
        //functionInit header
        funInitNavHeaderContent()
        //call function to handle navView Clicking and in it we link the header of it
        funHandleNavViewProducts()
        //
        //call function to handle bottom Nav Click of items
        funHandleBottomNavProducts()
        //
        //funCheckEmailVerification
        funEmailCheck()
        //
        //handling buttonUpdate and EmailVerify clicks
        funHandleButtonUpdateVerify()
        //call function to perform default fragment addition
        fragmentDefaultAdd()
        //

    }


    private fun fragmentDefaultAdd() {
        //inflate default home fragment
        val homeFragment = HomeFragment()
        val stringHomeFragment = "homeFragment"
        fragmentInit(homeFragment, stringHomeFragment)
        //
    }

    private fun fragmentInit(fragment: Fragment, tag: String) {
        //code begins
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragment, tag)
            .commitNow()
        //code ends

    }

    private fun funHandleBottomNavProducts() {
        //code begins
        botomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    //call home fragment
                    val homeFragment = HomeFragment()
                    val stringHomeFragment = "homeFragment"
                    fragmentInit(homeFragment, stringHomeFragment)
                    //

                }
                R.id.post -> {
                    //call post fragment
                    val postFragment = PostFragment()
                    val stringPostFragment = "postFragment"
                    fragmentInit(postFragment, stringPostFragment)

                    //

                }

                R.id.notification -> {
                    //call notification fragment
                    val notificationFragment = NotificationFragment()
                    val stringNotificationFragment = "notificationFragment"
                    fragmentInit(notificationFragment, stringNotificationFragment)
                    //

                }

                R.id.logout -> {
                    //snack user will be logged out
                    Snackbar.make(botomNav, "you are going to log out", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(R.color.accent_material_light, theme))
                        .setActionTextColor(Color.parseColor("#E9F90A"))
                        .setAction("sure") {
                            //code begins
                            //call functionLogout
                            funLogoutConfirmation()
                            //code ends
                        }
                        .show()
                    //
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        //code ends
    }

    private fun funLogoutConfirmation() {
        //code begins
        //creating array of string to make responsive is exit process
        val stringExit = arrayOf(
            "logging out...",
            "exiting application...",
            "requesting exit...",
            "performing exit..."
        )
        //
        val returnedNum = Random.nextInt(4)

        //creating a progress dialog to show logout
        val progD = ProgressDialog(this@ProductsHome)
        progD.setCancelable(false)
        progD.setMessage(stringExit[returnedNum])
        progD.create()
        progD.show()

        //delay of 3.5 seconds using handler
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //code begins
            //sign out user
            FirebaseAuth.getInstance().signOut()
            //

            //kill application
            finish()
            exitProcess(0)
            //
            //code ends
        }, 3500)
        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funEmailCheck() {
        //code begins
        val fabAuth = FirebaseAuth.getInstance()
        val userCurrent = fabAuth.currentUser
        //checking the verification status of the email and update theUI Accordingly
        //!verified=No Btn Update account. yes=textBannerEmailVerify,btnVerifyEmail

        if (userCurrent != null) {
            //user not verified
            if (!userCurrent.isEmailVerified) {
                //disable button Update details
                headerButtonUpdate.visibility = View.GONE

                //show alert that the email is no verified
                val alertD = AlertDialog.Builder(this@ProductsHome)
                alertD.setTitle("Email Verification")
                alertD.setCancelable(false)
                alertD.setMessage(
                    "your email address (${userCurrent.email}) needs to be verified within 2 days in order" +
                            " to avoid your account from becoming inactive." +
                            "accounts created with non verified email addresses are deemed to be corrupt accounts," +
                            " to avoid this situation, kindly verify your email within the stipulated period of time."
                )
                alertD.setIcon(R.drawable.ic_warning)
                alertD.setPositiveButton("verifyNow") { dialog, _ ->
                    //open drawer
                    drawerLayout.openDrawer(GravityCompat.START)
                    //
                    //disable the verify email address until user accepts at snackBar
                    headerButtonVerifyEmail.isEnabled = false
                    //

                    //Snack to show user how to go about
                    Snackbar.make(
                        drawerLayout,
                        "click verify email button",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setBackgroundTint(
                            resources.getColor(
                                R.color.design_dark_default_color_primary,
                                theme
                            )
                        ).setAction("Ok") {
                            //enable the button verify email
                            headerButtonVerifyEmail.isEnabled = true
                            //animate the button
                            headerButtonVerifyEmail.startAnimation(
                                AnimationUtils.loadAnimation(
                                    this@ProductsHome,
                                    R.anim.fadeout
                                )
                            )
                            //
                        }
                        .setActionTextColor(Color.parseColor("#54FA08"))
                        .show()
                    //

                    //dismiss the dialog to avoid window leaked RT Exceptions
                    dialog.dismiss()
                    //
                }
                alertD.setNegativeButton("verifyLater") { dialog, _ ->
                    //show snack not verified is the account
                    Snackbar.make(toolbar, "account not yet approved", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(
                            resources.getColor(
                                R.color.design_dark_default_color_primary,
                                theme
                            )
                        )
                        .show()
                    //

                    //dismiss the dialog
                    dialog.dismiss()
                    //
                }
                alertD.create()
                alertD.show()
                //

                //
            }


            if (userCurrent.isEmailVerified) {
                //user verified the email address
                //disable button verify email,textViewBannerEmailVerify
                headerVerificationEmail.visibility = View.GONE
                headerButtonVerifyEmail.visibility = View.GONE
                //show snack congrats email verified
                MaterialAlertDialogBuilder(this@ProductsHome)
                    .setTitle("Congratulations!")
                    .setMessage("email verification was successful.\nyour account has been approved")
                    .setIcon(R.drawable.ic_copy_right_co)
                    .setBackground(
                        resources.getDrawable(
                            R.drawable.material_congratulations,
                            theme
                        )
                    )
                    .setNeutralButton("Ok") { dialog, _ ->
                        //code begins
                        dialog.dismiss()
                        //code ends
                    }
                    .setNegativeButton("don't show again") { dialog, _ ->

                        //code begins
                        // TODO: create a variable that will be checked on launching of the application so that
                        //TODO: showing of the dialog yes or no with respect to the variable state
                        //code ends
                    }
                    .show()
                    .create()
                //
            }
            //

            //code ends


        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funHandleButtonUpdateVerify() {

        headerButtonVerifyEmail.setOnClickListener {
            //code begins
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START, true)
            //

            //Progress Dialog
            val progressD = ProgressDialog(this@ProductsHome)
            progressD.setTitle("Email Verification")
            progressD.setCancelable(false)
            progressD.setMessage("processing request...")
            progressD.show()
            progressD.create()
            //

            val fabAuth = FirebaseAuth.getInstance()
            //send the email verification link
            fabAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    //code begins
                    //email verification link  has been sent successfully
                    //dismiss the pgD
                    progressD.dismiss()
                    //

                    //creating alertFor notify
                    val alertShowHowToVerify = AlertDialog.Builder(this@ProductsHome)
                    alertShowHowToVerify.setCancelable(false)
                    alertShowHowToVerify.setMessage(
                        "email verification link has been sent to this email address(${fabAuth.currentUser!!.email})\n" +
                                "open your email inbox and verify;if this is not the case check it in the spam and do the verification"
                    )
                    alertShowHowToVerify.setPositiveButton("Ok") { dialog, _ ->
                        //dismiss the dialog and finish the application processes
                        fabAuth.signOut()
                        //
                        dialog.dismiss()
                        //
                        finish()
                        //
                        exitProcess(0)
                        //
                    }
                    alertShowHowToVerify.show()
                    alertShowHowToVerify.create()
                    //code ends
                } else if (!it.isSuccessful) {
                    //code begins

                    //dismiss the pgD
                    progressD.dismiss()
                    //

                    //failed to send an email verification link
                    AlertDialog.Builder(this@ProductsHome)
                        .setMessage(it.exception?.message)
                        .setIcon(R.drawable.ic_warning)
                        .show()
                        .create()

                    //code ends
                }
            }
            //
            //code ends
        }

        headerButtonUpdate.setOnClickListener {
            //code begins
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START, true)
            //
            //create single choice dialog and from thence update the selected option
            val arrayOptionsUpdate =
                arrayOf(
                    "update username",
                    "update profile picture",
                    "update phone number",
                    "update account password"
                )
            val itemSelected = 0
            val alertOptionsUpdate = MaterialAlertDialogBuilder(this@ProductsHome)
            alertOptionsUpdate.setTitle("Select Your Option")
            alertOptionsUpdate.background = resources.getDrawable(R.drawable.layout_backg, theme)
            alertOptionsUpdate.setIcon(R.drawable.cart)
            alertOptionsUpdate.setSingleChoiceItems(
                arrayOptionsUpdate,
                itemSelected
            ) { dialog, which ->
                //code begins
                when (which) {
                    0 -> {
                        //call function to update image
                        functionUpdateUsername()
                        //
                        //dismiss dialog
                        dialog.dismiss()
                        //
                    }
                    1 -> {
                        //call function to update username
                        functionUpdateProfileImage()
                        //dismiss dialog
                        dialog.dismiss()
                        //
                    }
                    2 -> {

                        //call function update Phone Number
                        functionUpdatePhoneNumber()
                        //dismiss dialog
                        dialog.dismiss()
                        //
                    }

                    3 -> {
                        //call function update password
                        funUpdatePassword()
                        //
                        dialog.dismiss()
                    }
                }
                //code ends

            }
            alertOptionsUpdate.create()
            alertOptionsUpdate.show()
            //

            //code ends
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funUpdatePassword() {
        //code begins

        //inflate the layout for change password to be able in accessing the views
        val viewChangePassword = layoutInflater.inflate(R.layout.layout_change_password, null)
        //
        val cbShowPassword = viewChangePassword.findViewById<CheckBox>(R.id.cbShowPassword)
        val headerEditChange: TextInputEditText =
            viewChangePassword.findViewById(R.id.edtChangePasswordHeader)
        //

        val alertChangePassword = MaterialAlertDialogBuilder(this@ProductsHome)
        alertChangePassword.setView(viewChangePassword)
        alertChangePassword.setCancelable(false)
        alertChangePassword.setTitle("Account Password")
        alertChangePassword.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alertChangePassword.setIcon(R.drawable.cart)
        alertChangePassword.setPositiveButton("change Now") { dialog, _ ->


            val textPasswordChange = headerEditChange.text.toString()

            //call function to enhance the operation of password change
            funChangePasswordStart(textPasswordChange)
            //
            //dismiss the dialog to avoid RT Exceptions
            dialog.dismiss()
            //
        }
        alertChangePassword.setNeutralButton("back") { dialog, _ ->
            //dismiss the dialog
            dialog.dismiss()
            //
        }
        alertChangePassword.create()
        alertChangePassword.show()
        //

        //setting onclick listener on cb and btn ChangePass
        cbShowPassword.setOnCheckedChangeListener { compoundButton, b ->
            //cb is checked
            if (b) {
                //code begins
                //starting text transformation od showing the password
                headerEditChange.transformationMethod = PasswordTransformationMethod.getInstance()
                //code ends
            }
            //cb is no checked
            else {
                //code begins
                //text transformation is null always when not check
                headerEditChange.transformationMethod = null
                //code ends
            }
        }
        //

        //code ends
    }

    private fun funChangePasswordStart(textPasswordChange: String) {
        //code begins
        //creating progress dialog to show changes in the event
        val progressionDialog = ProgressDialog(this@ProductsHome)
        progressionDialog.setCancelable(false)
        progressionDialog.setTitle("Password Change")
        progressionDialog.setMessage("starting...")
        progressionDialog.create()
        progressionDialog.show()
        //
        FirebaseAuth.getInstance().currentUser?.updatePassword(textPasswordChange)
            ?.addOnCompleteListener {

                //password update successful
                if (it.isSuccessful) {
                    //dismiss the progress dialog
                    progressionDialog.setMessage("updating...")
                    //call function to update the fStore keyPassword with the new password
                    funUpdatePasswordStore(textPasswordChange, progressionDialog)
                    //
                }
                //password change failure
                else if (!it.isSuccessful) {

                    //dismiss the pg and show alert of failure to change password
                    progressionDialog.dismiss()
                    //show alert
                    AlertDialog.Builder(this@ProductsHome)
                        .setMessage(it.exception?.message)
                        .setIcon(R.drawable.ic_warning)
                        .show()
                        .create()
                    //

                }

            }

        //

        //code ends
    }

    private fun funUpdatePasswordStore(
        textPasswordChange: String,
        progressionDialog: ProgressDialog
    ) {
        //code begins
        //creating a firebase instance then acquiring a uniqueUID for differentiation of users Under One Collection Of Document ComradeUsers
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        //creating instance of fStore
        val fStoreUsers = FirebaseFirestore.getInstance();

        //creating the map of keySpecific to the password
        val keyPassword = "Password"
        //
        val mapUpdatePassword = hashMapOf(keyPassword to textPasswordChange)
        //beginning the process of updating the password
        if (currentUID != null) {
            fStoreUsers.collection(ComRadeUser).document(currentUID)
                .update(mapUpdatePassword as Map<String, String>).addOnCompleteListener {
                    //password update successful
                    if (it.isSuccessful) {
                        //dismiss the progress dialog
                        progressionDialog.dismiss()
                        //alert the user with new password
                        //show alert
                        AlertDialog.Builder(this@ProductsHome)
                            .setTitle("Congratulations!")
                            .setMessage(
                                "password update was successful." +
                                        "\nYour new login password is now ($textPasswordChange)"
                            )
                            .setIcon(R.drawable.ic_nike_done)
                            .setCancelable(false)
                            .setPositiveButton("fine") { dialog, _ ->
                                //dimiss the dialog
                                dialog.dismiss()
                                //
                            }
                            .show()
                            .create()
                        //
                        //
                    }

                    //password update failure in the fStore
                    else if (!it.isSuccessful) {

                        //dismiss the pg
                        progressionDialog.dismiss()
                        //show alert
                        //show alert
                        AlertDialog.Builder(this@ProductsHome)
                            .setMessage(it.exception?.message)
                            .setIcon(R.drawable.ic_warning)
                            .show()
                            .create()
                        //

                        //
                    }
                }
        }
        //
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun functionUpdatePhoneNumber() {
        //code begins
        //init of the view that will help access of the variable phone number entered
        val viewUpdatePhoneNumber = layoutInflater.inflate(R.layout.update_phone_number, null)
        //
        //animate the view
        viewUpdatePhoneNumber.startAnimation(
            AnimationUtils.loadAnimation(
                this@ProductsHome,
                R.anim.rotate
            )
        )
        val phoneNumberEntered =
            viewUpdatePhoneNumber.findViewById<TextInputEditText>(R.id.edtUpdatePhoneNumber)
        //
        val alertUpdatePhone = MaterialAlertDialogBuilder(this@ProductsHome)
        alertUpdatePhone.setCancelable(false)
        alertUpdatePhone.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alertUpdatePhone.setView(viewUpdatePhoneNumber)
        alertUpdatePhone.setPositiveButton("update") { dialog, _ ->

            val textPhoneEntered = phoneNumberEntered.text.toString()

            //call function to update the phone number on a thread
            callFunctionUpdatePhoneNumber(textPhoneEntered)

            //dismiss
            dialog.dismiss()
            //
        }
        alertUpdatePhone.setNeutralButton("back") { dialog, _ ->
            //dismiss  dialog
            dialog.dismiss()
            //
        }
        alertUpdatePhone.create()
        alertUpdatePhone.show()

        //code ends
    }

    private fun callFunctionUpdatePhoneNumber(textPhoneEntered: String) {
        //code begins
        //check the legibility of the data entered
        if (TextUtils.isEmpty(textPhoneEntered)) {
            Toast.makeText(this@ProductsHome, "missing field unacceptable", Toast.LENGTH_SHORT)
                .show()
        } else if (textPhoneEntered.length < 10) {
            Toast.makeText(this@ProductsHome, "number incomplete !", Toast.LENGTH_SHORT).show()
        } else if (textPhoneEntered.length > 10) {
            Toast.makeText(this@ProductsHome, "number too long !", Toast.LENGTH_SHORT).show()

        } else {
            functionUpdatePhoneNow(textPhoneEntered)
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun functionUpdatePhoneNow(textPhoneEntered: String) {
        //code begins
        //create pg
        val progD = ProgressDialog(this@ProductsHome)
        progD.setTitle("Phone Number Update")
        progD.setMessage("updating...")
        progD.setCancelable(false)
        progD.create()
        progD.show()

        val keyPhone = "PhoneNumber"

        //creating the  map to update the phone number
        val mapUpdateNumber = hashMapOf(keyPhone to textPhoneEntered)

        //begin init of the UID and then the fStore for Storage update
        val userIDCurrent = FirebaseAuth.getInstance().currentUser?.uid
        //
        val fStore = userIDCurrent?.let {
            FirebaseFirestore.getInstance().collection(ComRadeUser).document(
                it
            ).update(mapUpdateNumber as Map<String, Any>).addOnCompleteListener {
                //code begins
                if (it.isSuccessful) {
                    //dismiss the pg
                    progD.dismiss()
                    //

                    //alert the user with congrats

                    val alertSuccess = MaterialAlertDialogBuilder(this@ProductsHome)
                    alertSuccess.setCancelable(false)
                    alertSuccess.background =
                        resources.getDrawable(R.drawable.general_alert_dg, theme)
                    alertSuccess.setMessage("Congratulations phone number updated successfully to ($textPhoneEntered).\nrestart the application for changes to effect")
                    alertSuccess.setPositiveButton("restart") { dialog, _ ->

                        //dismiss the dialog
                        dialog.dismiss()
                        //
                        val stringExit = arrayOf(
                            "logging out...",
                            "exiting application...",
                            "requesting exit...",
                            "performing exit..."
                        )
                        //
                        val returnedNum = Random.nextInt(4)
                        //show the progress dialog
                        progD.setMessage(stringExit[returnedNum])
                        progD.show()
                        //
                        //delay for 3 seconds and exit
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {

                            //sign out the user before exit
                            FirebaseAuth.getInstance().signOut()
                            //

                            finish()
                            exitProcess(0)

                        }, 3500)
                        //
                    }
                    alertSuccess.create()
                    alertSuccess.show()
                    //

                } else if (!it.isSuccessful) {
                    //dismiss the alert
                    progD.dismiss()
                    //

                    //alert the user of error
                    //show alert
                    AlertDialog.Builder(this@ProductsHome)
                        .setMessage(it.exception?.message)
                        .setIcon(R.drawable.ic_warning)
                        .show()
                        .create()
                    //
                    //

                }
                //code ends
            }
        }

        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun functionUpdateUsername() {
        //code begins
        //init of the view to facilitate access to the view Views
        val viewUpdateUsername = layoutInflater.inflate(R.layout.update_username, null)
        //animate the view
        viewUpdateUsername.startAnimation(
            AnimationUtils.loadAnimation(
                this@ProductsHome,
                R.anim.rotate
            )
        )
        //init of first name $ lastname wit reference to the view
        val firstNameEnteredView =
            viewUpdateUsername.findViewById<TextInputEditText>(R.id.edtUpdateFirstName)
        val lastNameEnteredView =
            viewUpdateUsername.findViewById<TextInputEditText>(R.id.edtUpdateLastName)
        //
        //fetching the values from the entered views
        //

        //defining a  dialog that will facilitate setting up of the layout Update Username
        val alertDialogUpdateUserName = MaterialAlertDialogBuilder(this@ProductsHome)
        alertDialogUpdateUserName.setView(viewUpdateUsername)
        alertDialogUpdateUserName.setCancelable(false)
        alertDialogUpdateUserName.background =
            resources.getDrawable(R.drawable.general_alert_dg, theme)

        alertDialogUpdateUserName.setPositiveButton("Update") { dialog, _ ->
            val textFirstName = firstNameEnteredView.text.toString()
            val textLastName = lastNameEnteredView.text.toString()

            //checking the legitimacy of the data entered
            if (TextUtils.isEmpty(textFirstName) or TextUtils.isEmpty(textLastName)) {
                //toast empty fields not allowed
                Toast.makeText(
                    this@ProductsHome,
                    "missing field(s) unacceptable",
                    Toast.LENGTH_SHORT
                ).show()
                //
                dialog.dismiss()
                //
            } else {
                //begin update of username
                //launch a coroutine to start the process
                startUpdateOfUserName(textFirstName, textLastName)
                //
                dialog.dismiss()
            }
            //
        }
        alertDialogUpdateUserName.setNeutralButton("back") { dialog, _ ->

            //dismiss the dialog to avoid RT Errors
            dialog.dismiss()
            //
        }
        alertDialogUpdateUserName.show()


        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun startUpdateOfUserName(
        firstName: String,
        lastName: String
    ) {
        //code begins
        val progressDg = ProgressDialog(this@ProductsHome)
        progressDg.setCancelable(false)
        progressDg.setTitle("Username Update")
        progressDg.setMessage("starting...")
        progressDg.create()
        progressDg.show()
        //
        val keyFirstName = "FirstName"
        val keyLastName = "LastName"
        //

        val mapUserUpdateUserName = hashMapOf(keyFirstName to firstName, keyLastName to lastName)

        //init of UID
        val userIDLogged = FirebaseAuth.getInstance().currentUser?.uid

        //init of fStore and begin update of username
        val fStore = FirebaseFirestore.getInstance().collection(ComRadeUser)
        //
        //begin update of the user

        if (userIDLogged != null) {
            fStore.document(userIDLogged).update(mapUserUpdateUserName as Map<String, String>)
                .addOnCompleteListener {

                    //successfully updated the username
                    if (it.isSuccessful) {

                        //dismiss the pg
                        progressDg.dismiss()
                        //
                        val alertSuccess = MaterialAlertDialogBuilder(this@ProductsHome)
                        alertSuccess.setCancelable(false)
                        alertSuccess.background =
                            resources.getDrawable(R.drawable.general_alert_dg, theme)
                        alertSuccess.setMessage("Congratulations username updated successfully to ($firstName $lastName)\nrestart the application for changes to effect")
                        alertSuccess.setPositiveButton("restart") { dialog, _ ->

                            //dismiss the dialog
                            dialog.dismiss()
                            //
                            val stringExit = arrayOf(
                                "logging out...",
                                "exiting application...",
                                "requesting exit...",
                                "performing exit..."
                            )
                            //
                            val returnedNum = Random.nextInt(4)
                            //show the progress dialog
                            progressDg.setMessage(stringExit[returnedNum])
                            progressDg.show()
                            //
                            //delay for 3 seconds and exit
                            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                                //sign out the user before exit
                                FirebaseAuth.getInstance().signOut()
                                //
                                finish()
                                exitProcess(0)

                            }, 3500)
                            //
                        }
                        alertSuccess.create()
                        alertSuccess.show()

                    }
                    //failed to update the username
                    else if (!it.isSuccessful) {
                        //dismiss the pg
                        progressDg.dismiss()
                        //

                        //alert the user of the error
                        AlertDialog.Builder(this@ProductsHome)
                            .setMessage(it.exception?.message)
                            .setIcon(R.drawable.ic_warning)
                            .create()
                            .show()
                        //

                    }

                }
        }


        //code ends
    }

    private fun functionUpdateProfileImage() {
        //code begins
        Snackbar.make(
            drawerLayout,
            "select your preferred image from gallery",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("pick") {
                //code begins
                val intentUpdatePicture = Intent()
                intentUpdatePicture.action = Intent.ACTION_PICK
                intentUpdatePicture.type = "image/*"
                galleryPickUpdate.launch(intentUpdatePicture)
                //code ends

            }.setBackgroundTint(resources.getColor(R.color.accent_material_light, theme)).show()
        //code ends
    }

    private fun funInitNavHeaderContent() {
        //code begins
        //inflating the header on the navView
        viewHeader = navView.inflateHeaderView(R.layout.header_products)
        //
        headerImage = viewHeader.findViewById(R.id.headerImage)
        headerVerificationEmail = viewHeader.findViewById(R.id.emailVerifyHeader)
        headerTitleUsername = viewHeader.findViewById(R.id.usernameHeader)
        headerUniversity = viewHeader.findViewById(R.id.universityHeader)
        headerEmail = viewHeader.findViewById(R.id.emailHeader)
        headerPhoneNumber = viewHeader.findViewById(R.id.phoneNumberHeader)
        headerButtonVerifyEmail = viewHeader.findViewById(R.id.btnCompatHeaderVerify)
        headerButtonUpdate = viewHeader.findViewById(R.id.btnCompatHeaderUpdate)
        //
        //adding marque effect on the textVerify Email
        headerVerificationEmail.setSingleLine()
        headerVerificationEmail.ellipsize = TextUtils.TruncateAt.MARQUEE
        headerVerificationEmail.marqueeRepeatLimit = -1
        headerVerificationEmail.isSelected = true
        //

        //code ends

    }

    private fun funHandleNavViewProducts() {
        //code begins
        //setting listener on the navigation View
        navView.setNavigationItemSelectedListener(this@ProductsHome)
        //code ends

    }

    private fun funInitGlobals() {
        //code begins
        toolbar = findViewById(R.id.toolbarProducts)
        drawerLayout = findViewById(R.id.drawerProducts)
        navView = findViewById(R.id.navProducts)
        botomNav = findViewById(R.id.bottomNavProducts)

        drawerToggle = ActionBarDrawerToggle(
            this@ProductsHome,
            drawerLayout,
            toolbar,
            R.string.drawerOpen,
            R.string.drawerClose
        )

        //setting support for toolbar to enable navView Be attached Toolbar
        setSupportActionBar(toolbar)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        toolbar.setNavigationOnClickListener {
            //creating a layoutAnim controller for the drawerLayout
            val controllerAnimDrawerLayout = LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    this@ProductsHome,
                    R.anim.abc_slide_in_top
                )
            )
            controllerAnimDrawerLayout.delay = 1.0f
            drawerLayout.layoutAnimation = controllerAnimDrawerLayout
            //

            //control the drawer opening on the click of the navigation Icon
            if (!drawerLayout.isOpen) {
                //animate the drawer before opening
                drawerLayout.startLayoutAnimation()
                //
                drawerLayout.openDrawer(GravityCompat.START, true)
            }
            //
        }
        //


        //code ends

    }

    private fun funFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.smsDev -> {
                Toast.makeText(this@ProductsHome, "sms developer", Toast.LENGTH_LONG).show()
            }
            R.id.callDev -> {
                Toast.makeText(this@ProductsHome, "call developer", Toast.LENGTH_LONG).show()

            }
            R.id.aboutMarketCM -> {
                Toast.makeText(this@ProductsHome, "about market cm", Toast.LENGTH_LONG).show()

            }
            R.id.aboutPrivacy -> {
                Toast.makeText(this@ProductsHome, "about privacy", Toast.LENGTH_LONG).show()

            }

        }

        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private val galleryPickUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {

            //activity image pick successful
            if (it.resultCode == RESULT_OK) {

                //open the drawer so that the user can partly see the selected image
                drawerLayout.openDrawer(GravityCompat.START, true)
                //
                val uriDataUpdatePic = it.data?.data
                //setting the image uri on the profile icon
                headerImage.setImageURI(uriDataUpdatePic)
                //

                //animate the image
                headerImage.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@ProductsHome,
                        R.anim.rotate
                    )
                )
                //

                //alert the user of beginning the process of updating the profile picture
                //after delay 3 seconds
                val thread = Thread(Runnable {
                    headerImage.postDelayed(Runnable {

                        val alertUserPictureUpdating = MaterialAlertDialogBuilder(this)
                        alertUserPictureUpdating.setCancelable(false)
                        alertUserPictureUpdating.setIcon(R.drawable.cart)
                        alertUserPictureUpdating.background =
                            resources.getDrawable(R.drawable.general_alert_dg, theme)
                        alertUserPictureUpdating.setTitle("updating profile picture")
                        alertUserPictureUpdating.setMessage("your selected profile picture is going to be updated")
                        alertUserPictureUpdating.setPositiveButton("update") { dialog, _ ->
                            //
                            updateProfilePictureNow(uriDataUpdatePic)
                            //
                            //dialog dismiss
                            dialog.dismiss()
                            //
                        }
                        alertUserPictureUpdating.create()
                        alertUserPictureUpdating.show()

                    }, 4300);
                })
                thread.start()
                //

            }

            //activity image pick failed
            if (it.resultCode == RESULT_CANCELED) {
                //code begins
                //show snackBar you did not pick an image from the gallery
                Snackbar.make(
                    botomNav,
                    "yo did not pick an image from gallery!",
                    Snackbar.LENGTH_LONG
                ).setTextColor(Color.parseColor("#EEBD09")).show()
                //

                //code ends
            }
        }

    private fun updateProfilePictureNow(uriDataUpdatePic: Uri?) {
        //todo: there is a problem in the functions of updating the profile picture image.need resolve
        //code begins
        val progressDg = ProgressDialog(this@ProductsHome)
        progressDg.setCancelable(false)
        progressDg.setMessage("uploading image...")
        progressDg.create()
        progressDg.show()
        //"$ComRadeUser/$currentUID/${fabUserEmail}/uri
        val currentUID = FirebaseAuth.getInstance().uid
        val emailOfUser = FirebaseAuth.getInstance().currentUser?.email
        val fabStorageIntUpdate =
            currentUID?.let {
                if (emailOfUser != null) {
                    if (uriDataUpdatePic != null) {
                        FirebaseStorage.getInstance().reference.child(ComRadeUser).child(it)
                            .child(emailOfUser).delete().addOnCompleteListener {


                                //successfully deleted the image
                                if (it.isSuccessful) {
                                    //code begins
                                    progressDg.setMessage("finishing...")
                                    //call function complete updating
                                    funCompleteUpdatingProfileImage(
                                        uriDataUpdatePic,
                                        currentUID,
                                        progressDg
                                    )
                                    //
                                    //code begins
                                }
                                //failed to delete the image
                                if (!it.isSuccessful) {
                                    //code begins
                                    //dismiss the pg
                                    progressDg.dismiss()
                                    //

                                    AlertDialog.Builder(this@ProductsHome)
                                        .setMessage(it.exception?.message)
                                        .setIcon(R.drawable.ic_warning)
                                        .create()
                                        .show()

                                    //code ends

                                }

                            }
                    }
                }
            }
        //
        //code ends
    }

    private fun funCompleteUpdatingProfileImage(
        uriDataUpdatePic: Uri?,
        currentUID: String?,
        progressDg: ProgressDialog
    ) {
        //code begins
        val fabUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val fabStorageInt = FirebaseStorage.getInstance().reference

        //initiate the update of image at fab storage with the selected image from gallery
        if (currentUID != null) {
            if (uriDataUpdatePic != null) {
                if (fabUserEmail != null) {
                    fabStorageInt.child(ComRadeUser).child(currentUID).child(fabUserEmail)
                        .putFile(uriDataUpdatePic).addOnCompleteListener {
                            //code begins
                            //successfully obtained download uri
                            if (it.isSuccessful) {
                                //keyToImageUri fStore
                                val keyImageUri = "ImagePath"
                                //
                                //get download URI from fabStorage
                                fabStorageInt.downloadUrl.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        //download uri was fetched successfully hence get the download Uri to string
                                        //updating the pg to
                                        progressDg.setMessage("Congratulations...")
                                        //obtaining the uri from
                                        var urlDownloadUrlString = it.toString()
                                        //
                                        //calling a function that will allow now continuation to the fStore for Storing new Update imageLink
                                        //call function to update the mapping data in the fStore
                                        funUpdateStoreImageUrl(
                                            urlDownloadUrlString,
                                            keyImageUri,
                                            progressDg
                                        )
                                        //
                                        //

                                    } else if (!it.isSuccessful) {
                                        //dismiss the progress dialog
                                        progressDg.dismiss()
                                        //
                                        //alert the error to the user
                                        AlertDialog.Builder(this@ProductsHome)
                                            .setMessage(it.exception?.message)
                                            .setIcon(R.drawable.ic_warning)
                                            .create()
                                            .show()
                                        //
                                        //

                                    }

                                }
                                //
                            }

                            if (!it.isSuccessful) {
                                //dismiss the pg
                                progressDg.dismiss()
                                //
                                //alert user of url failure
                                AlertDialog.Builder(this@ProductsHome)
                                    .setMessage(it.exception?.message)
                                    .setIcon(R.drawable.ic_warning)
                                    .create()
                                    .show()
                                //
                            }

                            //code ends

                        }
                }
            }
        }
        //
    }


    private fun funUpdateStoreImageUrl(
        urlDownloadUrlString: String,
        keyImageUri: String,
        progressDg: ProgressDialog
    ) {
        //code begins2
        val mapData = hashMapOf(keyImageUri to urlDownloadUrlString)
        //starting the process of image update fStore
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        val fStoreUsers = FirebaseFirestore.getInstance();

        if (currentUID != null) {
            fStoreUsers.collection(ComRadeUser).document(currentUID)
                .update(mapData as Map<String, String>).addOnCompleteListener {

                    if (it.isSuccessful) {
                        //dismiss the progress dialog
                        progressDg.dismiss()
                        //
                        //code begins
                        //todo:implement a function that will be always called when a successfully update is done in order to avoid exit of the app
                        //
                        Snackbar.make(drawerLayout, "app needs restart", Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(Color.parseColor("#5AFF0A"))
                            .setAction("Restart") {
                                //code begins
                                //sign out the user
                                FirebaseAuth.getInstance().signOut()
                                //
                                finish()
                                exitProcess(0)
                                //code ends
                            }
                            .show()

                        //code ends

                    } else if (!it.isSuccessful) {
                        //progress dialog dismiss
                        progressDg.dismiss()
                        //
                        //code begins
                        //alert the user and notify of the failure
                        AlertDialog.Builder(this@ProductsHome)
                            .setMessage(it.exception?.message)
                            .setIcon(R.drawable.ic_warning)
                            .create()
                            .show()
                        //code ends
                        //code ends

                    }

                }
        }
        //code ends

    }
}