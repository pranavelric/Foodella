package com.food.foodella.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.modal.CartItem
import com.food.foodella.modal.OrderHistory
import com.food.foodella.utils.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, val orderList: ArrayList<OrderHistory>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewResturantName: TextView = view.findViewById(R.id.textViewResturantName)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val recyclerViewItemsOrdered: RecyclerView =
            view.findViewById(R.id.recyclerViewItemsOrdered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_item, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }



    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {

        val restaurantObj = orderList[position]
        holder.textViewResturantName.text=restaurantObj.restaurantName


        var formatDate=restaurantObj.orderPlacedAt
        formatDate=formatDate.replace("-","/")
        formatDate=formatDate.substring(0,6)+"20"+formatDate.substring(6,8)
        holder.textViewDate.text =  formatDate


        var layoutManager = LinearLayoutManager(context)
        var orderedItemAdapter: CartAdapter









        if (ConnectionManager().checkConnectivity(context)) {

            try {

                val orderItemsPerRestaurant=ArrayList<CartItem>()

                val sharedPreferencess=context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE)

                val user_id=sharedPreferencess.getString("user_id","0")

                val queue = Volley.newRequestQueue(context)

                val url = "http://"+context.getString(R.string.ip_address)+"/v2/orders/fetch_result/" + user_id

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            val fetechedRestaurantJsonObject = data.getJSONObject(position)//restaurant at index of position

                            orderItemsPerRestaurant.clear()

                            val foodOrderedJsonArray=fetechedRestaurantJsonObject.getJSONArray("food_items")

                            for(j in 0 until foodOrderedJsonArray.length())//loop through all the items
                            {
                                val eachFoodItem = foodOrderedJsonArray.getJSONObject(j)//each food item
                                val itemObject = CartItem(
                                    eachFoodItem.getString("food_item_id"),
                                    eachFoodItem.getString("name"),
                                    eachFoodItem.getString("cost"),
                                    "000"
                                )

                                orderItemsPerRestaurant.add(itemObject)

                            }

                            orderedItemAdapter = CartAdapter(
                                context,
                                orderItemsPerRestaurant
                            )

                            holder.recyclerViewItemsOrdered.adapter =
                                orderedItemAdapter

                            holder.recyclerViewItemsOrdered.layoutManager = layoutManager


                        }
                    },
                    Response.ErrorListener {


                        Toast.makeText(
                            context,
                            "Some Error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = context.getString(R.string.token)

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    context,
                    "Some Unexpected error occured!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }






    }

}