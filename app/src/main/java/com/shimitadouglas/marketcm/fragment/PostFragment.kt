package com.shimitadouglas.marketcm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shimitadouglas.marketcm.R

class PostFragment : Fragment() {
    //init of the globals
    lateinit var viewPost: View

    //
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        //init of the view
        viewPost = inflater.inflate(R.layout.post_fragment, container, false)
        //update the tile of the fragment
        val title = "Post Products"
        updateTitle(title)

        //
        return viewPost
        //code ends
    }

    private fun updateTitle(title: String) {
        requireActivity().title = title
    }
}