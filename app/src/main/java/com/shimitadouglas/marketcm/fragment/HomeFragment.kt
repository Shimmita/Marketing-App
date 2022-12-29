package com.shimitadouglas.marketcm.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shimitadouglas.marketcm.R

class HomeFragment : Fragment() {

    //init of the global
    private lateinit var viewHome: View
    lateinit var toolbarHome: Toolbar

    //lateinit var recyclerViewHome: RecyclerView
    lateinit var collapsingToolbarLayoutHome: CollapsingToolbarLayout
    lateinit var appBarLayoutHome: AppBarLayout
    lateinit var floatingActionButtonHome: FloatingActionButton
    lateinit var coordinatorLayout: CoordinatorLayout
    //


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //code begins
        //init of the view
        viewHome = inflater.inflate(R.layout.home_fragment, container, false)
        //init of collapsing toolbar and other globals
        functionInit()
        //setting listener on the appbar using a function
        funListenerAppBarHome()
        //setting listener on the fab
        funFabListener()
        //
        //
        return viewHome
        //code ends
    }

    private fun funFabListener() {
        //code begins
        floatingActionButtonHome.setOnClickListener {
            //code begins
            //fab self anim
            floatingActionButtonHome.startAnimation(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.push_left_out
                )
            )
            //

            //appbar animate
            val homeAppBarContentRefreshAnim =
                LayoutAnimationController(
                    AnimationUtils.loadAnimation(
                        requireActivity(),
                        R.anim.bottom_up
                    )
                )
            homeAppBarContentRefreshAnim.delay = 2.0f
            homeAppBarContentRefreshAnim.order = LayoutAnimationController.ORDER_REVERSE
            appBarLayoutHome.layoutAnimation = homeAppBarContentRefreshAnim
            appBarLayoutHome.startLayoutAnimation()
            //

            //start fab functionality here after delay elapse
            floatingActionButtonHome.postDelayed(Runnable {
                //code functionality herein
                Toast.makeText(requireActivity(), "fab clicked", Toast.LENGTH_SHORT).show()
                //code ends

            }, 450)
            //end of fab functionality

            //code ends
        }
        //code ends
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun funListenerAppBarHome() {
        //code begins
        var scrollRange = -1
        appBarLayoutHome.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset == 0) {
                //collapsing toolbar fully collapsed and the toolbar should be given title
                collapsingToolbarLayoutHome.title = "hot products"
                //
                toolbarHome.background =
                    resources.getDrawable(R.drawable.material_four, requireActivity().theme);
                //

            } else {
                //the collapsing toolbar is expanded hence no ned to display the title text
                collapsingToolbarLayoutHome.title = ""
                toolbarHome.background = null
                //

            }
        }
        //code ends
    }

    @SuppressLint("PrivateResource")
    private fun functionInit() {
        //code  begins
        //call function to update the title accordingly
        val title = "Home"
        updateTitle(title)
        //init of variables and globals
        toolbarHome = viewHome.findViewById(R.id.toolbarHome)
        //recyclerViewHome = viewHome.findViewById(R.id.recyclerViewHomeProducts)
        collapsingToolbarLayoutHome = viewHome.findViewById(R.id.collapsingToolBarHome)
        appBarLayoutHome = viewHome.findViewById(R.id.appbarHome)
        floatingActionButtonHome = viewHome.findViewById(R.id.fabHome)
        coordinatorLayout = viewHome.findViewById(R.id.coordinatorHome)
        //

        //parent animate
        val parentLayoutHomeAnim =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    requireActivity(),
                    R.anim.bottom_up
                )
            )
        parentLayoutHomeAnim.delay = 0.5f
        parentLayoutHomeAnim.order = LayoutAnimationController.ORDER_REVERSE
        coordinatorLayout.layoutAnimation = parentLayoutHomeAnim
        coordinatorLayout.startLayoutAnimation()
        //


        //code ends

    }

    private fun updateTitle(title: String) {
        requireActivity().title = title
    }


}