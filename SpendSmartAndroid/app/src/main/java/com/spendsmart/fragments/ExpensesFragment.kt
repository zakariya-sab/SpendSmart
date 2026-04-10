package com.spendsmart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.spendsmart.adapters.ExpenseAdapter
import com.spendsmart.api.ApiClient
import com.spendsmart.databinding.FragmentExpensesBinding
import com.spendsmart.models.ApiResponse
import com.spendsmart.models.Expense
import com.spendsmart.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Expenses fragment — displays all user expenses.
 * Features:
 *   - RecyclerView listing all expenses with category color indicators
 *   - FloatingActionButton to open a dialog for adding a new expense
 *   - Swipe-to-delete functionality
 */
class ExpensesFragment : Fragment() {

    /** View binding — nullable to avoid memory leaks */
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    /** Session manager for API authentication */
    private lateinit var sessionManager: SessionManager

    /** Expense adapter for the RecyclerView */
    private lateinit var expenseAdapter: ExpenseAdapter

    /** Mutable list of expenses displayed in the RecyclerView */
    private val expenses = mutableListOf<Expense>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Set up RecyclerView with the expense adapter
        expenseAdapter = ExpenseAdapter(expenses) { expense ->
            // Delete the expense when the delete button is tapped
            expense.id?.let { deleteExpense(it) }
        }
        binding.rvExpenses.adapter = expenseAdapter

        // FAB click opens the add expense dialog
        binding.fabAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }

        loadExpenses()
    }

    /**
     * Load all expenses for the authenticated user from the API.
     */
    private fun loadExpenses() {
        binding.progressBar.visibility = View.VISIBLE
        ApiClient.getApiService(sessionManager).getExpenses()
            .enqueue(object : Callback<ApiResponse<List<Expense>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<Expense>>>,
                    response: Response<ApiResponse<List<Expense>>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        expenses.clear()
                        expenses.addAll(response.body()?.data?.reversed() ?: emptyList())
                        expenseAdapter.notifyDataSetChanged()

                        // Show empty state if no expenses
                        binding.tvEmpty.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<Expense>>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Failed to load expenses", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Delete an expense by ID and refresh the list.
     *
     * @param id the ID of the expense to delete
     */
    private fun deleteExpense(id: Long) {
        ApiClient.getApiService(sessionManager).deleteExpense(id)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(
                    call: Call<ApiResponse<Void>>,
                    response: Response<ApiResponse<Void>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show()
                        loadExpenses()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(context, "Failed to delete expense", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Show a dialog for adding a new expense.
     * Uses an AlertDialog with a custom layout for the form fields.
     */
    private fun showAddExpenseDialog() {
        // Simple dialog implementation — in production use a proper DialogFragment
        val dialogView = layoutInflater.inflate(
            com.spendsmart.R.layout.dialog_add_expense, null
        )
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Expense")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amountStr = dialogView.findViewById<android.widget.EditText>(
                    com.spendsmart.R.id.etAmount
                ).text.toString()
                val description = dialogView.findViewById<android.widget.EditText>(
                    com.spendsmart.R.id.etDescription
                ).text.toString()

                if (amountStr.isNotEmpty()) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val expense = Expense(
                        id = null,
                        amount = amountStr.toDouble(),
                        date = today,
                        description = description,
                        categoryId = 1L, // Default category ID — improve with a category picker
                        categoryName = null,
                        categoryColor = null
                    )
                    addExpense(expense)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Call the API to save a new expense and reload the list.
     *
     * @param expense the expense data to save
     */
    private fun addExpense(expense: Expense) {
        ApiClient.getApiService(sessionManager).addExpense(expense)
            .enqueue(object : Callback<ApiResponse<Expense>> {
                override fun onResponse(
                    call: Call<ApiResponse<Expense>>,
                    response: Response<ApiResponse<Expense>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "Expense added!", Toast.LENGTH_SHORT).show()
                        loadExpenses()
                    } else {
                        Toast.makeText(context, response.body()?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Expense>>, t: Throwable) {
                    Toast.makeText(context, "Failed to add expense", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
