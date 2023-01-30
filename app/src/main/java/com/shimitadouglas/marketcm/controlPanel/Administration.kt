package com.shimitadouglas.marketcm.controlPanel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.fragment_admin.MessageAdmin
import com.shimitadouglas.marketcm.fragment_admin.PostsAdmin
import com.shimitadouglas.marketcm.fragment_admin.UsersAdmin

class Administration : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administration)
        //code begins
        //init
        funInit()
        //
        //load the default fragment
        updateFragment(MessageAdmin(), "messageAdmin")
        //

        //setting listener on the bottom nav
        bottomNav.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.messagesAdmin -> {
                    updateFragment(MessageAdmin(), "messagesAdmin")
                }

                R.id.usersAdmin -> {
                    updateFragment(UsersAdmin(), "usersAdmin")
                }

                R.id.postsAdmin -> {
                    updateFragment(PostsAdmin(), "postsAdmin")
                }

            }

            return@setOnNavigationItemSelectedListener true
        }
        //

        //code ends
    }

    private fun funInit() {
        //code begins
        bottomNav = findViewById(R.id.bottomNavAdmin)
        //code ends
    }

    private fun updateFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContainerAdmin, fragment, tag).commitNow()

    }
}