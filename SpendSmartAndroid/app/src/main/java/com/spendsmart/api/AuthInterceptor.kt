package com.spendsmart.api

import com.spendsmart.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that automatically attaches the JWT token to every HTTP request.
 * Adds the 'Authorization: Bearer {token}' header required by the Spring Boot API.
 * If no token is stored (user not logged in), the request is sent without the header.
 *
 * @param sessionManager the session manager used to retrieve the stored JWT token
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    /**
     * Intercept the outgoing HTTP request and add the JWT Authorization header.
     *
     * @param chain the OkHttp interceptor chain
     * @return the response after processing the modified request
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getToken()

        // If a token exists, add the Authorization header
        return if (token != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            // No token — send the request as-is (for login/register endpoints)
            chain.proceed(originalRequest)
        }
    }
}
