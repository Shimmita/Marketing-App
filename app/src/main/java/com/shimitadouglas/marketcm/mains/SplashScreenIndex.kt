package com.shimitadouglas.marketcm.mains

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.mains.ProductsHome.Companion.sharedPreferenceName
import de.hdodenhof.circleimageview.CircleImageView

@SuppressLint("CustomSplashScreen")
class SplashScreenIndex : AppCompatActivity() {
    //declaration of the globals
    private lateinit var relativeLayoutSplash: RelativeLayout
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_index)
        //init of the globals
        funInitGlobals()
        //call functionUpdateUi (movement and the rotation of the tyre)
        funUpdateUi()
        //call function intent migration
        functionIntentMigration()
        //
    }

    private fun funInitGlobals() {
        //code begins
        relativeLayoutSplash = findViewById(R.id.parent_splash_parent)
        //code ends
    }

    private fun functionIntentMigration() {
        //code begins
        Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {

            val intentMain = Intent(this@SplashScreenIndex, MainActivity::class.java)
            intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentMain)
            finishAffinity()

        }, 1150)

        //code end
    }

    private fun funUpdateUi() {
        //title text movements
        val tv = findViewById<TextView>(R.id.textTitle)
        //apply motion
        tv.startAnimation(AnimationUtils.loadAnimation(this@SplashScreenIndex, R.anim.down))
        //ui image rotation
        val imageRotation = findViewById<CircleImageView>(R.id.circleImageTire)
        //apply rotation
        imageRotation.startAnimation(
            AnimationUtils.loadAnimation(
                this@SplashScreenIndex, R.anim.rotate_avg
            )
        )
        //
    }
}