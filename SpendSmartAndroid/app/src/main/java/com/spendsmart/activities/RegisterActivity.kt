package com.spendsmart.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spendsmart.api.ApiClient
import com.spendsmart.databinding.ActivityRegisterBinding
import com.spendsmart.models.ApiResponse
import com.spendsmart.models.User
import com.spendsmart.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Registration screen for creating a new SpendSmart account.
 * Collects first name, last name, email, and password,
 * then calls POST /api/auth/register.
 * On success, navigates back to the LoginActivity.
 */
class RegisterActivity : AppCompatActivity() {

    /** View binding for type-safe access to layout views */
    private lateinit var binding: ActivityRegisterBinding

    /** Session manager needed to initialize ApiClient */
    private lateinit var sessionManager: SessionManager

    /**
     * Initialize the activity and set up the registration form.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Handle register button click
        binding.btnRegister.setOnClickListener {
            performRegistration()
        }

        // Handle login link — go back to login screen
        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    /**
     * Validate inputs and call the registration API endpoint.
     * Shows loading state during the network request.
     */
    private fun performRegistration() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Validate all required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password length
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        // Build the user object for the API request
        val user = User(
            id = null,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            role = null
        )

        // Call the registration API
        ApiClient.getApiService(sessionManager).register(user)
            .enqueue(object : Callback<ApiResponse<User>> {

                /**
                 * Handle successful HTTP response.
                 */
                override fun onResponse(
                    call: Call<ApiResponse<User>>,
                    response: Response<ApiResponse<User>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Account created! Please log in.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Navigate back to login screen
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        val errorMsg = response.body()?.message ?: "Registration failed"
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                /**
                 * Handle network or server errors.
                 */
                override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(
                        this@RegisterActivity,
                        "Connection failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
