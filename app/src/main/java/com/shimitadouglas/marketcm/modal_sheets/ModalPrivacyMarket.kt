package com.shimitadouglas.marketcm.modal_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shimitadouglas.marketcm.R

class ModalPrivacyMarket(var which: String) : BottomSheetDialogFragment() {
    companion object {
        private const val TAG = "ModalPrivacyMarket"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.layout_modal_policy_about, container, false)
        val textView: TextView = view.findViewById(R.id.tvDataMarketDetails)
        val cardView:CardView=view.findViewById(R.id.cardMarketDetails)
        val linearLayout:LinearLayout=view.findViewById(R.id.linearParentDetails)


        if (which.equals("about", true)) {
            //about data
            textView.text = resources.getText(R.string.about_market)

        } else if (which.contains("policy")) {
            //policy/terms data
            textView.text =resources.getString(R.string.privacy_policy_terms)
        }
        //fun anim parent card
        funCardAnim(cardView,linearLayout)
        //

        return view
    }

    private fun funCardAnim(cardView: CardView, linearLayout: LinearLayout,) {
        //code begins
        val layoutAnim=LayoutAnimationController(AnimationUtils.loadAnimation(requireActivity(),R.anim.yobounce))
        layoutAnim.apply {
            delay=0.5f
            order=LayoutAnimationController.ORDER_REVERSE
            cardView.layoutAnimation=layoutAnim
            cardView.startLayoutAnimation()
        }

        //animate the icon separately 2.5sec delay
        linearLayout.postDelayed({
               val layoutAnim=LayoutAnimationController(AnimationUtils.loadAnimation(requireActivity(),R.anim.push_left_in))
            layoutAnim.apply {
                order=LayoutAnimationController.ORDER_REVERSE
                delay=0.5f
                linearLayout.layoutAnimation=layoutAnim
                linearLayout.startLayoutAnimation()
            }

        },2000)
        //code ends
    }

    private fun returnPrivacyAndPolicy(): String {
        var message = ""
        return message
    }

    private fun returnAboutMarketCM(): String {
        var messageAbout = ""
        return messageAbout
    }
}