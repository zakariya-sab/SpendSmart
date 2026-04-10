package com.spendsmart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.spendsmart.adapters.BudgetAdapter
import com.spendsmart.api.ApiClient
import com.spendsmart.databinding.FragmentBudgetsBinding
import com.spendsmart.models.ApiResponse
import com.spendsmart.models.Budget
import com.spendsmart.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Budgets fragment — displays monthly spending budgets with progress bars.
 * Features:
 *   - RecyclerView of budget cards with colored progress bars
 *   - FloatingActionButton to add a new budget
 *   - Each budget shows spent amount, limit, remaining, and percentage
 */
class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!

    /** Session manager for API authentication */
    private lateinit var sessionManager: SessionManager

    /** Budget adapter for the RecyclerView */
    private lateinit var budgetAdapter: BudgetAdapter

    /** Current month in 'YYYY-MM' format */
    private val currentMonth: String = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Set up RecyclerView
        budgetAdapter = BudgetAdapter(emptyList())
        binding.rvBudgets.adapter = budgetAdapter

        // FAB opens add budget dialog
        binding.fabAddBudget.setOnClickListener {
            showAddBudgetDialog()
        }

        loadBudgets()
    }

    /**
     * Load budgets for the current month from the API.
     */
    private fun loadBudgets() {
        binding.progressBar.visibility = View.VISIBLE
        ApiClient.getApiService(sessionManager).getBudgetsByMonth(currentMonth)
            .enqueue(object : Callback<ApiResponse<List<Budget>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<Budget>>>,
                    response: Response<ApiResponse<List<Budget>>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        val budgets = response.body()?.data ?: emptyList()
                        budgetAdapter.updateData(budgets)
                        binding.tvEmpty.visibility = if (budgets.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<Budget>>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Failed to load budgets", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Show a dialog for adding a new budget.
     */
    private fun showAddBudgetDialog() {
        val dialogView = layoutInflater.inflate(com.spendsmart.R.layout.dialog_add_budget, null)
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("New Budget")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val maxAmountStr = dialogView.findViewById<android.widget.EditText>(
                    com.spendsmart.R.id.etMaxAmount
                ).text.toString()

                if (maxAmountStr.isNotEmpty()) {
                    val budget = Budget(
                        id = null,
                        maxAmount = maxAmountStr.toDouble(),
                        spentAmount = null,
                        remainingAmount = null,
                        percentageUsed = null,
                        month = currentMonth,
                        categoryId = 1L, // Default — improve with category picker
                        categoryName = null,
                        categoryColor = null
                    )
                    createBudget(budget)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Call the API to create a new budget and reload the list.
     *
     * @param budget the budget data to save
     */
    private fun createBudget(budget: Budget) {
        ApiClient.getApiService(sessionManager).createBudget(budget)
            .enqueue(object : Callback<ApiResponse<Budget>> {
                override fun onResponse(
                    call: Call<ApiResponse<Budget>>,
                    response: Response<ApiResponse<Budget>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "Budget created!", Toast.LENGTH_SHORT).show()
                        loadBudgets()
                    } else {
                        Toast.makeText(
                            context,
                            response.body()?.message ?: "Failed to create budget",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Budget>>, t: Throwable) {
                    Toast.makeText(context, "Failed to create budget", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
