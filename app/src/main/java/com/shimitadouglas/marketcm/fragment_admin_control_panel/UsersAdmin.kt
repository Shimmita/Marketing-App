package com.shimitadouglas.marketcm.fragment_admin_control_panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shimitadouglas.marketcm.R

class UsersAdmin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //code begins
        val viewUsersAdmin = inflater.inflate(R.layout.users_admin, container, false)


        //
        return viewUsersAdmin
        //code ends
    }
}