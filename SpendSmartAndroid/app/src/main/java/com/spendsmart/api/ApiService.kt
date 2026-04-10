package com.spendsmart.api

import com.spendsmart.models.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit interface defining all SpendSmart API endpoints.
 * Each method corresponds to a Spring Boot REST controller endpoint.
 * Retrofit generates the implementation automatically at runtime.
 */
interface ApiService {

    // ==================== AUTHENTICATION ====================

    /**
     * Authenticate a user and get a JWT token.
     * POST /api/auth/login
     *
     * @param credentials map with "email" and "password" keys
     * @return the API response containing the JWT token and email
     */
    @POST("api/auth/login")
    fun login(@Body credentials: Map<String, String>): Call<ApiResponse<LoginResponse>>

    /**
     * Register a new user account.
     * POST /api/auth/register
     *
     * @param user the user registration data
     * @return the API response containing the created user
     */
    @POST("api/auth/register")
    fun register(@Body user: User): Call<ApiResponse<User>>

    // ==================== EXPENSES ====================

    /**
     * Get all expenses for the authenticated user.
     * GET /api/expenses
     *
     * @return the API response containing the list of expenses
     */
    @GET("api/expenses")
    fun getExpenses(): Call<ApiResponse<List<Expense>>>

    /**
     * Add a new expense.
     * POST /api/expenses
     *
     * @param expense the expense data to create
     * @return the API response containing the created expense
     */
    @POST("api/expenses")
    fun addExpense(@Body expense: Expense): Call<ApiResponse<Expense>>

    /**
     * Delete an expense by ID.
     * DELETE /api/expenses/{id}
     *
     * @param id the ID of the expense to delete
     * @return the API response confirming deletion
     */
    @DELETE("api/expenses/{id}")
    fun deleteExpense(@Path("id") id: Long): Call<ApiResponse<Void>>

    // ==================== BUDGETS ====================

    /**
     * Get all budgets for the authenticated user in a specific month.
     * GET /api/budgets/month/{month}
     *
     * @param month the month in 'YYYY-MM' format
     * @return the API response containing the list of budgets
     */
    @GET("api/budgets/month/{month}")
    fun getBudgetsByMonth(@Path("month") month: String): Call<ApiResponse<List<Budget>>>

    /**
     * Create a new monthly budget.
     * POST /api/budgets
     *
     * @param budget the budget data to create
     * @return the API response containing the created budget
     */
    @POST("api/budgets")
    fun createBudget(@Body budget: Budget): Call<ApiResponse<Budget>>

    // ==================== ALERTS ====================

    /**
     * Get all unread alerts for the authenticated user.
     * GET /api/alerts
     *
     * @return the API response containing the list of unread alerts
     */
    @GET("api/alerts")
    fun getAlerts(): Call<ApiResponse<List<Alert>>>

    /**
     * Mark a specific alert as read.
     * PUT /api/alerts/{id}/read
     *
     * @param id the ID of the alert to mark as read
     * @return the API response containing the updated alert
     */
    @PUT("api/alerts/{id}/read")
    fun markAlertAsRead(@Path("id") id: Long): Call<ApiResponse<Alert>>

    // ==================== SCORES ====================

    /**
     * Get the financial health score for the current month.
     * GET /api/scores/current
     *
     * @return the API response containing the current month's score
     */
    @GET("api/scores/current")
    fun getCurrentScore(): Call<ApiResponse<Score>>
}
