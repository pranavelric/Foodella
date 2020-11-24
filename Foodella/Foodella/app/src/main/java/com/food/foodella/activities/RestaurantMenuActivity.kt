package com.food.foodella.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.adapter.RestaurantMenuAdapter
import com.food.foodella.modal.RestaurantMenu
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import com.food.foodella.utils.FullScreen
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_restaurant_menu.*

class RestaurantMenuActivity : AppCompatActivity() {

    var restaurantMenuList = arrayListOf<RestaurantMenu>()
   lateinit var  menuAdapter:RestaurantMenuAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        FullScreen().fullScreenNotch(this@RestaurantMenuActivity)

        name.text = intent.getStringExtra("restaurantName")
        price.text = intent.getStringExtra("restaurantPrice")
        rating.text = intent.getStringExtra("restaurantRating")


        Picasso.get().load(intent.getStringExtra("restaurantImage")).error(R.drawable.food)
            .into(restaurant_image)



        back.setOnClickListener {
            if(menuAdapter.getSelectedItemCount()>0) {
                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                alterDialog.setTitle("Alert!")
                alterDialog.setMessage("Going back will remove everything from cart")
                alterDialog.setPositiveButton("Okay") { _, _ ->
                    super.onBackPressed()
                }
                alterDialog.setNegativeButton("No") { _, _ ->

                }
                alterDialog.show()
            }else{
                super.onBackPressed()
            }
        }




    }

    private fun getData() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            restaurant_menu_progressdialog.visibility = View.VISIBLE

            try {
                val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)
                val url =
                    "http://" + getString(R.string.ip_address) + "/v2/restaurants/fetch_result/" + intent.getStringExtra(
                        "restaurantId"
                    )
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        val responseJsonObjData = it.getJSONObject("data")
                        val success = responseJsonObjData.getBoolean("success")
                        if (success) {
                            restaurantMenuList.clear()
                            val data = responseJsonObjData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val jsonObj = data.getJSONObject(i)
                                val menuObj = RestaurantMenu(
                                    jsonObj.getString("id"),
                                    jsonObj.getString("name"),
                                    jsonObj.getString("cost_for_one")
                                )
                                restaurantMenuList.add(menuObj)


                            }

                             menuAdapter = RestaurantMenuAdapter(
                                this@RestaurantMenuActivity,
                               intent.getStringExtra("restaurantId").toString(),//pass the restaurant Id
                                 intent.getStringExtra("restaurantName").toString(),
                                goto_cart_btn,
                                go_to_cart_layout ,
                                restaurantMenuList

                            )

                            food_recycler.adapter = menuAdapter
                            food_recycler.layoutManager =
                                LinearLayoutManager(this@RestaurantMenuActivity)


                        }
                        restaurant_menu_progressdialog.visibility = View.INVISIBLE

                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Some Error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                        restaurant_menu_progressdialog.visibility = View.INVISIBLE

                    }

                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)


                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Some Unexpected error occured!",
                    Toast.LENGTH_SHORT
                ).show()
            }


        } else {
            DialogMaker().createDialog(this@RestaurantMenuActivity)
        }
    }



    override fun onBackPressed() {


        if(menuAdapter.getSelectedItemCount()>0) {


            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { _, _ ->

            }
            alterDialog.show()
        }else{
            super.onBackPressed()
        }

    }





    override fun onResume() {

        if (ConnectionManager().checkConnectivity(applicationContext)) {
            if(restaurantMenuList.isEmpty())
                getData()
        }else
        {

  DialogMaker().createDialog(this@RestaurantMenuActivity)

        }


        super.onResume()
    }









    override fun onWindowFocusChanged(hasFocus: Boolean) {

        if (hasFocus) {
            FullScreen().fullScreen(this@RestaurantMenuActivity)

        }

        super.onWindowFocusChanged(hasFocus)
    }

}