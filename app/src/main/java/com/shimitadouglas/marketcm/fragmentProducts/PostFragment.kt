package com.shimitadouglas.marketcm.fragmentProducts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.modal_sheets.ModalMyPostManager
import com.shimitadouglas.marketcm.modal_sheets.ModalPostProducts

class PostFragment : Fragment() {
    //init of the globals
    lateinit var viewPost: View
    lateinit var gridLayout: LinearLayout
    lateinit var cardViewPostProduct: CardView
    lateinit var cardViewMyRecentPost: CardView
    lateinit var cardViewUpdatePost: CardView
    lateinit var cardViewDeletePost: CardView
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        //init of the view
        viewPost = inflater.inflate(R.layout.post_fragment, container, false)
        //call function init globals
        funInitGlobals()
        //animate parent using fun
        funAnimateParent()
        //anim cards onclick using function
        funAnimOnclickCards()
        //
        return viewPost

        //code ends
    }

    private fun funAnimOnclickCards() {
        //code begins

        //card post
        cardViewPostProduct.setOnClickListener {
            //anim the card for sometime before operations
            cardViewPostProduct.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.bottom_up
                )
            )
            //delay before operations
            cardViewPostProduct.postDelayed({

                //fun modal sheet posting operations
                funAlertUserHowPosting()
                //

            }, 400)
            //


        }
        //
        //card myRecent post
        cardViewMyRecentPost.setOnClickListener {
            //code begins
            //anim the  card before operations
            cardViewMyRecentPost.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.bottom_up
                )
            )
            //
            cardViewMyRecentPost.postDelayed(Runnable {
                //define  myRecent operation here
                //fun show modal sheet
                val section="views"
                funShowModalSheetMyPostsOp(section)
                //
            }, 450)
            //


            //code ends

        }

        //card update post
        cardViewUpdatePost.setOnClickListener {
            //code begins
            //animate the card before operations
            cardViewUpdatePost.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.bottom_up
                )
            )
            //delay before operations
            cardViewUpdatePost.postDelayed(Runnable {

                //perform the update operation here
                //call fun show modal
                val section = "update"
                funShowModalSheetUpdateOp(section)
                //

            }, 450)
            //


            //code ends

        }

        //card delete
        cardViewDeletePost.setOnClickListener {
            //code begins
            //animate the card before operations
            cardViewDeletePost.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.bottom_up
                )
            )
            //delay before the actual operations
            cardViewDeletePost.postDelayed({
                //perform the actual delete operations here
                //call fun to load and show modal sheet
                val section = "delete"
                funShowModalSheetDeleteOp(section)
                //
            }, 450)
            //

            //code ends

        }


        //code ends
    }

    private fun funShowModalSheetMyPostsOp(section: String) {
        //code begins
        val modalMyPosts = ModalMyPostManager(section)
        modalMyPosts.show(this.childFragmentManager, "modal_my_post_manager")
        //code ends
    }

    private fun funShowModalSheetUpdateOp(section: String) {
        //code begins
        val modalUpdate = ModalMyPostManager(section)
        modalUpdate.show(this.childFragmentManager, "modal_my_post_manager")
        //code ends
    }

    private fun funShowModalSheetDeleteOp(section: String) {
        //code begins
        val modalDelete = ModalMyPostManager(section)
        modalDelete.show(this.childFragmentManager, "modal_my_post_manager")
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funAlertUserHowPosting() {
        //code begins
        val alertPostingHint = MaterialAlertDialogBuilder(requireActivity())
        alertPostingHint.setTitle("How To Post")
        alertPostingHint.background =
            resources.getDrawable(R.drawable.material_seven, requireActivity().theme)
        alertPostingHint.setMessage(
            "With Market CM, posting a product to the online market involves:\n" +
                    "\n1.providing an image of an item(product)\n\n2.providing the name(title) of the product\n\n3.providing description of the product i.e condition of the " +
                    "product\n\n4.providing the price of the product.the price shouldn't be too high (negotiable is the price)"
        )

        alertPostingHint.setIcon(R.drawable.cart)
        alertPostingHint.setPositiveButton("begin") { dialog, _ ->
            //call function to start uploading of the post
            funBeginPosting()
            //
            //dismiss the dialog to avoid RT Exceptions
            dialog.dismiss()
            //
        }

        alertPostingHint.setNeutralButton("cancel") { dialog, _ ->
            //dismiss the dialog
            //anim card post of shaking
            cardViewPostProduct.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.shake
                )
            )
            //
            dialog.dismiss()
            //
        }

        alertPostingHint.setCancelable(false)
        alertPostingHint.create()
        alertPostingHint.show()
        //code ends

    }

    private fun funBeginPosting() {
        //code begins
        //display modal sheet that will be invoked which contains all views for the posting
        val displayPostingModal = ModalPostProducts()
        displayPostingModal.show(this.childFragmentManager, "modal_posting")
        //
        //code ends
    }

    private fun funAnimateParent() {
        //code begins
        val layoutAnimationController = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                requireActivity(),
                R.anim.bottom_up
            )
        )
        layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        layoutAnimationController.delay = 0.5f
        gridLayout.layoutAnimation = layoutAnimationController
        gridLayout.startLayoutAnimation()

        //code ends
    }

    private fun funInitGlobals() {
        //code begins
        //update the tile of the fragment
        val title = "Post Products"
        updateTitle(title)
        //
        gridLayout = viewPost.findViewById(R.id.linearPostParent)
        cardViewPostProduct = viewPost.findViewById(R.id.cardPostProducts)
        cardViewMyRecentPost = viewPost.findViewById(R.id.cardRecentPosts)
        cardViewUpdatePost = viewPost.findViewById(R.id.cardUpdatePost)
        cardViewDeletePost = viewPost.findViewById(R.id.cardDeletePost)
        //code ends
    }

    private fun updateTitle(title: String) {
        requireActivity().title = title
    }
}