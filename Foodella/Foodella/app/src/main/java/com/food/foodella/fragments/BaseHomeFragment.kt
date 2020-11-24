package com.food.foodella.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.adapter.BaseHomeAdapter
import com.food.foodella.modal.Restaurant
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_base_home.view.*
import kotlinx.android.synthetic.main.sort_dialog_layout.view.*
import java.lang.Exception
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class BaseHomeFragment(val contextParams: Context) : Fragment() {
    lateinit var baseHomeAdapter: BaseHomeAdapter
    var restaurantInfoList = arrayListOf<Restaurant>()
    lateinit var vieww: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        vieww = inflater.inflate(R.layout.fragment_base_home, container, false)


        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        vieww.text_name.text = "Hey ${sharedPref?.getString("name", "Pranav")},"






        vieww.editText_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(strTyped: Editable?) {
                filterFun(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })






        return vieww
    }


    fun fetchData() {
        if (ConnectionManager().checkConnectivity(contextParams.applicationContext)) {
            vieww.progress_bar_layout.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(activity as Context)

                val url =
                    "http://" + getString(R.string.ip_address) + "/v2/restaurants/fetch_result"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        val responseObjectData = it.getJSONObject("data")
                        val success = responseObjectData.getBoolean("success")
                        if (success) {
                            val data = responseObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObj = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )

                                restaurantInfoList.add(restaurantObj)


                                baseHomeAdapter = BaseHomeAdapter(
                                    activity as Context,
                                    restaurantInfoList
                                )

                                vieww.restaurant_recycler.adapter = baseHomeAdapter

                                vieww.restaurant_recycler.layoutManager =
                                    StaggeredGridLayoutManager(
                                        2,
                                        StaggeredGridLayoutManager.VERTICAL
                                    );


                            }

                        }

                        vieww.progress_bar_layout.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        vieww.progress_bar_layout.visibility = View.INVISIBLE
                        Toast.makeText(
                            activity as Context,
                            "Some Error occurred!" + it.message.toString(),
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

                queue.add(jsonObjectRequest)

            } catch (e: Exception) {
                Toast.makeText(
                    activity as Context,
                    "Some Unexpected error occured!" + e.message,
                    Toast.LENGTH_SHORT
                ).show()

            }


        } else {

            context?.let { DialogMaker().createDialog(it) }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.base_home_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.sort -> {
                val radioButton = View.inflate(contextParams, R.layout.sort_dialog_layout, null)
                androidx.appcompat.app.AlertDialog.Builder(contextParams)
                    .setTitle("Sort By?")
                    .setView(radioButton)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButton.radio_high_to_low.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            restaurantInfoList.reverse()
                            baseHomeAdapter.notifyDataSetChanged()
                        }
                        if (radioButton.radio_low_to_high.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            baseHomeAdapter.notifyDataSetChanged()
                        }
                        if (radioButton.radio_rating.isChecked) {
                            Collections.sort(restaurantInfoList, ratingComparator)
                            restaurantInfoList.reverse()
                            baseHomeAdapter.notifyDataSetChanged()
                        }
                    }
                    .setNeutralButton("Cancel") { text, listener ->

                    }
                    .create().show()

            }

        }


        return super.onOptionsItemSelected(item)
    }


    fun filterFun(strTyped: String) {//to filter the recycler view depending on what is typed
        val filteredList = arrayListOf<Restaurant>()

        for (item in restaurantInfoList) {
            if (item.restaurantName.toLowerCase()
                    .contains(strTyped.toLowerCase())
            ) {

                filteredList.add(item)

            }
        }

        if (filteredList.size == 0) {
            vieww.dashboard_fragment_cant_find_restaurant.visibility = View.VISIBLE
        } else {
            vieww.dashboard_fragment_cant_find_restaurant.visibility = View.INVISIBLE
        }

        baseHomeAdapter.filterList(filteredList)

    }


    var ratingComparator = Comparator<Restaurant> { rest1, rest2 ->

        if (rest1.restaurantRating.compareTo(rest2.restaurantRating, true) == 0) {
            rest1.restaurantName.compareTo(rest2.restaurantName, true)
        } else {
            rest1.restaurantRating.compareTo(rest2.restaurantRating, true)
        }

    }

    var costComparator = Comparator<Restaurant> { rest1, rest2 ->

        rest1.cost_for_one.compareTo(rest2.cost_for_one, true)

    }


    override fun onResume() {
        if (ConnectionManager().checkConnectivity(contextParams.applicationContext)) {
            if (restaurantInfoList.isEmpty()) {
                fetchData()
            }
        } else {
            DialogMaker().createDialog(contextParams)
        }

        super.onResume()
    }


}