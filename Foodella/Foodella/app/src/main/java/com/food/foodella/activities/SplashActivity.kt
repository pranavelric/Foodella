package com.food.foodella.activities


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.food.foodella.R
import com.food.foodella.utils.FullScreen


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FullScreen().fullScreenNotch(this@SplashActivity)

        Handler().postDelayed({
            val mainIntent =
                Intent(this@SplashActivity, LoginActivity::class.java)
            finish()
            startActivity(mainIntent)
        }, 2000)


    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            //fullscreen
           FullScreen().fullScreen(this@SplashActivity)
        }
    }


}