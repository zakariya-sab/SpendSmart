package com.spendsmart.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spendsmart.databinding.ItemExpenseBinding
import com.spendsmart.models.Expense

/**
 * RecyclerView adapter for displaying a list of expenses.
 * Each row shows the category color dot, category name, description, date, and amount.
 * Provides a delete callback invoked when the delete button is tapped.
 *
 * @param expenses     the list of expenses to display
 * @param onDeleteClick callback invoked when the user taps the delete button on an expense row
 */
class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    /**
     * ViewHolder holding the bound views for a single expense row.
     *
     * @param binding the ItemExpenseBinding containing the row views
     */
    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Create a new ViewHolder by inflating the expense item layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExpenseViewHolder(binding)
    }

    /**
     * Bind the expense data to the views in the ViewHolder.
     * Sets the category color, name, description, date, amount, and delete button.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the expense in the list
     */
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        with(holder.binding) {
            // Set category color indicator dot
            if (!expense.categoryColor.isNullOrEmpty()) {
                try {
                    viewCategoryColor.setBackgroundColor(Color.parseColor(expense.categoryColor))
                } catch (e: IllegalArgumentException) {
                    viewCategoryColor.setBackgroundColor(Color.GRAY)
                }
            }

            // Display category name and description
            tvCategoryName.text = expense.categoryName ?: "Unknown"
            tvDescription.text = expense.description?.takeIf { it.isNotEmpty() } ?: "No description"

            // Display date and amount
            tvDate.text = expense.date.take(10) // Show only YYYY-MM-DD part
            tvAmount.text = "${expense.amount} MAD"

            // Set up delete button click
            btnDelete.setOnClickListener { onDeleteClick(expense) }
        }
    }

    /**
     * Return the total number of expense items.
     */
    override fun getItemCount(): Int = expenses.size
}
