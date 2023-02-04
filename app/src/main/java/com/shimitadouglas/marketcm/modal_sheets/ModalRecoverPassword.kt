package com.shimitadouglas.marketcm.modal_sheets

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.notifications.NormalNotification
import es.dmoral.toasty.Toasty

class ModalRecoverPassword : BottomSheetDialogFragment() {
    //progressD email
    lateinit var progressDialogEmailVerification: ProgressDialog

    //
    //init of the globals
    lateinit var viewAttach: View
    lateinit var btnRec: MaterialButton
    lateinit var linearRecModal: LinearLayout
    lateinit var emailInputRec: TextInputEditText
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init the view as the first element and other globals
        viewAttach = inflater.inflate(R.layout.layout_passcode_rec, container, false) as View
        btnRec = viewAttach.findViewById(R.id.btnRecover)
        linearRecModal = viewAttach.findViewById(R.id.linearBottomSheetRecovery)
        emailInputRec = viewAttach.findViewById(R.id.emailRecovery)

        //setting onclick to the button
        btnRec.setOnClickListener {

            //check the legitimacy of the data entry email option
            val emailDataRecovery = emailInputRec.text.toString()
            if (TextUtils.isEmpty(emailDataRecovery)) {
                emailInputRec.error = "empty field not allowed!"
            } else if (!emailDataRecovery.contains("@gmail.com")) {
                emailInputRec.error = "email address invalid try xyz@gmail.com"
            } else {
                //call function to start recovery everything ok fine
                funStartRecovery(emailDataRecovery)
                //
            }
        }
        //call func animate the parent
        funAnimateParent()
        //
        //funInit progressD
        funInitProgressD()
        //

        return viewAttach
    }

    private fun funInitProgressD() {
        //code begins
        val viewProgress = LayoutInflater.from(requireActivity())
            .inflate(R.layout.general_progress_dialog_view, null, false)
        progressDialogEmailVerification = ProgressDialog(requireActivity())
        progressDialogEmailVerification.setTitle("password reset")
        progressDialogEmailVerification.setCancelable(false)
        progressDialogEmailVerification.setMessage("sending request")
        progressDialogEmailVerification.setView(viewProgress)
        progressDialogEmailVerification.setIcon(R.drawable.cart)
        //code ends
    }

    private fun funStartRecovery(emailDataRecovery: String) {
        //show the progress dialog
        progressDialogEmailVerification.create()
        progressDialogEmailVerification.show()
        //
        //code begin
        val firebaseRequestChangePassword = FirebaseAuth.getInstance()
        //send the password reset email to the email being reset
        firebaseRequestChangePassword.sendPasswordResetEmail(emailDataRecovery)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //dismiss the progressD
                    progressDialogEmailVerification.dismiss()
                    //
                    //successfully sent password reset email
                    funAlertSuccessEmailResetPasswordSent(emailDataRecovery)
                    //
                } else if (!it.isSuccessful) {
                    //dismiss the progressD
                    progressDialogEmailVerification.dismiss()
                    //
                    //password reset email failure
                    funToastyFail("failed to send password reset email!")
                    //
                }

            }
        //code ends
    }

    private fun funAlertSuccessEmailResetPasswordSent(emailDataRecovery: String) {
        //code begins
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("password reset email")
            .setIcon(R.drawable.ic_info)

            .setMessage(
                "password reset link has been sent to\n" +
                        "($emailDataRecovery)\n\ncheck it in your email inbox to reset forgotten account password\n" +
                        "sometimes the email may not be present in the inbox." +
                        "\n\nif that is the case check it in the\n" +
                        "spam folder of your gmail application."
            )
            .setCancelable(false)
            .setPositiveButton("ok") { dialog, _ ->

                //show notification of email sent to the gmail account
                val normalNotification = NormalNotification(
                    requireActivity(),
                    "Password Reset Email",
                    "password reset email sent to $emailDataRecovery",
                    R.drawable.cart
                )
                normalNotification.funCreateNotification()
                //

                //dismiss the dialog
                dialog.dismiss()
                //
            }
            .create()
            .show()

        //code ends
    }

    private fun funAnimateParent() {
        //code begin
        val modalPassRecController = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                this.requireActivity(),
                R.anim.rotate_avg
            )
        )
        modalPassRecController.delay = 0.2f

        //setting the controller on to the view modal parent
        linearRecModal.layoutAnimation = modalPassRecController
        //starting the view changing instance of rotation by animation
        linearRecModal.startLayoutAnimation()
        //
        //code end

    }

    //function Toasty Fail
    private fun funToastyFail(message: String) {
        Toasty.custom(
            requireActivity(),
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
            requireActivity(),
            s,
            R.drawable.ic_nike_done,
            R.color.colorWhite,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }

}