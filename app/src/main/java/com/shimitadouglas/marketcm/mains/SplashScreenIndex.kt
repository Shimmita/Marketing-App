package com.shimitadouglas.marketcm.mains

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.sharedPreferenceName
import de.hdodenhof.circleimageview.CircleImageView

@SuppressLint("CustomSplashScreen")
class SplashScreenIndex : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_index)
        //call function full screen
        functionFullScreen()
        //
        //call functionUpdateUi (movement and the rotation of the tyre)
        funUpdateUi()
        //call function intent migration
        functionIntentMigration()
        //
    }

    private fun functionIntentMigration() {
        //code begins
        Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {

            val intentMain = Intent(this@SplashScreenIndex, MainActivity::class.java)
            intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentMain)
            finishAffinity()

        }, 4500)

        //code end
    }

    private fun funUpdateUi() {
        //title text movements
        val tv = findViewById<TextView>(R.id.textTitle)
        //apply motion
        tv.startAnimation(AnimationUtils.loadAnimation(this@SplashScreenIndex, R.anim.down))
        //
        //

        //ui image rotation
        val imageRotation = findViewById<CircleImageView>(R.id.circleImageTire)
        //apply rotation
        imageRotation.startAnimation(
            AnimationUtils.loadAnimation(
                this@SplashScreenIndex, R.anim.rotate
            )
        )
        //

    }

    private fun functionFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        //check if the user is not signed out, sign him out forcefully and clear the data of the session
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            //clear all the data saved in the shared preference
            val sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            //clear sign out the user
            FirebaseAuth.getInstance().signOut()
            //
        }
        //

    }
}