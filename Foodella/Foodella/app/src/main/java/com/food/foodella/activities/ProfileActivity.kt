package com.food.foodella.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.food.foodella.R
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.profile_content.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        FullScreen().fullScreenNotch(this@ProfileActivity)

        val sharedPreferencess = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        profile_name_text.text = sharedPreferencess.getString("name", "Pranav")
        email_text.text = sharedPreferencess.getString("email", "temp@gmail.com")
        top_email_text.text = sharedPreferencess.getString("email", "temp@gmail.com")
        mobile_number_text.text = sharedPreferencess.getString("mobile_number", "1234567890")
        address_text.text = sharedPreferencess.getString("address", "planet earth")


        back_to_home.setOnClickListener {

            val options = ActivityOptions.makeCustomAnimation(
                this@ProfileActivity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(
                Intent(this@ProfileActivity, HomeActivity::class.java),
                options.toBundle()
            )

        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {

        if (hasFocus) {
            FullScreen().fullScreen(this@ProfileActivity)

        }
        super.onWindowFocusChanged(hasFocus)
    }


}