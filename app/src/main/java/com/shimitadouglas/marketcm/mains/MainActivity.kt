package com.shimitadouglas.marketcm.mains

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modals.ModalRecoverPassword

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    //globals declaration
    lateinit var relativeLoginParent: RelativeLayout
    lateinit var tvRegistration: TextView
    lateinit var btnLogin: MaterialButton
    lateinit var editLoginEmail: TextInputEditText
    lateinit var editLoginPassword: TextInputEditText
    lateinit var tvRecoverPasscode: TextView
    lateinit var linearRec: LinearLayout


    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //call function fullscreen
        funFullScreen()
        //
        //call function init of the globals
        funInitGlobals()
        //

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
                funNowLogin(enteredEmail, enteredPassword)
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
            linearRec.startAnimation(
                AnimationUtils.loadAnimation(
                    this@MainActivity,
                    R.anim.abc_slide_in_bottom
                )
            )
            //
            linearRec.postDelayed(Runnable {
                //begin the operation of modal show password recovery
                val holderModalPasscodeRecModal = ModalRecoverPassword()
                holderModalPasscodeRecModal.show(supportFragmentManager, "fragment_modal_pass_rec")
                //

            }, 500)
            //
            //code ends
        }
        //

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funStartOperationLoginNow(enteredEmail: Editable, enteredPassword: Editable?) {
        //create progress dialog to show login progress
        val progressD = ProgressDialog(this@MainActivity)
        progressD.setCancelable(false)
        progressD.setTitle("Login")
        progressD.setMessage("verifying...")
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
                    //
                    //intent migration to the activity products
                    Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_LONG).show()
                    //intent migration here
                    val intentProductsHome = Intent(this@MainActivity, ProductsHome::class.java)
                    intentProductsHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intentProductsHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    //start the activity of intent migration to products home
                    this@MainActivity.startActivity(intentProductsHome)
                    //
                    //finish
                    finish()
                    //
                    //
                }
                //login failed
                else if (!it.isSuccessful) {
                    //dismiss the progress
                    progressD.dismiss()

                    //alert error of login
                    val alert_dialog = MaterialAlertDialogBuilder(this@MainActivity)
                    alert_dialog.setTitle("Login Failed")
                    alert_dialog.setIcon(R.drawable.ic_warning)
                    alert_dialog.setMessage(it.exception?.message)
                    alert_dialog.background =
                        resources.getDrawable(R.drawable.general_alert_dg, theme)
                    alert_dialog.show()
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
        alert_dialog.setTitle(getString(R.string.passwarn))
        alert_dialog.setIcon(R.drawable.ic_warning)
        alert_dialog.setMessage(getString(R.string.messge_pass_warn))
        alert_dialog.background = resources.getDrawable(R.drawable.general_alert_dg, theme)
        alert_dialog.show()
        //code end

    }

    private fun funInitGlobals() {
        //code begins
        relativeLoginParent = findViewById(R.id.relativeMainLogin)
        tvRegistration = findViewById(R.id.tv_register_account)
        btnLogin = findViewById(R.id.buttonLogin)
        editLoginEmail = findViewById(R.id.loginEmail)
        editLoginPassword = findViewById(R.id.loginPassword)
        tvRecoverPasscode = findViewById(R.id.tv_recover_password)
        linearRec = findViewById(R.id.linearRecovery)
        //parent animate
        val layAnimControl =
            LayoutAnimationController(AnimationUtils.loadAnimation(this@MainActivity, R.anim.down))
        layAnimControl.apply {
            delay = 0.35f
            order = LayoutAnimationController.ORDER_NORMAL
        }

        //applying the anim on parent
        relativeLoginParent.layoutAnimation = layAnimControl
        relativeLoginParent.startLayoutAnimation()
        //code ends
    }

    private fun funFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
    }

}