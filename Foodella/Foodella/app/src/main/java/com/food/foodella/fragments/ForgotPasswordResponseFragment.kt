package com.food.foodella.fragments

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.activities.LoginActivity
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import kotlinx.android.synthetic.main.fragment_forgot_password_response.*
import kotlinx.android.synthetic.main.fragment_forgot_password_response.view.*
import org.json.JSONException
import org.json.JSONObject


class ForgotPasswordResponseFragment(val contextParam: Context, val mobile_number: String) :
    Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_forgot_password_response, container, false)

        view.cirSubmitButton.setOnClickListener {
            if (view.editTextOtp.text.isBlank()) {
                view.editTextOtp.setError("OTP missing")
            } else {
                if (view.editTextPassword.text.isBlank()) {
                    view.editTextPassword.setError("Password Missing")
                } else {
                    if (view.editTextConfirmPassword.text.isBlank()) {
                       view.editTextConfirmPassword.setError("Confirm Password Missing")
                    } else {
                        if ((view.editTextPassword.text.toString()
                                .toInt() == view.editTextConfirmPassword.text.toString().toInt())
                        ) {
                            if (ConnectionManager().checkConnectivity(activity as Context)) {

                                forgot_password_fragment_Progressdialog.visibility = View.VISIBLE
                                try {

                                    val loginUser = JSONObject()

                                    loginUser.put("mobile_number", mobile_number)
                                    loginUser.put("password", editTextPassword.text.toString())
                                    loginUser.put("otp", editTextOtp.text.toString())

                                    val queue = Volley.newRequestQueue(activity as Context)

                                    val url =
                                        "http://" + getString(R.string.ip_address) + "/v2/reset_password/fetch_result"

                                    val jsonObjectRequest = object : JsonObjectRequest(
                                        Request.Method.POST,
                                        url,
                                        loginUser,
                                        Response.Listener {

                                            val responseJsonObjectData = it.getJSONObject("data")

                                            val success =
                                                responseJsonObjectData.getBoolean("success")

                                            if (success) {

                                                val serverMessage =
                                                    responseJsonObjectData.getString("successMessage")

                                                Toast.makeText(
                                                    contextParam,
                                                    serverMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                passwordChanged()

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

                                            view.forgot_password_fragment_Progressdialog.visibility =
                                                View.INVISIBLE

                                            Toast.makeText(
                                                contextParam,
                                                "Some Error occurred!",
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
                                        "Some unexpected error occured!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                DialogMaker().createDialog(activity as Context)
                            }

                        } else {

                            view.editTextConfirmPassword.setError("Passwords don't match")

                        }
                    }
                }
            }

        }

        return view
    }


    fun passwordChanged() {
        val options = ActivityOptions.makeCustomAnimation(
            activity,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )

        activity?.startActivity(Intent(activity, LoginActivity::class.java), options.toBundle())
    }


    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as Context)) {
            DialogMaker().createDialog(activity as Context)
        }

        super.onResume()
    }


}