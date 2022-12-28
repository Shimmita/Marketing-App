package com.shimitadouglas.marketcm.mains

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.shimitadouglas.marketcm.R
import de.hdodenhof.circleimageview.CircleImageView

class SplashScreenIndex : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_index)
        //call function full screen
        functionFullScreen()
        //
        //call functionUpdateUi
        funUpdateUi()
        //

        //call function intent migration
        functionIntentMigration()
        //
    }

    private fun functionIntentMigration() {
        //code begins
        Handler().postDelayed(kotlinx.coroutines.Runnable {

            val intent_main = Intent(this@SplashScreenIndex, MainActivity::class.java)
            intent_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent_main)
            finish()
        }, 5000)

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
        val image_rotation = findViewById<CircleImageView>(R.id.circleImageTire)
        //apply rotation
        image_rotation.startAnimation(
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
    }
}