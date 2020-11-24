package com.food.foodella.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.adapter.BaseHomeAdapter
import com.food.foodella.database.RestaurantDatabase
import com.food.foodella.database.RestaurantEntity
import com.food.foodella.modal.Restaurant
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import kotlinx.android.synthetic.main.fragment_favourite_restaurant.*
import kotlinx.android.synthetic.main.fragment_favourite_restaurant.view.*
import java.lang.Exception


class FavouriteRestaurantFragment(val contextParam: Context) : Fragment() {
    var restaurantInfoList = arrayListOf<Restaurant>()

    lateinit var vieww: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        vieww = inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)



        return vieww


    }

    private fun getData() {
        if (ConnectionManager().checkConnectivity(contextParam)) {

            favourite_restaurant_fragment_Progressdialog.visibility = View.VISIBLE
            try {

                val queue = Volley.newRequestQueue(context)

                val url =
                    "http://" + getString(R.string.ip_address) + "/v2/restaurants/fetch_result"

                val jsonObjRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        val responseJsonData = it.getJSONObject("data")
                        val success = responseJsonData.getBoolean("success")
                        if (success) {

                            restaurantInfoList.clear()
                            val data = responseJsonData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val resJsonObj = data.getJSONObject(i)

                                val resEntity = RestaurantEntity(
                                    resJsonObj.getString("id"),
                                    resJsonObj.getString("name")
                                )


                                if (DBAsyncTask(contextParam, resEntity, 1).execute().get()) {

                                    val restaurantObject = Restaurant(
                                        resJsonObj.getString("id"),
                                        resJsonObj.getString("name"),
                                        resJsonObj.getString("rating"),
                                        resJsonObj.getString("cost_for_one"),
                                        resJsonObj.getString("image_url")
                                    )
                                    restaurantInfoList.add(restaurantObject)

                                }


                            }
                            val favAdapter = BaseHomeAdapter(contextParam, restaurantInfoList)

                            recyclerViewFavouriteRestaurant.adapter = favAdapter
                            recyclerViewFavouriteRestaurant.layoutManager =
                                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

                            if (restaurantInfoList.size == 0) {
                               vieww.empty_list.visibility=View.VISIBLE
                            }
                            else{
                                vieww.empty_list.visibility=View.GONE
                            }


                        }

                        favourite_restaurant_fragment_Progressdialog.visibility = View.INVISIBLE


                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            activity as Context,
                            "mSome Error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()

                        favourite_restaurant_fragment_Progressdialog.visibility = View.INVISIBLE

                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)

                        return headers
                    }
                }

                queue.add(jsonObjRequest)

            } catch (e: Exception) {
                Toast.makeText(
                    activity as Context,
                    "Some Unexpected error occured!",
                    Toast.LENGTH_SHORT
                ).show()

            }




        } else {
            DialogMaker().createDialog(contextParam)
        }
    }


    class DBAsyncTask(val context: Context, val resEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(resEntity.restaurant_id)
                    db.close()
                    return restaurant != null
                }
                else -> return false

            }
        }

    }


    override fun onResume() {

        if (ConnectionManager().checkConnectivity(contextParam)) {
            getData()
        } else {
            DialogMaker().createDialog(contextParam)
        }
        super.onResume()
    }
}