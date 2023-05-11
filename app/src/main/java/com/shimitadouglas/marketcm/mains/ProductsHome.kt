package com.shimitadouglas.marketcm.mains

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shimitadouglas.marketcm.Networking.NetworkMonitor
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.admin_check.AdministrationVerify
import com.shimitadouglas.marketcm.app_controller.DataClassAppController
import com.shimitadouglas.marketcm.control_panel_admin.Administration
import com.shimitadouglas.marketcm.fragmentProducts.HomeFragment
import com.shimitadouglas.marketcm.fragmentProducts.NotificationFragment
import com.shimitadouglas.marketcm.fragmentProducts.PostFragment
import com.shimitadouglas.marketcm.modal_data_admin_fetch.DataAdmin
import com.shimitadouglas.marketcm.modal_data_posts.DataClassProductsData
import com.shimitadouglas.marketcm.modal_data_profile.DataProfile
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts
import com.shimitadouglas.marketcm.modal_sheets.ModalPrivacyMarket
import com.shimitadouglas.marketcm.notifications.BigTextNotificationEmail
import com.shimitadouglas.marketcm.notifications.BigTextNotificationGen
import com.shimitadouglas.marketcm.notifications.NormalNotification
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar
import java.util.HashMap

class ProductsHome : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object {
        //Declaration Globals
        const val CollectionCounterfeit = "Counterfeit Reports"

        //shared prefs for storing the states of some data to avoid constants reloads
        var sharedPreferenceName: String = "MarketCmSharedPreference"

        //
        private const val TAG = "ProductsHome"

        //
        const val CollectionAppController = "AppController"
        const val documentAppController = "market_cm"
        const val CollectionAdmin = "Admin"
        const val documentAdmin = "admin"
        //
    }


    //declaration of the globals
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var viewHeader: View

    //declaration of the items of the header
    private lateinit var headerVerificationEmail: TextView
    private lateinit var headerTitleUsername: TextView
    private lateinit var headerUniversity: TextView
    private lateinit var headerEmail: TextView
    private lateinit var headerPhoneNumber: TextView
    private lateinit var headerImage: CircleImageView
    private lateinit var headerDateRegistration: TextView
    private lateinit var headerButtonUpdate: AppCompatButton
    private lateinit var headerButtonVerifyEmail: AppCompatButton
    //
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_home)

        //functionInitGlobals and drawers
        funInitGlobals()
        //call function to handle navView Clicking and in it we link the header of it
        funHandleNavViewProducts()
        //functionInit header
        funInitNavHeaderContent()
        //call function to handle bottom Nav Click of items
        funHandleBottomNavProducts()
        //funCheckEmailVerification
        funEmailCheck()
        //fun load the data from the fStore
        funFetchProfileDataBackend()

        //fun check admin shield activation
        funCheckAdminShieldActivationMenuItem()

        //fun control the running of the app
        funControlApp()
        //setting listeners on the buttonHeaders update email and verify email
        headerButtonVerifyEmail.setOnClickListener {
            //fun to verify email
            funVerifyEmail()
            //
        }

        //setting listener on the buttonHeader update email
        headerButtonUpdate.setOnClickListener {
            //funUpdate email
            funUpdateAccount()
            //
        }


        //fun to manage the number of notification counts for the bottom nav fragment notification
        funManageNumberNotification()
        //code ends
    }

    private fun funManageNumberNotification() {
        //code begins
        //fetch notification from the repo (UID)
        val uniqueUIDCurrentlyLoggedIn = FirebaseAuth.getInstance().currentUser?.uid
        val storeFetchNotifications = FirebaseFirestore.getInstance()
        if (uniqueUIDCurrentlyLoggedIn != null) {
            storeFetchNotifications.collection(uniqueUIDCurrentlyLoggedIn).get()
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        //code
                        val documents = it.result
                        var numberOfNotifications = 0
                        //from the documents exclude doc in a loop that does not contain UID since thay be pointing to
                        //the user private repo posts rather than the notifications whose doc value is only pure timer in millis
                        for (doc in documents) {
                            if (!doc.id.contains(uniqueUIDCurrentlyLoggedIn)) {
                                //increment their value since they are docs not containing UID thus are notifications
                                numberOfNotifications++

                                //using the create notification fun to pass the size of the notifications
                                val idIconNotification = R.id.notification
                                createNotificationAlertBadges(
                                    idIconNotification, numberOfNotifications
                                )
                            }
                        }

                        //code ends
                    } else if (!it.isSuccessful) {
                        //code begins
                        funToastyFail("detected an error!")
                        return@addOnCompleteListener
                        //code ends
                    }
                }

        } else {
            funToastyFail("something went wrong")
        }
        //code begins
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funUpdateAccount() {
        //code begins
        //close drawer
        drawerLayout.closeDrawer(GravityCompat.START, true)
        //
        //create single choice dialog and from thence update the selected option
        val arrayOptionsUpdate = arrayOf(
            "update username",
            "update profile picture",
            "update phone number",
            "update account password"
        )
        val itemSelected = 4
        val alertOptionsUpdate = MaterialAlertDialogBuilder(this@ProductsHome)
        alertOptionsUpdate.setTitle("select option")
        alertOptionsUpdate.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alertOptionsUpdate.setIcon(R.drawable.cart)
        alertOptionsUpdate.setSingleChoiceItems(
            arrayOptionsUpdate, itemSelected
        ) { dialog, which ->
            //code begins
            when (which) {
                0 -> {
                    //call function to update username
                    functionUpdateUsername()
                    //
                    //dismiss dialog
                    dialog.dismiss()
                    //
                }
                1 -> {
                    //call function to update image
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
        //code ends
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funVerifyEmail() {
        //code begins
        //close drawer
        drawerLayout.closeDrawer(GravityCompat.START, true)
        //Progress Dialog
        val progressD = ProgressDialog(this@ProductsHome)
        progressD.setTitle("email verification")
        progressD.setCancelable(false)
        progressD.setMessage("processing ")
        progressD.show()
        progressD.create()

        val fabAuth = FirebaseAuth.getInstance()
        //send the email verification link
        fabAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                //dismiss the pgD
                progressD.dismiss()
                //creating alertFor notify email verification sent
                val alertShowHowToVerify = MaterialAlertDialogBuilder(this@ProductsHome)
                alertShowHowToVerify.setCancelable(false)
                alertShowHowToVerify.setIcon(R.drawable.ic_info)
                alertShowHowToVerify.background =
                    resources.getDrawable(R.drawable.general_alert_dg, theme)
                alertShowHowToVerify.setTitle("Email Verification")
                alertShowHowToVerify.setMessage(
                    "email verification link has been sent to (${fabAuth.currentUser!!.email})\n" + "\nopen your email inbox and click the email link to verify\n" + "\nIf the email verification link is not present in the email inbox folder then do check it in the spam folder and do the verification"
                )
                alertShowHowToVerify.setPositiveButton("Ok") { dialog, _ ->
                    //show notification of email verification
                    NormalNotification(
                        this@ProductsHome,
                        "Email Verification",
                        "verification link sent successfully",
                        R.drawable.cart
                    ).funCreateNotification()
                    //sign out the user
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        FirebaseAuth.getInstance().signOut()
                        dialog.dismiss()
                        finish()
                    }
                }
                alertShowHowToVerify.create()
                alertShowHowToVerify.show()
                //code ends
            } else if (!it.isSuccessful) {
                //code begins
                //dismiss the pgD
                progressD.dismiss()
                //failed to send an email verification link
                AlertDialog.Builder(this@ProductsHome).setMessage(it.exception?.message)
                    .setIcon(R.drawable.ic_warning).show().create()
                //code ends
            }
        }
        //code ends
    }

    @SuppressLint("InflateParams")
    private fun funControlApp() {
        //log
        Log.d(TAG, "funControlApp: begins")
        //code begins
        //progressD for the server
        val viewServer = LayoutInflater.from(this@ProductsHome)
            .inflate(R.layout.general_progress_dialog_view, null, false)
        val progressServerStatus = ProgressDialog(this@ProductsHome)
        progressServerStatus.setIcon(R.drawable.ic_download)
        progressServerStatus.setCancelable(false)
        progressServerStatus.setView(viewServer)
        progressServerStatus.setTitle("server status")
        progressServerStatus.setMessage("checking")
        progressServerStatus.create()
        progressServerStatus.show()

        //fetch the control status from the server
        val storeServerControl = FirebaseFirestore.getInstance()
        storeServerControl.collection(CollectionAppController).document(documentAppController).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //log
                    Log.d(TAG, "funControlApp: fetched app control data successfully")
                    //

                    //dismiss the progressDServer and continue to the home products fragment
                    progressServerStatus.dismiss()
                    //

                    val classDataFilter: DataClassAppController? =
                        it.result.toObject(DataClassAppController::class.java)
                    if (classDataFilter != null) {
                        //log data
                        Log.d(
                            TAG, "funControlApp: dataClassAppController its class filter not null"
                        )

                        val runA = classDataFilter.runA
                        val runB = classDataFilter.runB

                        //if the two runs are equal then the app should proceed else fail the app instantly and show the alert dialog
                        if (runA.equals(runB, true)) {

                            //log data
                            Log.d(TAG, "funControlApp: AppController data is yes continue app")
                            //dismiss the progressDialog
                            progressServerStatus.dismiss()
                            //
                            funContinueAppNoInterruptionFromServer()
                            //code ends
                        } else if (runA != runB) {

                            //log data
                            Log.d(
                                TAG,
                                "funControlApp: AppControllerData is not yes. checking user role...."
                            )

                            //fun check user is admin or not if not show maintenance alert else continue safely
                            funCheckUserRole(progressServerStatus)
                            //
                        }
                    } else {
                        //log data
                        Log.d(TAG, "funControlApp: DataClassAppController is null exiting...")
                        //
                        //dismiss the progressD
                        progressServerStatus.dismiss()
                        //
                        funToastyFail("something went wrong!")
                        // sign out the current user
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            FirebaseAuth.getInstance().signOut()
                            //finish the application
                            finish()
                            //
                        }
                        //finish
                        finish()
                        //

                    }
                    //
                } else if (!it.isSuccessful) {
                    //log data
                    Log.d(TAG, "funControlApp: error fetching AppControllerData from the server")
                    //

                    //dismiss the progressD there is an exception from the server
                    progressServerStatus.dismiss()
                    //alert user of an error and exit
                    funAlertFailureServer(it)
                    //
                }
            }
        //code ends
    }

    private fun funContinueAppNoInterruptionFromServer() {
        //code begins
        //call function to perform default fragment addition
        fragmentDefaultAdd()
        //code ends
    }

    private fun funCheckUserRole(progressServerStatus: ProgressDialog) {

        //log data
        Log.d(TAG, "funCheckUserRole: begins")
        //

        //fetch the data from the storeAdmin and check user role
        val storeAdmin = FirebaseFirestore.getInstance()
        storeAdmin.collection(CollectionAdmin).document(documentAdmin).get().addOnCompleteListener {

            if (it.isSuccessful) {
                //log data
                Log.d(TAG, "funCheckUserRole:Successfully Fetched the Admin data")
                //

                //getting the admin data
                val classFilter: DataAdmin? = it.result.toObject(DataAdmin::class.java)
                //
                if (classFilter != null) {

                    //log data
                    Log.d(TAG, "funCheckUserRole: admin Clas filter data is not null")
                    //

                    val adminEmail = classFilter.email
                    val adminEmailB = classFilter.emailB
                    val adminPassCode = classFilter.password
                    val adminPhone = classFilter.phone

                    //call the fun to necessitate the equality current user with admin data
                    funFetchCurrentUserDataForComparison(
                        adminEmail, adminEmailB, adminPhone, adminPassCode, progressServerStatus
                    )
                    //

                } else {

                    //log data
                    Log.d(TAG, "funCheckUserRole: class filter data admin is null exiting...")
                    //dismiss the progress Dialog
                    progressServerStatus.dismiss()
                    //toast something went wrong
                    funToastyFail("something went wrong!")
                    //

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        //sign out the user
                        FirebaseAuth.getInstance().signOut()
                        //
                        //finish the app instantly
                        finish()
                        //
                    } else {
                        //finish
                        finish()
                        //
                    }


                }
            } else if (!it.isSuccessful) {

                //log data
                Log.d(TAG, "funCheckUserRole: was problem fetching admin data from the server")
                //dismiss the progressD
                progressServerStatus.dismiss()
                //show alert the failure from the server
                funAlertFailureServer(it)
                //
            }

        }
        //
    }

    private fun funFetchCurrentUserDataForComparison(
        adminEmail: String?,
        adminEmailB: String?,
        adminPhone: String?,
        adminPassCode: String?,
        progressServerStatus: ProgressDialog
    ) {

        //log data
        Log.d(TAG, "funFetchCurrentUserDataForComparison: begin")
        //code begins
        val uniqueID = FirebaseAuth.getInstance().currentUser?.uid
        //fetch user data from the cloud
        val storeUsersData = FirebaseFirestore.getInstance()
        if (uniqueID != null) {
            storeUsersData.collection(Registration.ComradeUser).document(uniqueID).get().addOnCompleteListener {

                if (it.isSuccessful) {
                    //log data
                    Log.d(
                        TAG, "funFetchCurrentUserDataForComparison:fetching the user data was yes"
                    )
                    //

                    val classFilter: DataProfile? = it.result.toObject(DataProfile::class.java)
                    if (classFilter != null) {
                        //log data
                        Log.d(TAG, "funFetchCurrentUserDataForComparison: user data is not null")
                        //

                        val passcodeUser = classFilter.Password
                        val phoneUser = classFilter.PhoneNumber
                        val emailUser = classFilter.Email
                        if (passcodeUser == adminPassCode && phoneUser == adminPhone && emailUser == adminEmail) {
                            //log data
                            Log.d(
                                TAG, "funFetchCurrentUserDataForComparison: user is admin match A"
                            )
                            //

                            //continue the app user is admin despite app failure admin accesses it freely
                            //call function to perform default fragment addition
                            fragmentDefaultAdd()
                            //

                        } else if (passcodeUser == adminPassCode && phoneUser == adminPhone && emailUser == adminEmailB) {
                            //log data
                            Log.d(
                                TAG, "funFetchCurrentUserDataForComparison: user is admin  match B"
                            )
                            //continue the app user is admin despite app failure admin accesses it freely
                            //call function to perform default fragment addition
                            fragmentDefaultAdd()
                            //code ends
                        } else {
                            //log data
                            Log.d(
                                TAG, "funFetchCurrentUserDataForComparison: user no admin stop app"
                            )
                            //dismiss progressD
                            progressServerStatus.dismiss()
                            //user is ordinary user show alert app maintenance normal users have less privileges
                            funAlertAppUnderMaintenance()
                        }
                    } else {
                        //log data
                        Log.d(
                            TAG,
                            "funFetchCurrentUserDataForComparison: user data class filter is null exiting..."
                        )
                        //

                        //dismiss the progress dialog
                        progressServerStatus.dismiss()
                        //
                        funToastyFail("something went wrong!")
                        //sign out and exit the application
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            FirebaseAuth.getInstance().signOut()
                            finish()
                        } else {
                            finish()
                        }
                        //
                    }

                } else if (it.isSuccessful) {
                    //log data
                    Log.d(
                        TAG,
                        "funFetchCurrentUserDataForComparison:was a problem fetching user data from the server"
                    )
                    //

                    //dismiss the progressD
                    progressServerStatus.dismiss()

                    //alert user of the error
                    funAlertFailureServer(it)
                    //
                }
            }
        }
        //code ends
    }

    private fun funAlertAppUnderMaintenance() {
        //obtain the name of the user from the shared prefs
        val sharedPref =
            this@ProductsHome.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val firstName = sharedPref.getString("firstname", "")
        val lastName = sharedPref.getString("lastname", "")

        if (firstName != null) {
            if (lastName != null) {
                if (firstName.isNotEmpty() && lastName.isNotEmpty() && firstName.trim() != "" && lastName.trim() != "") {
                    val alertAppUnderMaintenance = MaterialAlertDialogBuilder(this@ProductsHome)
                    alertAppUnderMaintenance.setCancelable(false)
                    alertAppUnderMaintenance.setIcon(R.drawable.ic_info)
                    alertAppUnderMaintenance.setTitle("App Maintenance")
                    alertAppUnderMaintenance.setMessage("dear $firstName $lastName currently the application is undergoing maintenance please try again later")
                    alertAppUnderMaintenance.setPositiveButton("Ok") { dialog, _ ->
                        //show the notification(BigTextNotifyGen)
                        val bigTitle = "Attention"
                        val bigMessage =
                            "Sasa $firstName $lastName currently the application is undergoing maintenance.\n" + "Services will be fully restored after maintenance is over.\n" + "Kindly be patient as the software team resolve some issues.\n " + "Thank you."
                        val smallMessage = "application is under maintenance"
                        val byMessage = getString(R.string.by_me)
                        val bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.cart)
                        val smallTitle = "attention"
                        //

                        //showing of the message
                        BigTextNotificationGen(
                            this@ProductsHome,
                            bigTitle,
                            smallTitle,
                            bigMessage,
                            smallMessage,
                            bitmapImage,
                            R.drawable.ic_cart,
                            byMessage
                        ).funCreateBigTextNotification()
                        //
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            //sign out the user and then finish app
                            FirebaseAuth.getInstance().signOut()
                            dialog.dismiss()
                            //
                            //finish the app
                            finish()
                        } else {
                            //user not logged in dismiss the dialog and sign out
                            dialog.dismiss()
                            //finish the app
                            finish()
                            //
                        }
                    }
                    alertAppUnderMaintenance.create()
                    alertAppUnderMaintenance.show()

                }
            }
        } else {
            val alertAppUnderMaintenance = MaterialAlertDialogBuilder(this@ProductsHome)
            alertAppUnderMaintenance.setCancelable(false)
            alertAppUnderMaintenance.setIcon(R.drawable.ic_info)
            alertAppUnderMaintenance.setTitle("App Maintenance")
            alertAppUnderMaintenance.setMessage("dear user currently the application is under maintenance please try again later")
            alertAppUnderMaintenance.setPositiveButton("Ok") { dialog, _ ->

                //show the notification(BigTextNotifyGen)
                val bigTitle = "Attention"
                val bigMessage =
                    "Sasa, the application is undergoing maintenance" + "services will be fully restored after maintenance is over " + "kindly be patient as the software team resolve some issues " + "thank you."
                val smallMessage = "application is under maintenance"
                val byMessage = getString(R.string.by_me)
                val bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.cart)
                val smallTitle = "attention"
                //

                //showing of the message
                BigTextNotificationGen(
                    this@ProductsHome,
                    bigTitle,
                    smallTitle,
                    bigMessage,
                    smallMessage,
                    bitmapImage,
                    R.drawable.ic_cart,
                    byMessage
                ).funCreateBigTextNotification()
                //


                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    //sign out the user and then finish app
                    FirebaseAuth.getInstance().signOut()
                    dialog.dismiss()
                    //finish the app
                    finish()
                    //
                } else {
                    //user not logged in dismiss the dialog and sign out
                    dialog.dismiss()
                    //finish the app
                    finish()
                }
            }
            alertAppUnderMaintenance.create()
            alertAppUnderMaintenance.show()

        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertFailureServer(it: Task<DocumentSnapshot>) {
        //code begins
        //log the error to find find reason why
        Log.d(TAG, "funAlertFailureServer: error:${it.exception?.message}\n")
        //
        val alertErrorFromServer = MaterialAlertDialogBuilder(this@ProductsHome)
        alertErrorFromServer.setIcon(R.drawable.ic_info)
        alertErrorFromServer.setCancelable(false)
        alertErrorFromServer.setTitle("Server Failure")
        alertErrorFromServer.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alertErrorFromServer.setMessage("application encountered an error while trying to communicate with the server kindly try again later")
        alertErrorFromServer.setPositiveButton("exit") { dialog, _ ->
            //dismiss the dialog
            dialog.dismiss()
            //finish the activity and end the app ->exit the user
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                FirebaseAuth.getInstance().signOut()
                finish()
            } else {
                //
                finish()
            }
            //
        }
        alertErrorFromServer.setNegativeButton("retry") { dialog, _ ->
            //call fun to recreate this activity again
            funRecreateTheActivity()
            //
            dialog.dismiss()
        }
        alertErrorFromServer.create()
        alertErrorFromServer.show()
        //code begins
    }

    private fun funRecreateTheActivity() {
        //code begins
        funToastyShow("re-trying...")
        this@ProductsHome.recreate()
        //code ends
    }

    private fun funCheckAdminShieldActivationMenuItem() {
        //get the bottomNav menu and extract the shield item w/c be passed in the constructor
        //to determine if be shown or not basing on the fact if current user is an admin
        val bottomNavMenu = bottomNav.menu
        val menuShield = bottomNavMenu.findItem(R.id.adminShield)
        //init of the adminVerify class
        val administrationVerify = AdministrationVerify(this@ProductsHome)
        administrationVerify.verifyIsAdminShowShield(menuShield)
        //
    }

    @SuppressLint("SetTextI18n")
    private fun funFetchProfileDataBackend() {
        //code begins
        val uniqueUID = FirebaseAuth.getInstance().uid
        val backendStoreCloud = FirebaseFirestore.getInstance()
        //beginning the process of obtaining the data from the cloud path(comrade user/uniqueID)
        if (uniqueUID != null) {
            backendStoreCloud.collection(Registration.ComradeUser).document(uniqueUID).get()
                .addOnSuccessListener {
                    //check  if the snapshot exits
                    if (it.exists()) {
                        //convert the data into class readable
                        val classDataProfile: DataProfile? = it.toObject(DataProfile::class.java)
                        if (classDataProfile != null) {
                            val email = classDataProfile.Email
                            val fName = classDataProfile.FirstName
                            val lName = classDataProfile.LastName
                            val phone = classDataProfile.PhoneNumber
                            val university = classDataProfile.University
                            val image = classDataProfile.ImagePath
                            val password = classDataProfile.Password
                            val registrationDate = classDataProfile.registrationDate

                            //init the shared pref and check if the data it contains is equal to the data
                            //being fetched from store if no, put the data being fetched from the store as current

                            //init shared preference from where we go obtain the data stored
                            val sharedPreferencesInit =
                                getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
                            sharedPreferencesInit.apply {
                                val imageInSharedPref = getString("image", "")
                                val firstNameInSharedPref = getString("firstname", "")
                                val lastnameInSharedPref = getString("lastname", "")
                                val phoneInSharedPref = getString("phone", "")
                                val universityInSharedPref = getString("university", "")
                                val emailInSharedPref = getString("email", "")
                                val registrationDateInSharedPref = getString("date", "")

                                //compare the data present if is the same from the one being fetched
                                if (imageInSharedPref != image || phoneInSharedPref != phone || universityInSharedPref != university || emailInSharedPref != email || firstNameInSharedPref != fName || lastnameInSharedPref != lName || registrationDateInSharedPref != registrationDate) {
                                    //something is wrong with the data present in the shared prefs thus we should load the data being fetched to be legit into
                                    //the shared prefs

                                    //saving the data into the shared prefs
                                    val sharedPreferences = getSharedPreferences(
                                        sharedPreferenceName, Context.MODE_PRIVATE
                                    )
                                    sharedPreferences.edit().apply {
                                        putString("email", email)
                                        putString("firstname", fName)
                                        putString("lastname", lName)
                                        putString("phone", phone)
                                        putString("university", university)
                                        putString("image", image)
                                        putString("password", password)
                                        putString("registrationDate", registrationDate)
                                    }.apply()

                                    //load the data into the  nav header of data being fetched
                                    Glide.with(this@ProductsHome).apply {
                                        load(image).into(headerImage)
                                        //loading data on the header textViews
                                        headerEmail.text = email
                                        headerTitleUsername.text = "$fName $lName"
                                        headerPhoneNumber.text = phone
                                        headerUniversity.text = university
                                        headerDateRegistration.text =
                                            "Registered: $registrationDate"
                                        //
                                    }
                                    //


                                } else {
                                    //the data in shared pref is the same as the data being loaded, thus set the data from shared pref onto nav
                                    //header
                                    //load the data into the  nav header of data being fetched
                                    Glide.with(this@ProductsHome).apply {
                                        load(imageInSharedPref).into(headerImage)
                                        //loading data on the header textViews
                                        headerEmail.text = emailInSharedPref
                                        headerTitleUsername.text =
                                            "$firstNameInSharedPref $lastnameInSharedPref"
                                        headerPhoneNumber.text = phoneInSharedPref
                                        headerUniversity.text = universityInSharedPref
                                        //
                                    }
                                    //
                                }
                            }
                        }
                        //

                    } else if (!it.exists()) {
                        //toast to the user of an error occurred
                        Toast.makeText(
                            this@ProductsHome,
                            "Unknown error occurred while retrieving data!",
                            Toast.LENGTH_LONG
                        ).show()
                        //
                    }
                }.addOnFailureListener {
                    //data fetch was a failure
                    AlertDialog.Builder(this@ProductsHome).setTitle("Data Fetching Failed")
                        .setIcon(R.drawable.ic_warning).setCancelable(false)
                        .setMessage("application encountered an error while loading the data from the server.\nReason:\n(${it.message.toString()})")
                    //
                    return@addOnFailureListener
                }
        }
        //
        //code ends
    }


    private fun fragmentDefaultAdd() {
        //inflate default home fragment
        val homeFragment = HomeFragment()
        val stringHomeFragment = "homeFragment"
        //
        fragmentInit(homeFragment, stringHomeFragment)
        //
    }

    private fun fragmentInit(fragment: Fragment, tag: String) {
        //code begins
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragment, tag)
            .commitNow()
        //animate bottom nav
        val layoutAnimationController = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                this@ProductsHome, R.anim.bottom_up
            )
        )
        layoutAnimationController.order = LayoutAnimationController.ORDER_REVERSE
        bottomNav.layoutAnimation = layoutAnimationController
        bottomNav.startLayoutAnimation()
        //
        //code ends

    }

    @Suppress("DEPRECATION")
    private fun funHandleBottomNavProducts() {
        //code begins
        bottomNav.setOnNavigationItemSelectedListener {
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
                    Snackbar.make(bottomNav, "you are going to log out", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(
                            resources.getColor(
                                R.color.cardview_dark_background, theme
                            )
                        ).setActionTextColor(
                            resources.getColor(
                                R.color.accent_material_light, theme
                            )
                        ).setAction("sure") {
                            //code begins
                            //call functionLogout
                            funLogoutConfirmation()
                            //code ends
                        }.show()
                    //
                }

                R.id.adminShield -> {
                    //migrate to the administration
                    funAdminMigration()
                    //
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        //code ends
    }

    private fun funAdminMigration() {
        //code begins
        //check email,password,isAdmin to proceed to administration
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        startActivity(Intent(this@ProductsHome, Administration::class.java))
        //code ends
    }

    private fun funLogoutConfirmation() {
        //sign out the user and then finish the application
        if (FirebaseAuth.getInstance().currentUser != null) {
            //logout the user
            FirebaseAuth.getInstance().signOut()
            //finish the application
            finish()
        } else {
            //finish the application thou no current user is logged is logged in
            finish()
        }
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
                val alertD = MaterialAlertDialogBuilder(this@ProductsHome)
                alertD.setTitle("Email Verification")

                alertD.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
                alertD.setCancelable(false)
                alertD.setMessage(
                    "(${userCurrent.email})" + "\nneeds to be verified for your account to become approved officially"
                )
                alertD.setIcon(R.drawable.ic_info)
                alertD.setPositiveButton("verifyNow") { dialog, _ ->
                    //open drawer
                    drawerLayout.openDrawer(GravityCompat.START)
                    //disable the verify email address until user accepts at snackBar
                    headerButtonVerifyEmail.isEnabled = false
                    //

                    //Snack to show user how to go about
                    Snackbar.make(
                        drawerLayout, "click verify email button", Snackbar.LENGTH_INDEFINITE
                    ).setBackgroundTint(
                        resources.getColor(
                            R.color.black, theme
                        )
                    ).setAction("Ok") {
                        //enable the button verify email and visible
                        headerButtonVerifyEmail.apply {
                            isEnabled = true
                            visibility = View.VISIBLE
                        }
                        //animate the button using the handler 0.5 seconds delay
                        Handler(mainLooper).postDelayed({
                            headerButtonVerifyEmail.startAnimation(
                                AnimationUtils.loadAnimation(
                                    this@ProductsHome, R.anim.fadeout
                                )
                            )
                        }, 500)

                        //
                    }.setActionTextColor(Color.parseColor("#54FA08")).show()
                    //

                    //dismiss the dialog to avoid window leaked RT Exceptions
                    dialog.dismiss()
                    //
                }
                alertD.setNegativeButton("verifyLater") { dialog, _ ->
                    //show snack not verified is the account
                    Snackbar.make(toolbar, "account not yet approved!", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(
                            resources.getColor(
                                R.color.accent_material_light, theme
                            )
                        ).show()
                    //

                    //dismiss the dialog
                    dialog.dismiss()
                    //
                }
                alertD.create()
                alertD.show()
                //

                //
            } else if (userCurrent.isEmailVerified) {
                //user verified the email address
                //disable button verify email,textViewBannerEmailVerify
                headerVerificationEmail.visibility = View.GONE
                headerButtonVerifyEmail.visibility = View.GONE
                //enabling the visibility of the update button since a user cannot update his/her a/c unless is verified the email
                headerButtonUpdate.visibility = View.VISIBLE
                //show notification
                funShowApprovedAccountNotification()
                //
            }

            //code ends
        }
    }

    private fun funShowApprovedAccountNotification() {
        //show notification of congratulations email verified
        val bigTextNotificationEmail = BigTextNotificationEmail(
            this@ProductsHome,
            "Congratulations",
            "account approved",
            "you successfully verified the account email address.\n" + "now post your products freely and get your earnings from the interested members",
            "MarketCM approved your account\n",
            BitmapFactory.decodeResource(resources, R.drawable.cart),
            R.drawable.ic_cart,
            "by:shimmitadouglas"
        )

        val directory = this@ProductsHome.filesDir
        val file = File("$directory/showFile.txt")
        if (!file.exists()) {
            //show the notification as it has not been shown to create this file
            bigTextNotificationEmail.funCreateBigTextNotification()
            //
            Log.d(TAG, "funShowApprovedAccountNotification: fileDoesNot Exist")


        } else if (file.exists()) {
            //do not show notification since it created this file firstTime it was shown
            Log.d(TAG, "funShowApprovedAccountNotification: fileExists")
            //
            //
            val files = this@ProductsHome.filesDir.listFiles()
            for (file in files!!) {
                Log.d(TAG, "funShowApprovedAccountNotification:files:$file")
            }
            //
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
        alertChangePassword.setPositiveButton("change Now") { dialog, _ ->

            val textPasswordChange = headerEditChange.text.toString()
            //check if textPassword is null
            if (textPasswordChange.isEmpty()) {
                funToastyFail("empty fields not allowed")
                //dismiss the dialog
                dialog.dismiss()
                //
            } else if (textPasswordChange.isNotEmpty() && textPasswordChange.trim() != "") {
                //call function to enhance the operation of password change
                funChangePasswordStart(textPasswordChange)
                //dismiss the dialog to avoid RT Exceptions
                dialog.dismiss()
                //
            }
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
        cbShowPassword.setOnCheckedChangeListener { _, b ->
            //cb is checked
            if (b) {
                //code begins
                //text transformation is null always when not check
                headerEditChange.transformationMethod = null
                //code ends
            }
            //cb is no checked
            else {
                //code begins
                //starting text transformation od showing the password
                headerEditChange.transformationMethod = PasswordTransformationMethod.getInstance()
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
                    AlertDialog.Builder(this@ProductsHome).setMessage(it.exception?.message)
                        .setIcon(R.drawable.ic_warning).show().create()
                    //

                }

            }

        //

        //code ends
    }

    private fun funUpdatePasswordStore(
        textPasswordChange: String, progressionDialog: ProgressDialog
    ) {
        //code begins
        //creating a firebase instance then acquiring a uniqueUID for differentiation of users Under One Collection Of Document ComradeUsers
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        //creating instance of fStore
        val fStoreUsers = FirebaseFirestore.getInstance()

        //creating the map of keySpecific to the password
        val keyPassword = "Password"
        //
        val mapUpdatePassword = hashMapOf(keyPassword to textPasswordChange)
        //beginning the process of updating the password
        if (currentUID != null) {
            fStoreUsers.collection(Registration.ComradeUser).document(currentUID)
                .update(mapUpdatePassword as Map<String, String>).addOnCompleteListener {
                    //password update successful
                    if (it.isSuccessful) {
                        //dismiss the progress dialog
                        progressionDialog.dismiss()
                        //alert the user with new password
                        //show alert
                        AlertDialog.Builder(this@ProductsHome).setTitle("Congratulations!")
                            .setMessage(
                                "password update was successful" + "\nyour new login account password is ($textPasswordChange)"
                            ).setIcon(R.drawable.ic_nike_done).setCancelable(false)
                            .setPositiveButton("okay") { dialog, _ ->
                                //dismiss the dialog
                                dialog.dismiss()
                                //
                            }.show().create()
                        //
                        //
                    }

                    //password update failure in the fStore
                    else if (!it.isSuccessful) {

                        //dismiss the pg
                        progressionDialog.dismiss()
                        //show alert
                        //show alert
                        AlertDialog.Builder(this@ProductsHome).setMessage(it.exception?.message)
                            .setIcon(R.drawable.ic_warning).show().create()
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
                this@ProductsHome, R.anim.rotate_avg
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
            //trim the number to remove white spaces
            textPhoneEntered.trim()
            //
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
            funToastyFail("number incomplete!")
        } else if (textPhoneEntered.length > 10) {
            funToastyFail("this number is long!")
        } else {
            functionUpdatePhoneNow(textPhoneEntered)
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun functionUpdatePhoneNow(textPhoneEntered: String) {
        //code begins
        //create pg
        val progressDialogPhoneUpdate = ProgressDialog(this@ProductsHome)
        progressDialogPhoneUpdate.setTitle("Phone Number Update")
        progressDialogPhoneUpdate.setMessage("updating...")
        progressDialogPhoneUpdate.setCancelable(false)
        progressDialogPhoneUpdate.create()
        progressDialogPhoneUpdate.show()

        val keyPhone = "PhoneNumber"

        //creating the  map to update the phone number
        val mapUpdateNumber = hashMapOf(keyPhone to textPhoneEntered)

        //begin init of the UID and then the fStore for Storage update
        val userIDCurrent = FirebaseAuth.getInstance().currentUser?.uid
        //
        val fStore = userIDCurrent?.let {
            FirebaseFirestore.getInstance().collection(Registration.ComradeUser).document(
                it
            ).update(mapUpdateNumber as Map<String, Any>).addOnCompleteListener {
                //code begins
                if (it.isSuccessful) {
                    //change the message of the progressDialog
                    progressDialogPhoneUpdate.setMessage("validating")
                    //
                    funUpdatePhoneInProductsPosts(textPhoneEntered, progressDialogPhoneUpdate)
                    //code ends
                } else if (!it.isSuccessful) {
                    //dismiss the alert
                    progressDialogPhoneUpdate.dismiss()
                    //alert the user of error
                    AlertDialog.Builder(this@ProductsHome).setMessage(it.exception?.message)
                        .setIcon(R.drawable.ic_warning).setPositiveButton("ok") { dialog, _ ->
                            //dismiss the dialog
                            dialog.dismiss()
                            //migrate products home
                            funIntentMigrateHome()
                            //
                        }.show().create()
                    //
                }
                //code ends
            }
        }

        //code ends
    }

    @Suppress("DEPRECATION")
    private fun funUpdatePhoneInProductsPosts(
        textPhoneEntered: String, progressDialogPhoneUpdate: ProgressDialog
    ) {
        //code begins
        val keyPhone = "phone"
        val mapData = hashMapOf(keyPhone to textPhoneEntered)
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val storePublicRepoPosts = FirebaseFirestore.getInstance()
        storePublicRepoPosts.collection(ModalPostProducts.CollectionPost).get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (currentUserID != null) {
                    val documents = it.result
                    for (doc in documents) {
                        var documentIDs = doc.id
                        if (documentIDs.contains(currentUserID)) {
                            storePublicRepoPosts.collection(ModalPostProducts.CollectionPost).document(documentIDs)
                                .update(mapData as Map<String, Any>).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        //dismiss the progressDialog
                                        funIntentMigrateHome()
                                        //intent migrate home
                                        funIntentMigrateHome()
                                        //
                                    } else if (!it.isSuccessful) {
                                        //toast error
                                        funToastyFail("unexpected error encountered!")
                                        //dismiss the progress
                                        progressDialogPhoneUpdate.dismiss()
                                        //intent migrate
                                        funIntentMigrateHome()
                                        //return home
                                        return@addOnCompleteListener
                                    }
                                }
                        }
                    }
                }
            } else if (!it.isSuccessful) {
                //toast encountered an error
                funToastyFail("encountered an error!")
                //dismiss the progressDialog
                progressDialogPhoneUpdate.dismiss()
                //intent return home
                funIntentMigrateHome()
                //
                return@addOnCompleteListener
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
                this@ProductsHome, R.anim.rotate_avg
            )
        )
        //init of first name $ lastname wit reference to the view
        val firstNameEnteredView =
            viewUpdateUsername.findViewById<TextInputEditText>(R.id.edtUpdateFirstName)
        val lastNameEnteredView =
            viewUpdateUsername.findViewById<TextInputEditText>(R.id.edtUpdateLastName)
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

            //trim the names to remove the white spaces
            textFirstName.trim()
            textLastName.trim()
            //

            //checking the legitimacy of the data entered
            if (TextUtils.isEmpty(textFirstName) or TextUtils.isEmpty(textLastName)) {
                //toast empty fields not allowed
                funToastyFail("missing fields unacceptable!")
                //
                dialog.dismiss()
                //
            } else {
                //begin update of username
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
    @Suppress("DEPRECATION")
    private fun startUpdateOfUserName(
        firstName: String, lastName: String
    ) {
        //code begins
        val progressDg = ProgressDialog(this@ProductsHome)
        progressDg.setCancelable(false)
        progressDg.setTitle("username update")
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
        val fStore = FirebaseFirestore.getInstance().collection(Registration.ComradeUser)
        //
        //begin update of the user

        if (userIDLogged != null) {
            fStore.document(userIDLogged).update(mapUserUpdateUserName as Map<String, String>)
                .addOnCompleteListener {

                    //successfully updated the username on the section Comrade Users
                    if (it.isSuccessful) {
                        progressDg.setMessage("validating")
                        //begin updating of the username present on the products
                        //path to the public repo(CollectionPost/UID+Timer/data)
                        //obtain all the userID present on the commodities related to the currentUID
                        funUpdateUsernameOnProducts(progressDg, firstName, lastName)
                        //
                    }
                    //failed to update the username
                    else if (!it.isSuccessful) {
                        //dismiss the pg
                        progressDg.dismiss()
                        //

                        //alert the user of the error
                        AlertDialog.Builder(this@ProductsHome).setMessage(it.exception?.message)
                            .setIcon(R.drawable.ic_warning).create().show()
                        //

                    }

                }
        }


        //code ends
    }

    @Suppress("DEPRECATION")
    private fun funUpdateUsernameOnProducts(
        progressDg: ProgressDialog,
        firstName: String,
        lastName: String,
    ) {
        //val current user
        val userCurrentID = FirebaseAuth.getInstance().currentUser?.uid
        //
        //obtaining the fullName of the user from the parameter of the current function
        val fullName = "$firstName $lastName"
        //init of the storage
        val storeProductsPosted = FirebaseFirestore.getInstance()
        storeProductsPosted.collection(ModalPostProducts.CollectionPost).get().addOnCompleteListener {
            if (it.isSuccessful) {
                //update the message of the progressDialog
                val documents = it.result
                //iteration through the document to obtain individual doc ids
                for (doc in documents) {
                    //checking if the document exists
                    if (doc.exists()) {
                        //checking if the current userUID is null
                        if (userCurrentID != null) {
                            //obtaining products that are connected to the user ID and then updating their names
                            //appropriately
                            val documentsID = doc.id
                            //
                            if (documentsID.contains(userCurrentID)) {
                                //begin the process of updating the name of the products related to the user
                                funUpdateTheUserNameInProducts(documentsID, fullName, progressDg)
                            }
                        } else {
                            //dismiss the progressDialog and return
                            progressDg.dismiss()
                            return@addOnCompleteListener
                            //
                        }
                    } else {
                        //return and dismiss the progress Dialog
                        progressDg.dismiss()
                        funToastyFail("oops! something went wrong")
                        return@addOnCompleteListener
                    }
                }

            } else if (!it.isSuccessful) {
                //dismiss the progressD
                progressDg.dismiss()
                //toast error
                funToastyFail("oops! something went wrong")
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun funUpdateTheUserNameInProducts(
        documentsID: String, fullName: String, progressDg: ProgressDialog
    ) {
        //code begins
        val keyName = "Owner"
        val mapData = hashMapOf(keyName to fullName)
        val storePostPublicRepo = FirebaseFirestore.getInstance()
        storePostPublicRepo.collection(ModalPostProducts.CollectionPost).document(documentsID)
            .update(mapData as Map<String, Any>).addOnCompleteListener {
                if (it.isSuccessful) {
                    //code begins
                    progressDg.dismiss()
                    //call function intent migration to the products home
                    funIntentMigrateHome()
                    //code ends
                } else if (!it.isSuccessful) {
                    //dismiss the progress Dialog
                    progressDg.dismiss()

                    //intent return home
                    funIntentMigrateHome()
                    //toast the the error
                    funToastyFail("encountered an error while updating!")
                    //return the listener
                    return@addOnCompleteListener
                }
            }
        //code ends
    }

    private fun funIntentMigrateHome() {
        //code begins
        val intent = Intent(this@ProductsHome, ProductsHome::class.java)
        startActivity(intent)
        //code ends
    }

    private fun functionUpdateProfileImage() {
        //code begins
        Snackbar.make(
            drawerLayout, "select your preferred image from gallery", Snackbar.LENGTH_INDEFINITE
        ).setAction("pick") {
            //check if the permissions read_write are granted or null
            Dexter.withContext(this@ProductsHome).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    //permissions granted
                    //launch intent update image
                    val intentUpdatePicture = Intent()
                    intentUpdatePicture.action = Intent.ACTION_PICK
                    intentUpdatePicture.type = "image/*"
                    galleryPickUpdate.launch(intentUpdatePicture)
                    //code ends
                    //
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                ) {
                    //permission not granted
                    funShowAlertPermissionRationale()
                    //
                }
            }).check()
            //

        }.setBackgroundTint(resources.getColor(R.color.accent_material_light, theme)).show()
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funShowAlertPermissionRationale() {

        //code begins
        //show  the alert of provided by the rationale dialog
        val alertPermissionRationale = MaterialAlertDialogBuilder(this@ProductsHome)
        alertPermissionRationale.setTitle("Permissions")
        alertPermissionRationale.setIcon(R.drawable.ic_info)
        alertPermissionRationale.setMessage(
            "Market CM requires that the requested permissions are necessary for it to function properly." + " grant the permissions to use the application"
        )
        alertPermissionRationale.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
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

    @SuppressLint("SetTextI18n")
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
        headerDateRegistration = viewHeader.findViewById(R.id.registrationDateHeader)

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
        bottomNav = findViewById(R.id.bottomNavProducts)

        drawerToggle = ActionBarDrawerToggle(
            this@ProductsHome, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose
        )

        //setting support for toolbar
        //  navView Be attached Toolbar
        setSupportActionBar(toolbar)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        toolbar.setNavigationOnClickListener {
            //creating a layoutAnim controller for the drawerLayout
            val controllerAnimDrawerLayout = LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    this@ProductsHome, R.anim.bottom_up
                )
            )
            controllerAnimDrawerLayout.delay = 0.2f
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

        //code ends

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.smsDev -> {
                //launch function to sms developer thread new
                funSMSDev()
            }
            R.id.emailDev -> {
                //launch function on a different thread to email developer
                funEmailDeveloper()
            }
            R.id.callDev -> {
                //launch function call developer on a different thread
                funCallDev()
            }

            R.id.aboutMarketCM -> {
                funShowModalSheetMarketCM()
            }
            R.id.aboutPrivacy -> {
                funShowModalSheetPrivacy()
            }

            R.id.reportScammer -> {
                funAlertReportScammer()

            }
            R.id.shareApplication -> {
                funShareApplication()
            }


        }

        return true
    }

    private fun funShowModalSheetPrivacy() {
        //coded begins
        val which = "policy"
        val modalPolicy = ModalPrivacyMarket(which)
        modalPolicy.show(supportFragmentManager, "modal_privacy_policy")
        //code ends
    }

    private fun funShowModalSheetMarketCM() {
        //code begins
        val which = "about"
        val modalAboutMarket = ModalPrivacyMarket(which)
        modalAboutMarket.show(supportFragmentManager, "modal_about_market_cm")
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertReportScammer() {
        //code begins
        val listScammerOption: Array<String> =
            resources.getStringArray(R.array.report_scammer_options)
        var selected = ""
        val alertReportScammer = MaterialAlertDialogBuilder(this@ProductsHome)
        alertReportScammer.setTitle("report counterfeit")
        alertReportScammer.setIcon(R.drawable.ic_report)
        alertReportScammer.setCancelable(false)
        alertReportScammer.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alertReportScammer.setSingleChoiceItems(listScammerOption, 1) { _, which ->
            selected = listScammerOption[which]
            funToastyShow(listScammerOption[which])
        }
        alertReportScammer.setPositiveButton("ok") { dialog, _ ->
            if (selected.isNotEmpty()) {
                if (selected.contains("counterfeit", true)) {
                    //call fun false products
                    funReportScammerCounterfeitProducts()
                    //dismiss the dg
                    dialog.dismiss()
                    //
                }
            } else if (selected.isEmpty()) {
                //call
                funToastyShow("select option")
                //dismiss the dg
                dialog.dismiss()
                //
            }
        }
        alertReportScammer.setNegativeButton("no") { dialog, _ ->
            //dismiss the  dialog
            dialog.dismiss()
            //
        }
        alertReportScammer.create().show()

        //code ends
    }

    @SuppressLint("InflateParams", "UseCompatLoadingForDrawables")
    private fun funReportScammerCounterfeitProducts() {
        //code begins
        //infiltrate the view containing the counterfeit products then show it in an alert dialog
        val viewCounterfeit = LayoutInflater.from(this@ProductsHome)
            .inflate(R.layout.layout_report_scammer_counterfeit_view, null, false)
        val editTextProductCode =
            viewCounterfeit.findViewById<EditText>(R.id.edtReportScammerOptionCounterfeitCode)
        val editTextMessage =
            viewCounterfeit.findViewById<EditText>(R.id.edtReportScammerOptionCounterfeitMessage)


        val alertSubmitReport = MaterialAlertDialogBuilder(this@ProductsHome)
        alertSubmitReport.setCancelable(false)
        alertSubmitReport.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alertSubmitReport.setView(viewCounterfeit)
        alertSubmitReport.setIcon(R.drawable.ic_info)
        alertSubmitReport.setTitle("submission alert")
        alertSubmitReport.setPositiveButton("submit") { dialog, _ ->
            val productCode = editTextProductCode.text.toString()
            val productMessage = editTextMessage.text.toString()
            if (productCode.isEmpty() or (productMessage.isEmpty())) {
                funToastyFail("cannot submit empty fields!")
            } else {
                //call function submit counterfeit
                funSubmitCounterfeitProductReport(productCode, productMessage)
                //dismiss
                dialog.dismiss()
                //
            }
        }
        alertSubmitReport.setNegativeButton("dismiss") { dialog, _ ->
            //dismiss the dg
            dialog.dismiss()
            //
        }
        alertSubmitReport.create().show()
        //code ends
    }

    private fun funSubmitCounterfeitProductReport(productCode: String, productMessage: String) {
        //code begins
        if (productCode.isNotEmpty() and (productMessage.isNotEmpty())) {
            //legit data. check internet
            val internetCheck = NetworkMonitor(this@ProductsHome)
            if (internetCheck.checkInternet()) {
                //internet is up
                //call fun post the report to the admin
                funSubmitCounterfeitNow(productCode, productMessage)
                //
            }

        }
        //code ends
    }

    @SuppressLint("InflateParams", "UseCompatLoadingForDrawables")
    private fun funSubmitCounterfeitNow(productCode: String, productMessage: String) {
        val viewProgress = LayoutInflater.from(this@ProductsHome)
            .inflate(R.layout.general_progress_dialog_view, null, false)
        val progressCounterfeitDialog = ProgressDialog(this@ProductsHome)
        progressCounterfeitDialog.setTitle("report counterfeit")
        progressCounterfeitDialog.setView(viewProgress)
        progressCounterfeitDialog.setMessage("code confirmation...")
        progressCounterfeitDialog.setIcon(R.drawable.ic_report)
        progressCounterfeitDialog.setCancelable(false)
        progressCounterfeitDialog.create()
        progressCounterfeitDialog.show()

        //creating an arraylist that will contain all data that will be used to match productCodes
        val arrayHoldData = arrayListOf<DataClassProductsData>()
        arrayHoldData.clear()
        //code begins
        //fetch all data from the store to check the existence of this product code
        val store = FirebaseFirestore.getInstance()
        store.collection(ModalPostProducts.CollectionPost).get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                for (data in it.result.documents) {
                    val dataFilter: DataClassProductsData? =
                        data.toObject(DataClassProductsData::class.java)
                    //adding the class of data onto the array then will match the codes
                    if (dataFilter != null) {
                        arrayHoldData.add(dataFilter)
                    }
                    //
                }

                //iterate through the array to match the product codes
                if (arrayHoldData.isNotEmpty()) {
                    //will hold suspect details
                    var suspectUniqueUID = ""
                    //get product timerControl ID that will fetch all the details about the product
                    var productTimerControllerID = ""
                    var isCodeFound = false

                    arrayHoldData.forEach {
                        if (it.productID.equals(productCode)) {
                            //boolean true code exits
                            isCodeFound = true
                            //get the details of the suspect by grabbing the key
                            suspectUniqueUID = it.userID.toString()
                            //get product timerControl ID that will fetch all the details about the product
                            //posted as a counterfeit one
                            productTimerControllerID = it.timerControlID.toString()
                            //
                        } else return@forEach
                    }


                    //code exists
                    if (isCodeFound) {
                        //begin posting of the data
                        if (suspectUniqueUID.isNotEmpty() && productTimerControllerID.isNotEmpty()) {
                            //change the message of the progress dialog to confirmed
                            progressCounterfeitDialog.setMessage("confirmed")
                            progressCounterfeitDialog.setIcon(R.drawable.ic_nike_done)
                            //check if the suspectUID is ==current user in order to prevent further reporting since
                            //the product belongs to the current user(u cannot report your own product)

                            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
                            if (currentUserID != null) {
                                if (currentUserID == suspectUniqueUID) {
                                    //dismiss the progress dialog
                                    progressCounterfeitDialog.dismiss()
                                    //
                                    MaterialAlertDialogBuilder(this@ProductsHome).setTitle("attention!")
                                        .setMessage(
                                            "system confirmed that this product belongs to you\n" + "\nactually you are the one who posted it!\n" + "\nyou cannot report your own products as counterfeit"
                                        ).setIcon(R.drawable.ic_warning).setBackground(
                                            resources.getDrawable(
                                                R.drawable.general_alert_dg, theme
                                            )
                                        ).setPositiveButton("ok") { dialog, _ ->

                                            //dismiss the dialog
                                            dialog.dismiss()
                                            //
                                        }.setCancelable(false).create().show()
                                }
                            } else {
                                //call a new function to finalise the process of reporting the user
                                funFinaliseReportingCounterfeit(
                                    progressCounterfeitDialog,
                                    productCode,
                                    productMessage,
                                    suspectUniqueUID,
                                    productTimerControllerID
                                )
                                //
                            }

                        } else {
                            //dismiss the progress dialog
                            progressCounterfeitDialog.dismiss()
                            //
                            funToastyFail("operation failure!")
                        }
                        //

                    } else {
                        //dismiss the progress dialog
                        progressCounterfeitDialog.dismiss()
                        //
                        //no exist is the code
                        funToastyFail("product code does not exist")
                    }


                } else {
                    //dismiss the progress dialog
                    progressCounterfeitDialog.dismiss()
                    //
                    funToastyFail("unknown error has occurred!")
                    return@addOnCompleteListener
                }
                //

            } else if (!it.isSuccessful) {
                //dismiss pg
                progressCounterfeitDialog.dismiss()
                //
                //toast an error
                funToastyFail("unknown error has occurred!")
                //
            }
        }
        //
        //code ends
    }

    @SuppressLint("SimpleDateFormat")
    private fun funFinaliseReportingCounterfeit(
        progressCounterfeitDialog: ProgressDialog,
        productCode: String,
        productMessage: String,
        suspectUniqueUID: String?,
        productTimerControllerID: String?
    ) {
        //code begins
        val uniqueUIDVictim = FirebaseAuth.getInstance().uid
        //creating the keys to be used for mapping the data
        val keyProductCode = "productCode"
        val keyProductController = "productControlUID"
        val keyClaimID = "ProductClaimID"
        val keyDate = "claimDate"
        val keyMessage = "productMessage"
        val keyVictimUID = "productVictimID"
        val keySuspectID = "productSuspectID"

        val calendar = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

        //getting current time millis
        val timerInMillis = System.currentTimeMillis()
        //evaluating the data
        val dataDateSent = formatter.format(calendar)
        val dataCombinationClaimID = uniqueUIDVictim + timerInMillis //also equals the document path
        //creating a map for the data
        val mapData = hashMapOf(
            keyDate to dataDateSent,
            keyProductCode to productCode,
            keyProductController to productTimerControllerID,
            keyMessage to productMessage,
            keyClaimID to dataCombinationClaimID,
            keyVictimUID to uniqueUIDVictim,
            keySuspectID to suspectUniqueUID
        )
        //
        //begin sending to the backend of the store
        //path for counterfeitReport(CollectionCounterfeit/dataCombinationClaimID)
        //check if UID is null else continue

        if (uniqueUIDVictim != null) {
            val store = FirebaseFirestore.getInstance()
            store.collection(CollectionCounterfeit).document(dataCombinationClaimID).set(mapData)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //dismiss the progress dialog
                        progressCounterfeitDialog.dismiss()
                        //

                        //show an alert congrats
                        MaterialAlertDialogBuilder(this@ProductsHome).setIcon(R.drawable.ic_nike_done)
                            .setTitle("report sent").setCancelable(false).setMessage(
                                "your report has been received successfully.\n" + "\nyou might be contacted in case of additional information " + "is to be enquired by our policy violation team.\n" + "\nthank you for choosing Market CM the better marketing option for a comrade."
                            ).setPositiveButton("okay") { dialog, _ ->
                                //dismiss the dialog
                                dialog.dismiss()
                                //
                            }.create().show()
                        //

                    } else if (!it.isSuccessful) {
                        //dismiss the progress dialog
                        progressCounterfeitDialog.dismiss()
                        //show toast error
                        funToastyFail("failed to send report try again!")
                        //
                        return@addOnCompleteListener
                    }


                }

        } else {
            funToastyFail("operation is not permitted!")
            //dismiss the progress Dialog
            progressCounterfeitDialog.dismiss()
            //
        }

        //code ends
    }


    private fun funShareApplication() {
        //code begins
        var intentShareApplication = Intent(Intent.ACTION_SEND)
        intentShareApplication.type = "text/plain"
        intentShareApplication.putExtra(
            Intent.EXTRA_TEXT, getString(R.string.share_market_cm)
        )
        startActivity(Intent.createChooser(intentShareApplication, getString(R.string.share_via)))
        //code ends
    }

    private fun funEmailDeveloper() {
        //code
        //get the name of the current user from the share preferences
        val sharedPrefs = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val firstName = sharedPrefs.getString("firstname", "")
        val lastname = sharedPrefs.getString("lastname", "")
        val fullName = "$firstName $lastname"
        //
        val emailsMyEmails = arrayOf("douglasshimita3@gmail.com", "shimitadouglas@gmail.com")
        val emailSubject = "write email subject here"
        val messageBodyText = "Hello $fullName write your message here"
        val intentEmail = Intent()
        intentEmail.action = Intent.ACTION_SEND
        intentEmail.setDataAndType(Uri.parse("email"), "message/rfc822")
        intentEmail.putExtra(Intent.EXTRA_EMAIL, emailsMyEmails)
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intentEmail.putExtra(Intent.EXTRA_TEXT, messageBodyText)
        startActivity(Intent.createChooser(intentEmail, "Launch Email"))
        //code ends

    }

    private fun funSMSDev() {
        //code begins
        val phoneNumber = "+254757450727"
        val messageBody =
            "hey,write your text here and send it to me, i will be glad to feedback you"
        val intentMessaging = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null))
        startActivity(Intent.createChooser(intentMessaging, "Launch SMS APP"))
        //code ends

    }

    private fun funCallDev() {
        //code begins
        //start an intent to the phone call
        val numberIntent = Intent()
        numberIntent.action = Intent.ACTION_DIAL
        numberIntent.data = Uri.parse("tel:+254757450727")
        startActivity(numberIntent)
        //
        //code ends

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private val galleryPickUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

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
                        this@ProductsHome, R.anim.rotate
                    )
                )

                //alert the user of beginning the process of updating the profile picture
                //after delay 3 seconds
                val thread = Thread {
                    headerImage.postDelayed({

                        val alertUserPictureUpdating = MaterialAlertDialogBuilder(this)
                        alertUserPictureUpdating.setCancelable(false)
                        alertUserPictureUpdating.setIcon(R.drawable.cart)
                        alertUserPictureUpdating.background =
                            resources.getDrawable(R.drawable.general_alert_dg, theme)
                        alertUserPictureUpdating.setTitle("updating profile picture")
                        alertUserPictureUpdating.setMessage("your selected profile picture is going to be updated")
                        alertUserPictureUpdating.setPositiveButton("update") { dialog, _ ->
                            //
                            funCompleteUpdatingProfileImage(uriDataUpdatePic)
                            //
                            //dialog dismiss
                            dialog.dismiss()
                            //
                        }
                        alertUserPictureUpdating.setNegativeButton("no"){
                                dialog,_->
                            //dismiss the dialog
                            dialog.dismiss()
                        }
                        alertUserPictureUpdating.create()
                        alertUserPictureUpdating.show()

                    }, 4300);
                }
                thread.start()
                //

            }

            //activity image pick failed
            if (it.resultCode == RESULT_CANCELED) {
                //code begins
                //show snackBar you did not pick an image from the gallery
                Snackbar.make(
                    bottomNav, "yo did not pick an image from gallery!", Snackbar.LENGTH_LONG
                ).setTextColor(Color.parseColor("#EEBD09")).show()
                //

                //code ends
            }
        }


    private fun funCompleteUpdatingProfileImage(uriDataUpdatePic: Uri?) {
        //code begins
        //pgD
        val progressDg = ProgressDialog(this@ProductsHome)
        progressDg.setCancelable(false)
        progressDg.setMessage("uploading image...")
        progressDg.create()
        progressDg.show()
        //
        val currentUID = FirebaseAuth.getInstance().uid
        val fabUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val fabStorageInt = FirebaseStorage.getInstance().reference
        //initiate the update of image at fab storage with the selected image from gallery
        //"$ComRadeUser/$currentUID/${fabUserEmail}/uri

        try {
            //compress the image into a size that is convenient for uploading to the storage w/c facilitates the saving of the storage
            val bitmap: Bitmap =
                MediaStore.Images.Media.getBitmap(contentResolver, uriDataUpdatePic)
            //init of the baos that will be used in the process of imageCompression
            val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
            //init of the compression process using the scale factor quality of 25
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
            //init of the byteArrayCompressed image by the help of toByteArray
            val byteArrayImageToUpdate: ByteArray = byteArrayOutputStream.toByteArray()
            //

            if (currentUID != null) {
                if (uriDataUpdatePic != null) {
                    if (fabUserEmail != null) {
                        fabStorageInt.child(Registration.ComradeUser).child(currentUID).child(fabUserEmail)
                            .putBytes(byteArrayImageToUpdate).addOnCompleteListener {
                                //code begins
                                //successfully obtained download uri
                                if (it.isSuccessful) {
                                    //keyToImageUri fStore
                                    val keyImageUri = "ImagePath"
                                    //get download URI from fabStorage

                                    it.result.storage.downloadUrl.addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            //download uri was fetched successfully hence get the download Uri to string
                                            //updating the pg to
                                            progressDg.setMessage("validation")
                                            //obtaining the uri from
                                            val urlDownloadUrlString = it.result.toString()
                                            //calling a function that will allow now continuation to the fStore for Storing new Update imageLink
                                            //call function to update the mapping data in the fStore
                                            funUpdateStoreImageUrl(
                                                urlDownloadUrlString, keyImageUri, progressDg
                                            )
                                            //
                                        } else if (!it.isSuccessful) {
                                            //dismiss the progress dialog
                                            progressDg.dismiss()
                                            //
                                            //alert the error to the user
                                            AlertDialog.Builder(this@ProductsHome)
                                                .setMessage(it.exception?.message)
                                                .setIcon(R.drawable.ic_warning).create().show()
                                            //
                                        }

                                    }
                                    //
                                }

                                if (!it.isSuccessful) {
                                    //dismiss the pg
                                    progressDg.dismiss()
                                    //alert user of url failure
                                    AlertDialog.Builder(this@ProductsHome)
                                        .setMessage(it.exception?.message)
                                        .setIcon(R.drawable.ic_warning).create().show()
                                    //
                                }

                                //code ends

                            }
                    }
                }
            }

        } catch (e: Exception) {
            //toast the error since the process of compression did not succeed
            funToastyFail("something went wrong!")
            //return home products
            startActivity(
                Intent(
                    this@ProductsHome, ProductsHome::class.java
                )
            )
        }
    }


    private fun funUpdateStoreImageUrl(
        urlDownloadUrlString: String, keyImageUri: String, progressDg: ProgressDialog
    ) {
        //code begins2
        val mapData = hashMapOf(keyImageUri to urlDownloadUrlString)
        //starting the process of image update fStore
        val fabInstanceFireStore = FirebaseAuth.getInstance()
        val currentUID = fabInstanceFireStore.currentUser?.uid
        val fStoreUsers = FirebaseFirestore.getInstance()

        if (currentUID != null) {
            fStoreUsers.collection(Registration.ComradeUser).document(currentUID)
                .update(mapData as Map<String, String>).addOnCompleteListener {

                    if (it.isSuccessful) {
                        //code begins
                        progressDg.setMessage("congratulations")
                        //begin the process of updating the image also in the products
                        funUpdateTheImagePublicRepo(currentUID, progressDg, urlDownloadUrlString)
                        //code ends

                    } else if (!it.isSuccessful) {
                        //progress dialog dismiss
                        progressDg.dismiss()
                        //
                        //code begins
                        //alert the user and notify of the failure
                        AlertDialog.Builder(this@ProductsHome).setMessage(it.exception?.message)
                            .setIcon(R.drawable.ic_warning).create().show()
                        //code ends
                        //code ends

                    }

                }
        }
        //code ends

    }

    private fun funUpdateTheImagePublicRepo(
        currentUID: String, progressDg: ProgressDialog, urlDownloadUrlString: String
    ) {

        //code begins
        //update the image of the items that are related to the current user
        val storeProductsPublicRepo = FirebaseFirestore.getInstance()
        storeProductsPublicRepo.collection(ModalPostProducts.CollectionPost).get().addOnCompleteListener {
            if (it.isSuccessful) {
                progressDg.setMessage("finishing")
                //code begins
                //successfully fetched the data begin the process of updating the owner image on products related
                val documents = it.result
                //loop through using the for each loop
                for (doc in documents) {
                    val dataIds = doc.id
                    //relate the ids of the image and check out if the returned ids match those of the products
                    //update their image uri appropriately
                    if (dataIds.contains(currentUID)) {
                        funUpdateOwnerImageOnProducts(
                            storeProductsPublicRepo,
                            dataIds,
                            progressDg,
                            urlDownloadUrlString,
                            currentUID
                        )
                    }
                }
                //code ends

            } else if (!it.isSuccessful) {
                //alert failure occurred
                val alertFail = MaterialAlertDialogBuilder(this@ProductsHome)
                alertFail.setTitle("failed")
                alertFail.setMessage("an error occurred while trying to update the image try again later")
                alertFail.setCancelable(false)
                alertFail.setPositiveButton("okay") { dialog, _ ->
                    //dismiss the alert dialog and then restart the products home activity
                    dialog.dismiss()
                    //
                    startActivity(Intent(this@ProductsHome, ProductsHome::class.java))
                    //
                }
                alertFail.create()
                alertFail.show()
            }
        }
        //code ends
    }

    private fun funUpdateOwnerImageOnProducts(
        storeProductsPublicRepo: FirebaseFirestore,
        dataIds: String,
        progressDg: ProgressDialog,
        urlDownloadUrlString: String,
        currentUID: String
    ) {
        //code begins
        val keyImageUriOwner = "imageOwner"
        //mapping the the data on the hashMap
        val mapData = hashMapOf(keyImageUriOwner to urlDownloadUrlString)
        storeProductsPublicRepo.collection(ModalPostProducts.CollectionPost).document(dataIds)
            .update(mapData as Map<String, Any>).addOnCompleteListener {

                if (it.isSuccessful) {
                    //dismiss the progress dialog and then start activity to home products
                    //update the private/personal repo file image of the owner on the products
                    funUpdateOwnerImageOnPrivateRepo(
                        progressDg, currentUID, mapData
                    )
                    //
                    startActivity(Intent(this@ProductsHome, ProductsHome::class.java))
                } else if (!it.isSuccessful) {
                    //dismiss the progress
                    progressDg.dismiss()
                    //toast error
                    funToastyFail("something went wrong")
                    //failure occurred while updating the image thus end the process by back to the progress activity
                    startActivity(Intent(this@ProductsHome, ProductsHome::class.java))
                    //
                    return@addOnCompleteListener
                }
            }
        //code ends
    }

    private fun funUpdateOwnerImageOnPrivateRepo(
        progressDg: ProgressDialog, currentUID: String, mapData: HashMap<String, String>
    ) {
        //code begins
        val storePrivateRepo = FirebaseFirestore.getInstance()
        storePrivateRepo.collection(currentUID).get().addOnCompleteListener {
            if (it.isSuccessful) {
                //code begins
                val documents = it.result
                for (doc in documents) {
                    val dataIds = doc.id
                    if (dataIds.contains(currentUID)) {
                        storePrivateRepo.collection(currentUID).document(dataIds)
                            .update(mapData as Map<String, Any>).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    //code begins
                                    progressDg.dismiss()
                                    //return home products
                                    startActivity(
                                        Intent(
                                            this@ProductsHome, ProductsHome::class.java
                                        )
                                    )
                                    //code ends
                                } else if (!it.isSuccessful) {
                                    //code begins
                                    //dismiss the progress
                                    progressDg.dismiss()
                                    //toast error
                                    funToastyFail("something went wrong")
                                    //failure occurred while updating the image thus end the process by back to the progress activity
                                    startActivity(
                                        Intent(
                                            this@ProductsHome, ProductsHome::class.java
                                        )
                                    )
                                    //
                                    return@addOnCompleteListener
                                    //code ends
                                }
                            }
                    }
                }
                //code ends
            } else if (!it.isSuccessful) {
                //dismiss the progress
                progressDg.dismiss()
                //toast error
                funToastyFail("something went wrong")
                //failure occurred while updating the image thus end the process by back to the progress activity
                startActivity(Intent(this@ProductsHome, ProductsHome::class.java))
                //
                return@addOnCompleteListener
            }
        }
        //code ends
    }

    //function Toasty Fail
    private fun funToastyFail(message: String) {
        Toasty.error(
            this@ProductsHome,
            message,
            Toasty.LENGTH_SHORT,
        ).show()
    }

    //function Toasty Successful
    private fun funToastyShow(s: String) {
        Toasty.success(
            this@ProductsHome,
            s,
            Toasty.LENGTH_SHORT,
        ).show()
    }

    private fun createNotificationAlertBadges(id: Int, number_badges: Int) {
        val badge = bottomNav.getOrCreateBadge(id)
        if (number_badges == 0) {
            //invisible the badge since no notifications are present
            badge.isVisible = false
        } else {
            //number of notifications are greater than zero thus let the badge become visible
            badge.isVisible = true
            badge.backgroundColor = resources.getColor(R.color.transparrent)
            badge.badgeTextColor = resources.getColor(R.color.purple_500)
            badge.number = number_badges
        }
    }
    
    
    
}