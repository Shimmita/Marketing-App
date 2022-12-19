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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shimitadouglas.marketcm.Registration.Companion.ComRadeUser
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

    //inflating the layout of the change password in order to be able to access its views


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
        //
        //
    }

    private fun funHandleBottomNavProducts() {
        //code begins
        botomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    Toast.makeText(this@ProductsHome, "home", Toast.LENGTH_SHORT).show()
                }
                R.id.post -> {
                    Toast.makeText(this@ProductsHome, "post", Toast.LENGTH_SHORT).show()

                }

                R.id.notification -> {
                    Toast.makeText(this@ProductsHome, "notification", Toast.LENGTH_SHORT).show()

                }

                R.id.logout -> {
                    //snack user will be logged out
                    Snackbar.make(botomNav, "you are going to log out", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(R.color.colorButtonQuiz, theme))
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
                AlertDialog.Builder(this@ProductsHome)
                    .setMessage("Congratulations email Verified")
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
                        Toast.makeText(this, "username", Toast.LENGTH_SHORT).show()
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
                        //
                        Toast.makeText(this, "profile picture", Toast.LENGTH_SHORT).show()
                        //dismiss dialog
                        dialog.dismiss()
                        //
                    }
                    2 -> {

                        //call function update Phone Number
                        functionUpdatePhoneNumber()
                        //

                        Toast.makeText(this, "phone number", Toast.LENGTH_SHORT).show()
                        //dismiss dialog
                        dialog.dismiss()
                        //
                    }

                    3 -> {
                        Toast.makeText(this, "password", Toast.LENGTH_SHORT).show()

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

    private fun functionUpdatePhoneNumber() {
        //code begins

        //code ends
    }

    private fun functionUpdateUsername() {
        //code begins

        //code ends
    }

    private fun functionUpdateProfileImage() {
        //code begins
        Snackbar.make(drawerLayout, "pick image from gallery", Snackbar.LENGTH_INDEFINITE)
            .setAction("pick") {
                //code begins
                val intentUpdatePicture = Intent()
                intentUpdatePicture.action = Intent.ACTION_PICK
                intentUpdatePicture.type = "image/*"
                galleryPickUpdate.launch(intentUpdatePicture)
                //code ends

            }.show()
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

                //open the drawer so that the user can partly see the selected uri
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
                Toast.makeText(this@ProductsHome, "process cancelled", Toast.LENGTH_LONG).show()
            }
        }

    private fun updateProfilePictureNow(uriDataUpdatePic: Uri?) {
        //code begins
        val progressDg = ProgressDialog(this@ProductsHome)
        progressDg.setCancelable(false)
        progressDg.setMessage("uploading image...")
        progressDg.create()
        progressDg.show()
        //
        val currentUID = FirebaseAuth.getInstance().uid
        val fabStorageIntUpdate =
            FirebaseStorage.getInstance().getReference("$ComRadeUser/$currentUID")
        //beginning the process of image updating
        fabStorageIntUpdate.delete().addOnCompleteListener {
            //successfully deleted the image
            if (it.isSuccessful) {
                //code begins
                progressDg.setMessage("almost done...")
                //call function complete updating
                funCompleteUpdatingProfileImage(uriDataUpdatePic, currentUID, progressDg)
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
        val fabStorageInt =
            FirebaseStorage.getInstance().getReference("$ComRadeUser/$currentUID/$fabUserEmail")
        //beginning  the process
        if (uriDataUpdatePic != null) {
            fabStorageInt.putFile(uriDataUpdatePic).addOnCompleteListener {
                //successfully uploaded the image
                if (it.isSuccessful) {
                    //code begins
                    //lets begin the process of getting download url
                    fabStorageInt.downloadUrl.addOnCompleteListener {
                        //successfully obtained download uri
                        if (it.isSuccessful) {
                            progressDg.setMessage("Congratulations...")
                            val urlDownloadUrlString = it.toString()
                            //keyToImageUri fStore
                            val keyImageUri = "ImagePath"
                            //
                            //call function to update the mapping data in the fStore
                            funUpdateStoreImageUrl(urlDownloadUrlString, keyImageUri, progressDg)
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

                    }

                    //code ends
                }
                //failed to now upload the image to the storage
                if (!it.isSuccessful) {
                    //code begins
                    //dismiss the progressD
                    progressDg.dismiss()
                    //
                    //alert the user and notify of the failure
                    AlertDialog.Builder(this@ProductsHome)
                        .setMessage(it.exception?.message)
                        .setIcon(R.drawable.ic_warning)
                        .create()
                        .show()
                    //code ends
                }
            }
        }
        //code ends

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