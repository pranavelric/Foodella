package com.food.foodella.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_base_home.view.*

public class FullScreen {

    fun fullScreen(activity: Activity){
        activity.window.decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    fun fullScreenNotch(activity: Activity){
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
           activity.window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }




   fun adjustToolbarMarginForNotch(activity: Activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val windowInsets = activity.window.decorView.rootWindowInsets
            if (windowInsets != null) {
                val displayCutout = windowInsets.displayCutout
                if (displayCutout != null) {
                    val safeInsetTop = displayCutout.safeInsetTop
                    val newLayoutParams = activity.toolBar.layoutParams as ViewGroup.MarginLayoutParams
                    newLayoutParams.setMargins(0, safeInsetTop, 0, 0)
                    activity.toolBar.layoutParams = newLayoutParams

                }
            }
        }
    }


    fun adjustNavigationDrawerHeaderForNotch(activity: Activity){



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
           activity.navigationView.setOnApplyWindowInsetsListener { v, insets ->

                val header = activity.navigationView.getHeaderView(0)
                header.setPadding(
                    header.paddingLeft,
                    insets.systemWindowInsetTop,
                    header.paddingRight,
                    header.paddingBottom
                )
                insets.consumeSystemWindowInsets()
            }
        }

    }




}