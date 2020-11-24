package com.food.foodella.activities


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodella.R
import com.food.foodella.utils.ConnectionManager
import com.food.foodella.utils.DialogMaker
import com.food.foodella.utils.FullScreen
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_login.*
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferencess = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        //fullscreen on notch
        FullScreen().fullScreenNotch(this@LoginActivity)

        val options = ActivityOptions.makeCustomAnimation(
            this@LoginActivity,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )

        if (sharedPreferencess.getBoolean("user_logged_in", false)) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent, options.toBundle())
            finish();
        }

        //for transition animation
        text_signup.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent, options.toBundle())
        }

        text_forgot_pass.setOnClickListener {

            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent, options.toBundle())
        }



        cirLoginButton.setOnClickListener(View.OnClickListener {

            layout_login.visibility = View.INVISIBLE
            progress_bar.visibility - View.VISIBLE

            if (editTextMobile.text.isBlank()) {
                editTextMobile.error = "Mobile Number Missing"
                layout_login.visibility = View.VISIBLE
                progress_bar.visibility - View.INVISIBLE
            } else {
                if (editTextPassword.text.isBlank()) {
                    editTextPassword.error = "Missing Password"

                    layout_login.visibility = View.VISIBLE
                    progress_bar.visibility - View.INVISIBLE

                } else {
                    loginUser()
                }
            }

        })


    }


    private fun loginUser() {
        val sharedPreferencess =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)


        if (ConnectionManager().checkConnectivity(applicationContext)) {
            try {

                val loginUser = JSONObject()
                loginUser.put("mobile_number", editTextMobile.text)
                loginUser.put("password", editTextPassword.text)


                val queue = Volley.newRequestQueue(this@LoginActivity)

                val url = "http://" + getString(R.string.ip_address) + "/v2/login/fetch_result"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    loginUser,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")


                        if (success) {


                            val data = responseJsonObjectData.getJSONObject("data")
                            sharedPreferencess.edit().putBoolean("user_logged_in", true).apply()
                            sharedPreferencess.edit()
                                .putString("user_id", data.getString("user_id")).apply()
                            sharedPreferencess.edit().putString("name", data.getString("name"))
                                .apply()
                            sharedPreferencess.edit().putString("email", data.getString("email"))
                                .apply()
                            sharedPreferencess.edit()
                                .putString("mobile_number", data.getString("mobile_number")).apply()
                            sharedPreferencess.edit()
                                .putString("address", data.getString("address")).apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Welcome " + data.getString("name"),
                                Toast.LENGTH_SHORT
                            ).show()

                            userSuccessfullyLoggedIn()//after we get a response we call the Log the user in
                            layout_login.visibility = View.VISIBLE
                            progress_bar.visibility = View.INVISIBLE
                        } else {

                            layout_login.visibility = View.VISIBLE
                            progress_bar.visibility = View.INVISIBLE
                            val responseMessageServer =
                                responseJsonObjectData.getString("errorMessage")
                            Toast.makeText(
                                this@LoginActivity,
                                responseMessageServer.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    },
                    Response.ErrorListener {

                        layout_login.visibility = View.VISIBLE
                        progress_bar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@LoginActivity,
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
                layout_login.visibility = View.VISIBLE
                progress_bar.visibility = View.INVISIBLE
                Toast.makeText(
                    this@LoginActivity,
                    "Some unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }


        } else {

            layout_login.visibility = View.VISIBLE
            progress_bar.visibility = View.INVISIBLE
            DialogMaker().createDialog(this@LoginActivity)
        }

    }

    fun userSuccessfullyLoggedIn() {
        val options = ActivityOptions.makeCustomAnimation(
            this@LoginActivity,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )

        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent, options.toBundle())
        finish();

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            //fullscreen
            FullScreen().fullScreen(this@LoginActivity)
        }
    }


}