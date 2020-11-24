package com.food.foodella.activities

import android.app.ActivityOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.adapter.CartAdapter
import com.food.foodella.modal.CartItem
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {
    var totalAmount = 0
    var cartListItems = arrayListOf<CartItem>()
    lateinit var selectedItemsId: ArrayList<String>
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        FullScreen().fullScreenNotch(this@CartActivity)

        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()

        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId") as ArrayList<String>

        textViewOrderingFrom.text = restaurantName

        buttonPlaceOrder.setOnClickListener {
            val sharedPreferencess = this.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )

            if (ConnectionManager().checkConnectivity(applicationContext)) {

                activity_cart_Progressdialog.visibility = View.VISIBLE
                try {

                    val foodJsonArray = JSONArray()
                    for (foodItem in selectedItemsId) {
                        val singleItemObject = JSONObject()
                        singleItemObject.put("food_item_id", foodItem)
                        foodJsonArray.put(singleItemObject)
                    }
                    val sendOrder = JSONObject()
                    sendOrder.put("user_id", sharedPreferencess.getString("user_id", "0"))
                    sendOrder.put("restaurant_id", restaurantId)
                    sendOrder.put("total_cost", totalAmount)
                    sendOrder.put("food", foodJsonArray)


                    val queue = Volley.newRequestQueue(this)
                    val url =
                        "http://" + getString(R.string.ip_address) + "/v2/place_order/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        sendOrder,
                        Response.Listener {

                            val responseJsonObj = it.getJSONObject("data")
                            val success = responseJsonObj.getBoolean("success")
                            if (success) {
                                Toast.makeText(
                                    this,
                                    "Order Placed",
                                    Toast.LENGTH_SHORT
                                ).show()

                                //notification
                                createNotification()
                                val options = ActivityOptions.makeCustomAnimation(
                                    this@CartActivity,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )

                                activity_cart_Progressdialog.visibility = View.INVISIBLE
                                val intent = Intent(this, OrderPlacedActivity::class.java)
                                startActivity(intent, options.toBundle())
                                finishAffinity()

                            } else {
                                responseJsonObj.getString("errorMessage")
                                Toast.makeText(
                                    this, "Some error occured!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            activity_cart_Progressdialog.visibility = View.INVISIBLE


                        },
                        Response.ErrorListener {

                            Toast.makeText(
                                this,
                                "Some Error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()


                        }
                    ) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()

                            headers["Content-type"] = "application/json"
                            headers["token"] = getString(R.string.token)

                            return headers
                        }
                    }
                    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                        15000,
                        1,
                        1f
                    )

                    queue.add(jsonObjectRequest)

                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                DialogMaker().createDialog(this@CartActivity)
            }


        }

        setToolBar()


    }

    private fun createNotification() {


        val notificationId=1;
        val channelId="personal_notification"



        val notificationBulider= NotificationCompat.Builder(this,channelId)
        notificationBulider.setSmallIcon(R.drawable.app_logo)
        notificationBulider.setContentTitle("Order Placed")
        notificationBulider.setContentText("Your order has been successfully placed!")
        notificationBulider.setStyle(NotificationCompat.BigTextStyle().bigText("Ordered from "+restaurantName+" and amounting to Rs."+ totalAmount))
        notificationBulider.setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManagerCompat= NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId,notificationBulider.build())

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            val name ="Order Placed"
            val description="Your order has been successfully placed!"
            val importance= NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel= NotificationChannel(channelId,name,importance)

            notificationChannel.description=description

            val notificationManager=  (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)

        }



    }


    fun getData() {

        if (ConnectionManager().checkConnectivity(this)) {

            activity_cart_Progressdialog.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(this)

                val url =
                    "http://" + getString(R.string.ip_address) + "/v2/restaurants/fetch_result/" + restaurantId

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")


                            cartListItems.clear()

                            totalAmount = 0

                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if (selectedItemsId.contains(cartItemJsonObject.getString("id"))) {

                                    val menuObject = CartItem(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        restaurantId
                                    )

                                    totalAmount += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()


                                    cartListItems.add(menuObject)

                                }


                                val menuAdapter = CartAdapter(
                                    this,
                                    cartListItems
                                )

                                recyclerViewCart.adapter =
                                    menuAdapter

                                recyclerViewCart.layoutManager =
                                    LinearLayoutManager(this@CartActivity)
                            }



                            buttonPlaceOrder.text = "Place Order(Total:Rs. " + totalAmount + ")"

                        }
                        activity_cart_Progressdialog.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        activity_cart_Progressdialog.visibility = View.INVISIBLE

                    }) {
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
                    "Some Unexpected error occured!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            DialogMaker().createDialog(this@CartActivity)

        }

    }

    fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            getData()
        } else {
            DialogMaker().createDialog(this@CartActivity)
        }

        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            FullScreen().fullScreen(this@CartActivity)
            FullScreen().adjustToolbarMarginForNotch(this@CartActivity)
        }
        super.onWindowFocusChanged(hasFocus)
    }

}