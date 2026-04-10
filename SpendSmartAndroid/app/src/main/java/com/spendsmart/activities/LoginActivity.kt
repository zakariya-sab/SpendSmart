package com.spendsmart.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spendsmart.api.ApiClient
import com.spendsmart.databinding.ActivityLoginBinding
import com.spendsmart.models.ApiResponse
import com.spendsmart.models.LoginResponse
import com.spendsmart.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Login screen activity — the app's entry point.
 * Displays email and password fields and authenticates the user
 * against the Spring Boot backend. On success, saves the JWT token
 * and navigates to MainActivity.
 */
class LoginActivity : AppCompatActivity() {

    /** View binding for type-safe access to layout views */
    private lateinit var binding: ActivityLoginBinding

    /** Session manager for storing the JWT token */
    private lateinit var sessionManager: SessionManager

    /**
     * Initialize the activity, check if already logged in,
     * and set up the login form event listeners.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // If the user is already logged in, skip login and go to main screen
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Handle register link click — navigate to RegisterActivity
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Validate inputs and call the login API endpoint.
     * Shows a loading spinner during the network request.
     */
    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Validate inputs before making the API call
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        // Call the login API
        val credentials = mapOf("email" to email, "password" to password)
        ApiClient.getApiService(sessionManager).login(credentials)
            .enqueue(object : Callback<ApiResponse<LoginResponse>> {

                /**
                 * Handle successful HTTP response (2xx status codes).
                 * Check if the API operation itself was successful.
                 */
                override fun onResponse(
                    call: Call<ApiResponse<LoginResponse>>,
                    response: Response<ApiResponse<LoginResponse>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        val loginData = response.body()?.data
                        if (loginData != null) {
                            // Save the JWT token and email to SharedPreferences
                            sessionManager.saveSession(loginData.token, loginData.email)
                            navigateToMain()
                        }
                    } else {
                        val errorMsg = response.body()?.message ?: "Invalid email or password"
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                /**
                 * Handle network or server errors.
                 */
                override fun onFailure(call: Call<ApiResponse<LoginResponse>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(
                        this@LoginActivity,
                        "Connection failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /**
     * Navigate to the main activity and close the login screen.
     * Uses FLAG_ACTIVITY_NEW_TASK and CLEAR_TASK to prevent back navigation to login.
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
