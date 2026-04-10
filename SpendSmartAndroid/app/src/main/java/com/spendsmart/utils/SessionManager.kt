package com.spendsmart.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages the user's authentication session using SharedPreferences.
 * Stores and retrieves the JWT token and user email across app restarts.
 * Used by all activities and fragments that need authentication state.
 *
 * @param context the application context used to access SharedPreferences
 */
class SessionManager(context: Context) {

    /** SharedPreferences instance for persistent storage */
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Save the JWT token and user email after a successful login.
     *
     * @param token the JWT authentication token received from the server
     * @param email the logged-in user's email address
     */
    fun saveSession(token: String, email: String) {
        prefs.edit().apply {
            putString(Constants.KEY_TOKEN, token)
            putString(Constants.KEY_EMAIL, email)
            apply()
        }
    }

    /**
     * Retrieve the stored JWT token.
     *
     * @return the JWT token string, or null if not stored
     */
    fun getToken(): String? = prefs.getString(Constants.KEY_TOKEN, null)

    /**
     * Retrieve the stored user email address.
     *
     * @return the user's email address, or null if not stored
     */
    fun getUserEmail(): String? = prefs.getString(Constants.KEY_EMAIL, null)

    /**
     * Check whether the user is currently logged in.
     * A user is considered logged in if a JWT token exists in storage.
     *
     * @return true if a token is stored, false otherwise
     */
    fun isLoggedIn(): Boolean = getToken() != null

    /**
     * Clear all session data to log out the user.
     * Removes both the JWT token and the user email from storage.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
