package com.shimitadouglas.marketcm.controlPanel

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.fragment_admin.*
import es.dmoral.toasty.Toasty

class Administration : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView
    lateinit var toolbarAdministration: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administration)
        //code begins
        funInit()
        //load the default fragment is reports
        updateFragment(ReportsAdmin(), "messageAdmin")
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
                R.id.counterfeitReports -> {
                    updateFragment(ReportsAdmin(), "reportsAdmin")
                }

            }

            return@setOnNavigationItemSelectedListener true
        }
        //
        //check intent data
        funCheckIntentData()
        //
        //code ends
    }

    private fun funCheckIntentData() {
        //code begins
        val dataIntentFragmentMigration: String? = intent.getStringExtra("migration")
        val dataProductControlID: String? = intent.getStringExtra("productControlID")
        val dataVictimID: String? = intent.getStringExtra("victimID")
        val dataSuspectID: String? = intent.getStringExtra("suspectID")
        if (dataIntentFragmentMigration != null||dataVictimID!=null||dataSuspectID!=null||dataProductControlID!=null) {
            if (dataIntentFragmentMigration.equals("fragment_details", true)) {
                //migrate to fragment details
                updateFragment(DetailsReport(dataProductControlID,dataVictimID,dataSuspectID), "fragment_details")
                //

            }
        }

        //code ends
    }

    private fun funInit() {
        //code begins
        bottomNav = findViewById(R.id.bottomNavAdmin)
        toolbarAdministration=findViewById(R.id.toolbarAdmin)
        //enable actionBar
        //code ends
    }

    private fun updateFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContainerAdmin, fragment, tag).commitNow()

    }

    //function Toasty Successful
    private fun funToastyShow(s: String) {
        Toasty.custom(
            this@Administration,
            s,
            R.drawable.ic_nike_done,
            R.color.colorWhite,
            Toasty.LENGTH_SHORT,
            true,
            false
        ).show()
    }
}