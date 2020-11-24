package com.food.foodella.activities

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.food.foodella.R
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_order_placed.*

class OrderPlacedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        FullScreen().fullScreenNotch(this@OrderPlacedActivity)


        buttonOkay.setOnClickListener(View.OnClickListener {

            val options = ActivityOptions.makeCustomAnimation(
                this@OrderPlacedActivity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            val intent = Intent(this, HomeActivity::class.java)

            startActivity(intent, options.toBundle())

           finish()
        })

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            FullScreen().fullScreen(this@OrderPlacedActivity)

        }
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onBackPressed() {

    }

}