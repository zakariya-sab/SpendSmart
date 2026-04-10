package com.spendsmart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.spendsmart.R
import com.spendsmart.adapters.ExpenseAdapter
import com.spendsmart.api.ApiClient
import com.spendsmart.databinding.FragmentDashboardBinding
import com.spendsmart.models.ApiResponse
import com.spendsmart.models.Expense
import com.spendsmart.models.Score
import com.spendsmart.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Dashboard fragment — shows the user's financial summary.
 * Displays:
 *   - Financial health score with letter grade (A/B/C) and circular progress
 *   - Count of unread alerts
 *   - The 3 most recent expenses in a RecyclerView
 */
class DashboardFragment : Fragment() {

    /** View binding — nullable because Fragment views can be destroyed */
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    /** Session manager for getting the JWT token */
    private lateinit var sessionManager: SessionManager

    /**
     * Inflate the fragment layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Set up the UI and load data after the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        loadDashboardData()
    }

    /**
     * Load financial health score and recent expenses from the API.
     */
    private fun loadDashboardData() {
        loadCurrentScore()
        loadRecentExpenses()
        loadUnreadAlertsCount()
    }

    /**
     * Load the current month's financial health score and display it.
     */
    private fun loadCurrentScore() {
        ApiClient.getApiService(sessionManager).getCurrentScore()
            .enqueue(object : Callback<ApiResponse<Score>> {
                override fun onResponse(call: Call<ApiResponse<Score>>, response: Response<ApiResponse<Score>>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val score = response.body()?.data ?: return

                        // Display the grade letter in a colored circle
                        binding.tvGrade.text = score.grade
                        binding.tvScoreValue.text = "${score.value.toInt()}/100"

                        // Set progress bar value
                        binding.progressScore.progress = score.value.toInt()

                        // Color the grade badge based on letter
                        val color = when (score.grade) {
                            "A" -> R.color.grade_a
                            "B" -> R.color.grade_b
                            else -> R.color.grade_c
                        }
                        binding.cardGrade.setCardBackgroundColor(
                            ContextCompat.getColor(requireContext(), color)
                        )
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Score>>, t: Throwable) {
                    Toast.makeText(context, "Failed to load score", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Load the 3 most recent expenses and display them in a RecyclerView.
     */
    private fun loadRecentExpenses() {
        ApiClient.getApiService(sessionManager).getExpenses()
            .enqueue(object : Callback<ApiResponse<List<Expense>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<Expense>>>,
                    response: Response<ApiResponse<List<Expense>>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Show the 3 most recent expenses
                        val expenses = response.body()?.data?.takeLast(3)?.reversed() ?: emptyList()
                        binding.rvRecentExpenses.adapter = ExpenseAdapter(expenses) { _ -> }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<Expense>>>, t: Throwable) {
                    Toast.makeText(context, "Failed to load expenses", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Load the count of unread alerts and display it as a badge.
     */
    private fun loadUnreadAlertsCount() {
        ApiClient.getApiService(sessionManager).getAlerts()
            .enqueue(object : Callback<ApiResponse<List<com.spendsmart.models.Alert>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<com.spendsmart.models.Alert>>>,
                    response: Response<ApiResponse<List<com.spendsmart.models.Alert>>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val count = response.body()?.data?.size ?: 0
                        binding.tvAlertCount.text = "$count unread alert${if (count != 1) "s" else ""}"
                        binding.tvAlertCount.visibility = if (count > 0) View.VISIBLE else View.GONE
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<com.spendsmart.models.Alert>>>, t: Throwable) {}
            })
    }

    /**
     * Clean up the binding when the view is destroyed to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
