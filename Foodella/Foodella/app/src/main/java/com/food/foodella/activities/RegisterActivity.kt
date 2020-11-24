package com.food.foodella.activities


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_login.progress_bar
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_register.*
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferencess = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        setContentView(R.layout.activity_register)

        FullScreen().fullScreenNotch(this@RegisterActivity)

        if (sharedPreferencess.getBoolean("user_logged_in", false)) {
            openDashBoard()
        }


        login_text.setOnClickListener {
            val option = ActivityOptions.makeCustomAnimation(
                this@RegisterActivity,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right

            )
            startActivity(
                Intent(this@RegisterActivity, LoginActivity::class.java),
                option.toBundle()
            )

        }


        cirSignUpButton.setOnClickListener { registerUser() }


    }


    private fun registerUser() {


        val sharedPreferencess =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)


        sharedPreferencess.edit().putBoolean("user_logged_in", false).apply()

        if (ConnectionManager().checkConnectivity(applicationContext)) {


            if (checkForErrors()) {

                layout_register.visibility = View.INVISIBLE
                progress_bar.visibility = View.VISIBLE
                try {

                    val registerUser = JSONObject()
                    registerUser.put("name", editTextName.text)
                    registerUser.put("mobile_number", editTextMobile.text)
                    registerUser.put("password", editTextPassword.text)
                    registerUser.put("address", editTextDeliveryAddress.text)
                    registerUser.put("email", editTextEmail.text)


                    val queue = Volley.newRequestQueue(this@RegisterActivity)

                    val url =
                        "http://" + getString(R.string.ip_address) + "/v2/register/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        registerUser,
                        Response.Listener {


                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")


                            if (success) {

                                val data = responseJsonObjectData.getJSONObject("data")
                                sharedPreferencess.edit().putBoolean("user_logged_in", true)
                                    .apply()
                                sharedPreferencess.edit()
                                    .putString("user_id", data.getString("user_id")).apply()
                                sharedPreferencess.edit().putString("name", data.getString("name"))
                                    .apply()
                                sharedPreferencess.edit()
                                    .putString("email", data.getString("email")).apply()
                                sharedPreferencess.edit()
                                    .putString("mobile_number", data.getString("mobile_number"))
                                    .apply()
                                sharedPreferencess.edit()
                                    .putString("address", data.getString("address")).apply()


                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registered sucessfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                userSuccessfullyRegistered()


                            } else {
                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    this@RegisterActivity,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            layout_register.visibility = View.VISIBLE
                            progress_bar.visibility = View.INVISIBLE
                        },
                        Response.ErrorListener {

                            layout_register.visibility = View.VISIBLE
                            progress_bar.visibility = View.INVISIBLE

                            Toast.makeText(
                                this@RegisterActivity,
                                "Some Error occurred!!! "+it.message,
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
                        this@RegisterActivity,
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

        } else {
            DialogMaker().createDialog(this@RegisterActivity)

        }
    }


    fun checkForErrors(): Boolean {
        var errorPassCount = 0
        if (editTextName.text.isBlank()) {

            editTextName.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (editTextMobile.text.isBlank()) {
            editTextMobile.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (editTextEmail.text.isBlank()) {
            editTextEmail.error = "Field Missing!"
        } else {
            errorPassCount++
        }

        if (editTextDeliveryAddress.text.isBlank()) {
            editTextDeliveryAddress.error = "Field Missing!"
        } else {
            errorPassCount++
        }


        if (editTextPassword.text.isBlank()) {
            editTextPassword.error = "Field Missing!"
        } else {
            errorPassCount++
        }


        return errorPassCount == 5

    }

    fun userSuccessfullyRegistered() {
        openDashBoard()
    }

    fun openDashBoard() {

        val options = ActivityOptions.makeCustomAnimation(
            this@RegisterActivity,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )

        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        startActivity(intent, options.toBundle())
        finish();
        Toast.makeText(this, "DashBorsd", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(applicationContext)) {

            DialogMaker().createDialog(this@RegisterActivity)

        }


        super.onResume()

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            //fullscreen



            FullScreen().fullScreen(this@RegisterActivity)

        }
    }
}