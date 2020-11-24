package com.food.foodella.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.adapter.OrderHistoryAdapter
import com.food.foodella.modal.OrderHistory
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import org.json.JSONException


class OrderHistoryFragment(val contextParam: Context) : Fragment() {

    private lateinit var vieww: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vieww = inflater.inflate(R.layout.fragment_order_history, container, false)


        return vieww
    }


    fun getData() {


        val orderedRestaurantList = ArrayList<OrderHistory>()

        val sharedPreferencess = contextParam.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        val user_id = sharedPreferencess.getString("user_id", "000")

        if (ConnectionManager().checkConnectivity(contextParam.applicationContext)) {

            vieww.order_activity_history_Progressdialog.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(contextParam)

                val url =
                    "http://" + getString(R.string.ip_address) + "/v2/orders/fetch_result/" + user_id

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            if (data.length() == 0) {


                                vieww.order_history_fragment_no_orders.visibility = View.VISIBLE

                            } else {
                                vieww.order_history_fragment_no_orders.visibility = View.INVISIBLE



                                for (i in 0 until data.length()) {
                                    val restaurantItemJsonObject = data.getJSONObject(i)

                                    val eachRestaurantObject = OrderHistory(
                                        restaurantItemJsonObject.getString("order_id"),
                                        restaurantItemJsonObject.getString("restaurant_name"),
                                        restaurantItemJsonObject.getString("total_cost"),
                                        restaurantItemJsonObject.getString("order_placed_at")
                                            .substring(0, 10)
                                    )




                                    orderedRestaurantList.add(eachRestaurantObject)


                                }

                                val adapter = OrderHistoryAdapter(
                                    contextParam,
                                    orderedRestaurantList
                                )

                                vieww.recyclerViewAllOrders.adapter = adapter

                                vieww.recyclerViewAllOrders.layoutManager =
                                    LinearLayoutManager(contextParam)


                            }

                        }
                        vieww.order_activity_history_Progressdialog.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        vieww.order_activity_history_Progressdialog.visibility = View.INVISIBLE

                        Toast.makeText(
                            contextParam,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    contextParam,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            DialogMaker().createDialog(contextParam)

        }

    }


    override fun onResume() {

        if (ConnectionManager().checkConnectivity(contextParam.applicationContext)) {
            getData()
        } else {


            DialogMaker().createDialog(contextParam)

        }

        super.onResume()
    }


}