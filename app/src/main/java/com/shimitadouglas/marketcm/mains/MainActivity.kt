package com.shimitadouglas.marketcm.mains

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.NestedScrollView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_sheets.ModalRecoverPassword
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    //globals declaration
    private lateinit var relativeLoginParent: RelativeLayout
    lateinit var tvRegistration: AppCompatButton
    lateinit var btnLogin: AppCompatButton
    lateinit var editLoginEmail: TextInputEditText
    lateinit var editLoginPassword: TextInputEditText
    lateinit var tvRecoverPasscode: AppCompatButton
    lateinit var linearRec: LinearLayout
    lateinit var checkBox: CheckBox
    lateinit var textViewMove: TextView
    lateinit var nestedScrollViewMain: NestedScrollView
    //

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //clear sign out the current user
        FirebaseAuth.getInstance().signOut()
        //clear all the data saved in the shared preference
        val sharedPreferences =
            getSharedPreferences(ProductsHome.sharedPreferenceName, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()


        //call function init of the globals
        funInitGlobals()
        //setting onclick listener on button and tv
        btnLogin.setOnClickListener {


            //defining variables to hold email data and password data
            val enteredEmail = editLoginEmail.text
            val enteredPassword = editLoginPassword.text

            //checking legitimacy of input from email and password input
            if (TextUtils.isEmpty(enteredEmail)) {
                editLoginEmail.error = "please,this field cannot be empty!"
            } else if (TextUtils.isEmpty(enteredPassword)) {
                editLoginPassword.error = "please,this field cannot be empty!"
            } else if (enteredEmail?.length!! > 30) {
                editLoginEmail.error = "email is too long!"
            } else if (enteredPassword?.length!! < 6) {
                //call function display alert dialog informing password length is too short (min 8)
                funDisplayAlertDialogPassword()
                //
            }
            //everything ok lets begin login
            else {
                btnLogin.apply {
                    //animate the btn within 2.5 sec
                    startAnimation(
                        AnimationUtils.loadAnimation(
                            this@MainActivity,
                            R.anim.push_right_in
                        )
                    )
                    //delay fun post
                    postDelayed({
                        //begin login operations
                        funNowLogin(enteredEmail, enteredPassword)
                        //
                    }, 500)
                }
            }
            //

        }

        //setting onclick on the tv register
        tvRegistration.setOnClickListener {

            //start intent migration to account registration and anim too
            linearRec.startAnimation(
                AnimationUtils.loadAnimation(
                    this@MainActivity,
                    R.anim.abc_slide_in_bottom
                )
            )
            //begin the intent migration to effect visualisation
            //
            tvRegistration.postDelayed(Runnable {
                val intentAccountCreate = Intent(this@MainActivity, Registration::class.java)
                intentAccountCreate.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intentAccountCreate.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentAccountCreate)
                //finish
                finish()
            }, 500)
            //
        }

        //setting onclick on tvRecover
        tvRecoverPasscode.setOnClickListener {
            //code begins
            linearRec.apply {
                startAnimation(
                    AnimationUtils.loadAnimation(
                        this@MainActivity,
                        R.anim.abc_slide_in_bottom
                    )
                )
                postDelayed({
                    //begin the operation of modal show password recovery
                    val holderModalPasscodeRecModal = ModalRecoverPassword()
                    holderModalPasscodeRecModal.show(
                        supportFragmentManager,
                        "fragment_modal_pass_rec"
                    )
                    //
                }, 500)
            }
            //code ends
        }

        //setting listener on checkbox
        checkBox.setOnCheckedChangeListener { _, b ->

            val viewRecoverCreate: LinearLayout = findViewById(R.id.linearRecoverCreate)

            if (b) {
                //is checked show the more account info
                viewRecoverCreate.apply {
                    visibility = View.VISIBLE

                }
                //
            } else {
                //hide the more account info
                viewRecoverCreate.apply {
                    visibility = View.GONE

                }
                //
            }

        }


        //code ends
    }


    private fun funNowLogin(enteredEmail: Editable?, enteredPassword: Editable?) {
        if (enteredEmail != null) {
            if (!enteredEmail.contains("@gmail.com")) {
                Toast.makeText(
                    this@MainActivity,
                    "detected invalid email address",
                    Toast.LENGTH_LONG
                ).show()
                //setting the error enter a valid email
                editLoginEmail.error = "enter a valid email address!"
                //

            } else {
                //call function to ready Start Login 101
                funStartOperationLoginNow(enteredEmail, enteredPassword)
                //
            }
        }
    }

    @Suppress("Deprecation")
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funStartOperationLoginNow(enteredEmail: Editable, enteredPassword: Editable?) {
        //create progress dialog to show login progress
        val progressD = ProgressDialog(this@MainActivity)
        progressD.setCancelable(false)
        progressD.setTitle("Login")
        progressD.setMessage("verifying")
        progressD.create()
        progressD.show()
        //

        //code begins
        //creating firebase instance from the auth variable
        val faAuth = FirebaseAuth.getInstance()
        faAuth.signInWithEmailAndPassword(enteredEmail.toString(), enteredPassword.toString())
            .addOnCompleteListener {

                //login was a success
                if (it.isSuccessful) {
                    //dismiss the progress
                    progressD.dismiss()
                    //intent migration to the activity products
                    Toasty.success(
                        this@MainActivity,
                        "successful",
                        Toasty.LENGTH_LONG
                    ).show()
                    //intent migration here
                    val intentProductsHome = Intent(this@MainActivity, ProductsHome::class.java)
                    intentProductsHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intentProductsHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    //start the activity of intent migration to products home
                    this@MainActivity.startActivity(intentProductsHome)
                    finishAffinity()
                    //
                }
                //login failed
                else if (!it.isSuccessful) {
                    //dismiss the progress
                    progressD.dismiss()

                    //alert error of login
                    val alertDialog = MaterialAlertDialogBuilder(this@MainActivity)
                    alertDialog.setTitle("Login Failed")
                    alertDialog.setIcon(R.drawable.ic_warning)
                    alertDialog.setMessage(it.exception?.message)
                    alertDialog.background =
                        resources.getDrawable(R.drawable.general_alert_dg, theme)
                    alertDialog.show()
                    //code end
                    //
                }
            }
        //code ends
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funDisplayAlertDialogPassword() {
        //code start
        val alert_dialog = MaterialAlertDialogBuilder(this@MainActivity)
        alert_dialog.setTitle(getString(R.string.pass_warn))
        alert_dialog.setIcon(R.drawable.ic_warning)
        alert_dialog.setMessage(getString(R.string.message_pass_warn))
        alert_dialog.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alert_dialog.show()
        //code end

    }

    private fun funInitGlobals() {
        //code begins
        nestedScrollViewMain = findViewById(R.id.nestedMainLogin)
        relativeLoginParent = findViewById(R.id.relativeMainLogin)
        tvRegistration = findViewById(R.id.tv_register_account)
        btnLogin = findViewById(R.id.buttonLogin)
        editLoginEmail = findViewById(R.id.edtLoginEmail)
        editLoginPassword = findViewById(R.id.edtLoginPassword)
        tvRecoverPasscode = findViewById(R.id.tv_recover_password)
        linearRec = findViewById(R.id.linearRecoverCreate)
        checkBox = findViewById(R.id.cbMoreAccountInfo)
        textViewMove = findViewById(R.id.marqueeLogin)
        //parent animate
        val layAnimControl =
            LayoutAnimationController(AnimationUtils.loadAnimation(this@MainActivity, R.anim.down))
        layAnimControl.apply {
            order = LayoutAnimationController.ORDER_NORMAL
        }

        //applying the anim on parent
        relativeLoginParent.layoutAnimation = layAnimControl
        relativeLoginParent.startLayoutAnimation()
        //code ends

        //apply moveMaq textLogin
        textViewMove.setSingleLine()
        textViewMove.ellipsize = TextUtils.TruncateAt.MARQUEE
        textViewMove.marqueeRepeatLimit = -1
        textViewMove.isSelected = true
        //


        //trigger anim gradient on the parent nested scroll view
        val animationDrawableMainLogin = nestedScrollViewMain.background as AnimationDrawable
        animationDrawableMainLogin.apply {
            setEnterFadeDuration(5000)
            setExitFadeDuration(5000)
            start()
        }
        //
    }
}