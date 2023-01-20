package com.shimitadouglas.marketcm.fragment_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shimitadouglas.marketcm.R

class PostsAdmin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //code begins
        val viewPosts = inflater.inflate(R.layout.posts_admin, container, false)


        //
        return viewPosts
        //code ends
    }
}