package com.food.foodella.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.food.foodella.R
import com.food.foodella.fragments.ForgotPasswordFragment
import com.food.foodella.utils.FullScreen

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        FullScreen().fullScreenNotch(this@ForgotPasswordActivity)
        openForgetPasswordFragment()

    }


    private fun openForgetPasswordFragment() {

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(
            R.id.frameLayout,
            ForgotPasswordFragment(this)
        )
        transaction.commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

        if (hasFocus) {
            FullScreen().fullScreen(this@ForgotPasswordActivity)
        }
        super.onWindowFocusChanged(hasFocus)
    }

}