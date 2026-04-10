package com.spendsmart.models

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper matching the Spring Boot ApiResponseDTO.
 * All API endpoints return data in this format:
 * { "success": boolean, "message": String, "data": T }
 *
 * @param T the type of the data payload
 */
data class ApiResponse<T>(
    /** Whether the operation completed successfully */
    val success: Boolean,
    /** Human-readable result message */
    val message: String,
    /** The response data payload */
    val data: T?
)

/**
 * Model representing the JWT login response data.
 */
data class LoginResponse(
    /** The JWT authentication token */
    val token: String,
    /** The authenticated user's email address */
    val email: String
)

/**
 * Model representing a SpendSmart user account.
 */
data class User(
    /** Unique user identifier */
    val id: Long?,
    /** User's first name */
    val firstName: String,
    /** User's last name */
    val lastName: String,
    /** User's email address */
    val email: String,
    /** Password — only included in registration requests */
    val password: String?,
    /** User role: "USER" or "ADMIN" */
    val role: String?
)

/**
 * Model representing a single expense entry.
 */
data class Expense(
    /** Unique expense identifier */
    val id: Long?,
    /** Monetary amount of the expense */
    val amount: Double,
    /** Date when the expense occurred (ISO format string) */
    val date: String,
    /** Optional description of the expense */
    val description: String?,
    /** ID of the spending category */
    val categoryId: Long,
    /** Category display name — returned in API responses */
    val categoryName: String?,
    /** Category hex color code */
    val categoryColor: String?
)

/**
 * Model representing a monthly spending budget.
 */
data class Budget(
    /** Unique budget identifier */
    val id: Long?,
    /** Maximum spending limit */
    val maxAmount: Double,
    /** Current amount spent */
    val spentAmount: Double?,
    /** Remaining amount (maxAmount - spentAmount) */
    val remainingAmount: Double?,
    /** Percentage of budget used (0-100) */
    val percentageUsed: Double?,
    /** The month in 'YYYY-MM' format */
    val month: String,
    /** ID of the associated category */
    val categoryId: Long,
    /** Category display name */
    val categoryName: String?,
    /** Category hex color */
    val categoryColor: String?
)

/**
 * Model representing a financial alert.
 */
data class Alert(
    /** Unique alert identifier */
    val id: Long,
    /** Human-readable alert message */
    val message: String,
    /** When the alert was created */
    val date: String,
    /** Whether the user has read this alert */
    @SerializedName("isRead")
    val isRead: Boolean,
    /** Alert severity: "WARNING" or "EXCEEDED" */
    val type: String,
    /** The category name that triggered this alert */
    val categoryName: String?,
    /** The budget month */
    val budgetMonth: String?
)

/**
 * Model representing a monthly financial health score.
 */
data class Score(
    /** Unique score identifier */
    val id: Long,
    /** Numerical score from 0 to 100 */
    val value: Double,
    /** Letter grade: "A", "B", or "C" */
    val grade: String,
    /** The month in 'YYYY-MM' format */
    val month: String,
    /** When the score was calculated */
    val date: String
)

/**
 * Model representing a spending category.
 */
data class Category(
    /** Unique category identifier */
    val id: Long,
    /** Category display name */
    val name: String,
    /** Hex color code */
    val color: String,
    /** Icon name */
    val icon: String?
)
