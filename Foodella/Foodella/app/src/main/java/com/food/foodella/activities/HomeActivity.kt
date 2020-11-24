package com.food.foodella.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.MenuItem
import android.view.View

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.food.foodella.R
import com.food.foodella.fragments.BaseHomeFragment
import com.food.foodella.fragments.FaqFragment
import com.food.foodella.fragments.FavouriteRestaurantFragment
import com.food.foodella.fragments.OrderHistoryFragment
import com.food.foodella.utils.DialogMaker
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_home.*

import kotlinx.android.synthetic.main.drawer_header.view.*


class HomeActivity : AppCompatActivity() {

    private var previousMenuItemSelected: MenuItem? = null
    private var selectedItem: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        FullScreen().fullScreenNotch(this@HomeActivity)

        val sharedPreferencess = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        val headerView = navigationView.getHeaderView(0)
        navigationView.menu.getItem(0).isCheckable = true
        navigationView.menu.getItem(0).isChecked = true


        setToolBar()
        openBaseHome()

        headerView.textViewcurrentUser.text =
            sharedPreferencess.getString("name", "Pranav Choudhary")
        headerView.textViewMobileNumber.text =
            "+91-" + sharedPreferencess.getString("mobile_number", "1234567890")
        val actionBarToggle = object : ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        ) {


            override fun onDrawerClosed(drawerView: View) {

                when (selectedItem) {
                    "0" -> {
                        openBaseHome()
                        selectedItem = ""

                    }
                    "1" -> {
                        selectedItem = ""

                    }
                    "2" -> {
                        selectedItem = ""
                        openFavResFrag()
                    }
                    "3" -> {
                        selectedItem = ""
                        openOrderHistoryFrag()

                    }
                    "4" -> {
                        selectedItem = ""

                    }
                    "5" -> {
                        selectedItem = ""

                    }
                    else -> {
                        super.onDrawerClosed(drawerView)

                    }
                }


            }

        }





        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()



        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItemSelected != null) {
                previousMenuItemSelected?.isChecked = false
            }
            previousMenuItemSelected = it
            it.isChecked = true
            it.isCheckable = true

            when (it.itemId) {
                R.id.homee -> {
                    selectedItem = "0"
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    selectedItem = "1"
                    openProfileAcitivty()
                    drawerLayout.closeDrawers()
                }
                R.id.favouriteRestaurants -> {
                    selectedItem = "2"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    selectedItem = "3"

                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    selectedItem = "4"

                    openFaqFrag()


                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {

                    selectedItem = "5"
                    drawerLayout.closeDrawers()
                    DialogMaker().createLogoutDialog(this@HomeActivity)

                }

            }



            return@setNavigationItemSelectedListener true
        }


        headerView.imageViewCurrentUser.setOnClickListener {
            openProfileAcitivty()
            drawerLayout.closeDrawers()
        }

    }

    private fun openFaqFrag() {
        supportActionBar?.title = "Faqs"

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutHome,FaqFragment(this@HomeActivity))
        transaction.commit()

        navigationView.setCheckedItem(R.id.faqs)
    }

    private fun openOrderHistoryFrag(){


        supportActionBar?.title = "Order history"

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutHome,OrderHistoryFragment(this@HomeActivity))
        transaction.commit()

        navigationView.setCheckedItem(R.id.orderHistory)

    }


    private fun openProfileAcitivty() {
        val options = ActivityOptions.makeCustomAnimation(
            this@HomeActivity,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(
            Intent(this@HomeActivity, ProfileActivity::class.java),
            options.toBundle()
        )
    }
private fun openFavResFrag(){

    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.frameLayoutHome,FavouriteRestaurantFragment(this@HomeActivity))
    transaction.commit()
    supportActionBar?.title =
        "Favourite restaurants"//change the title when each new fragment is opened
    navigationView.setCheckedItem(R.id.favouriteRestaurants)
}

    private fun openBaseHome() {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutHome, BaseHomeFragment(this@HomeActivity))
        transaction.commit()
        supportActionBar?.title =
            "All Restaurants"//change the title when each new fragment is opened
        navigationView.setCheckedItem(R.id.homee)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.frameLayoutHome)

        when (currentFragment) {
            !is BaseHomeFragment -> {
                navigationView.menu.getItem(0).isChecked = true
                openBaseHome()
            }
            else -> super.onBackPressed()
        }

    }


    private fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            FullScreen().fullScreen(this@HomeActivity)
            FullScreen().adjustToolbarMarginForNotch(this@HomeActivity)

            FullScreen().adjustNavigationDrawerHeaderForNotch(this@HomeActivity)


        }
        super.onWindowFocusChanged(hasFocus)
    }


}