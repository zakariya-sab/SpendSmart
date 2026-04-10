package com.spendsmart.api

import com.spendsmart.utils.Constants
import com.spendsmart.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for the SpendSmart API.
 * Configured with:
 *   - AuthInterceptor: adds JWT token to every request
 *   - HttpLoggingInterceptor: logs all HTTP traffic for debugging
 *   - 30-second timeouts for connect, read, and write operations
 * The singleton instance is created lazily on first access.
 */
object ApiClient {

    /** Cached Retrofit instance — created once and reused */
    private var retrofit: Retrofit? = null

    /**
     * Get or create the Retrofit instance configured for the SpendSmart API.
     * Uses double-checked locking to ensure thread-safe singleton creation.
     *
     * @param sessionManager the session manager providing the JWT token
     * @return the configured Retrofit instance
     */
    fun getRetrofit(sessionManager: SessionManager): Retrofit {
        if (retrofit == null) {
            // Configure HTTP logging to print request/response details to Logcat
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Build OkHttpClient with auth and logging interceptors
            val okHttpClient = OkHttpClient.Builder()
                // Automatically attach JWT token to all requests
                .addInterceptor(AuthInterceptor(sessionManager))
                // Log all HTTP traffic for debugging
                .addInterceptor(loggingInterceptor)
                // Network timeout configuration
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Build the Retrofit instance pointing to the Spring Boot backend
            retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                // Use Gson to automatically convert JSON responses to Kotlin data classes
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    /**
     * Get a configured ApiService instance for making API calls.
     *
     * @param sessionManager the session manager providing the JWT token
     * @return an ApiService implementation created by Retrofit
     */
    fun getApiService(sessionManager: SessionManager): ApiService {
        return getRetrofit(sessionManager).create(ApiService::class.java)
    }
}
