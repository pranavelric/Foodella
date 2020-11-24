package com.food.foodella.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*

import org.json.JSONException
import org.json.JSONObject


class ForgotPasswordFragment(val contextParam: Context) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)


        view.cirSubmitButton.setOnClickListener {


            if (view.editTextMobile.text.isBlank()) {
                view.editTextMobile.setError("Mobile Number Missing")
            } else {
                if (view.editTextEmail.text.isBlank()) {
                    view.editTextEmail.error = "Email Missing"
                } else {

                    if (ConnectionManager().checkConnectivity(activity as Context)) {

                        try {

                            val loginUser = JSONObject()

                            loginUser.put("mobile_number", view.editTextMobile.text)
                            loginUser.put("email", view.editTextEmail.text)


                            val queue = Volley.newRequestQueue(activity as Context)

                            val url =
                                "http://" + getString(R.string.ip_address) + "/v2/forgot_password/fetch_result"

                            view.forgot_password_fragment_Progressdialog.visibility = View.VISIBLE

                            val jsonObjectRequest = object : JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                loginUser,
                                Response.Listener {

                                    val responseJsonObjectData = it.getJSONObject("data")

                                    val success = responseJsonObjectData.getBoolean("success")

                                    if (success) {

                                        val first_try =
                                            responseJsonObjectData.getBoolean("first_try")

                                        if (first_try == true) {
                                            Toast.makeText(
                                                contextParam,
                                                "OTP sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            callChangePasswordFragment()//after we get a response we call the Log the user in
                                        } else {
                                            Toast.makeText(
                                                contextParam,
                                                "OTP sent already",
                                                Toast.LENGTH_SHORT
                                            ).show()



                                            callChangePasswordFragment()
                                        }

                                    } else {
                                        val responseMessageServer =
                                            responseJsonObjectData.getString("errorMessage")
                                        Toast.makeText(
                                            contextParam,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                    view.forgot_password_fragment_Progressdialog.visibility =
                                        View.INVISIBLE
                                },
                                Response.ErrorListener {

                                    Toast.makeText(
                                        contextParam,
                                        "Some Error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                    view.forgot_password_fragment_Progressdialog.visibility =
                                        View.INVISIBLE

                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()

                                    headers["Content-type"] = "application/json"
                                    headers["token"] = getString(R.string.token)

                                    return headers
                                }
                            }
                            jsonObjectRequest.setRetryPolicy(
                                DefaultRetryPolicy(
                                    15000,
                                    1,
                                    1f
                                )
                            )

                            queue.add(jsonObjectRequest)

                        } catch (e: JSONException) {
                            Toast.makeText(
                                contextParam,
                                "Some unexpected error occured!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        DialogMaker().createDialog(activity as Context)
                    }
                }
            }
        }



        return view
    }


    fun callChangePasswordFragment() {

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.frameLayout,
            ForgotPasswordResponseFragment(contextParam, view?.editTextMobile?.text.toString())
        )

        transaction?.commit()
    }

    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as Context)) {
            DialogMaker().createDialog(activity as Context)
        }

        super.onResume()
    }

}