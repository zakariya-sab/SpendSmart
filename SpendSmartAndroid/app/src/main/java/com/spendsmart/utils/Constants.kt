package com.spendsmart.utils

/**
 * Application-wide constants used across the SpendSmart Android app.
 * Contains the backend API base URL and SharedPreferences keys.
 */
object Constants {

    /**
     * Base URL for the Spring Boot REST API.
     * - Use "http://10.0.2.2:8080/" when testing on the Android Emulator
     *   (10.0.2.2 maps to the host machine's localhost)
     * - Change to your computer's IP address when testing on a real device
     *   (e.g., "http://192.168.1.100:8080/")
     */
    const val BASE_URL = "http://10.0.2.2:8080/"

    /** SharedPreferences file name for storing session data */
    const val PREFS_NAME = "SpendSmartPrefs"

    /** Key for storing the JWT authentication token */
    const val KEY_TOKEN = "jwt_token"

    /** Key for storing the logged-in user's email address */
    const val KEY_EMAIL = "user_email"

    /** Primary brand color used throughout the app (orange) */
    const val PRIMARY_COLOR = "#F97316"
}
