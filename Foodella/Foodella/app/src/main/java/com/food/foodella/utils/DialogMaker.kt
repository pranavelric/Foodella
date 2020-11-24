package com.food.foodella.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.food.foodella.R
import com.food.foodella.activities.LoginActivity

public class DialogMaker {

    fun createDialog(context: Context ) {

        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(context)

        alterDialog.setTitle("No Internet")
        alterDialog.setMessage("Internet Connection can't be establish!")
        alterDialog.setPositiveButton("Open Settings") { text, listener ->
            val settingsIntent = Intent(Settings.ACTION_SETTINGS)//open wifi settings
            context.startActivity(settingsIntent)

        }

        alterDialog.setNegativeButton("Exit") { text, listener ->
            ActivityCompat.finishAffinity(context as Activity)
        }
        alterDialog.create()
        alterDialog.show()

    }


    fun createLogoutDialog(activity: Activity){


        val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity)

        alterDialog.setTitle("Confirmation")
        alterDialog.setMessage("Are you sure you want to log out?")
        alterDialog.setPositiveButton("Yes"){text,listener->
        // sharedPreferencess.edit().putBoolean("user_logged_in",false).apply()
activity.getSharedPreferences(activity.getString(R.string.shared_preferences),
    Context.MODE_PRIVATE).edit().putBoolean("user_logged_in",false).apply()

            val options =ActivityOptions.makeCustomAnimation(activity as Context,android.R.anim.fade_in,android.R.anim.fade_out)

            activity.startActivity(Intent(activity,LoginActivity::class.java),options.toBundle())

            ActivityCompat.finishAffinity(activity)
        }

        alterDialog.setNegativeButton("No"){ text,listener->

        }
        alterDialog.create()
        alterDialog.show()


    }



}