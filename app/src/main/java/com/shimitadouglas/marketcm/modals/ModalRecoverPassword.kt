package com.shimitadouglas.marketcm.modals

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.shimitadouglas.marketcm.R

class ModalRecoverPassword : BottomSheetDialogFragment() {
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
        viewAttach = inflater.inflate(R.layout.layout_passcode_rec, container, false)
        btnRec = viewAttach.findViewById(R.id.btnRecover)
        linearRecModal = viewAttach.findViewById(R.id.linearBottomSheetRecovery)
        emailInputRec = viewAttach.findViewById(R.id.emailRecovery)
        //call func animate the parent
        funAnimateParent()
        //

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
        //

        return viewAttach
    }

    private fun funStartRecovery(emailDataRecovery: String) {
        //code begin
        Toast.makeText(requireActivity(), "Recovery", Toast.LENGTH_SHORT).show()
        //code ends
    }

    private fun funAnimateParent() {
        //code begin
        val modalPassRecController = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                this.requireActivity(),
                R.anim.rotate
            )
        )
        modalPassRecController.delay = 0.5f

        //setting the controller on to the view modal parent
        linearRecModal.layoutAnimation = modalPassRecController
        //starting the view changing instance of rotation by animation
        linearRecModal.startLayoutAnimation()
        //
        //
        //code end

    }

}