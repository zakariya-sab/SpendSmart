package com.spendsmart.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spendsmart.R
import com.spendsmart.databinding.ItemBudgetBinding
import com.spendsmart.models.Budget

/**
 * RecyclerView adapter for displaying a list of monthly budgets.
 * Each card shows the category name, spent/max amounts, a progress bar,
 * and the remaining amount. Progress bar color changes based on usage:
 * green < 50%, orange 50-80%, red > 80%.
 *
 * @param budgets the initial list of budgets to display
 */
class BudgetAdapter(private var budgets: List<Budget>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    /**
     * ViewHolder for a single budget card.
     *
     * @param binding the ItemBudgetBinding containing the card views
     */
    inner class BudgetViewHolder(val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Update the adapter's data and refresh the list.
     *
     * @param newBudgets the new list of budgets to display
     */
    fun updateData(newBudgets: List<Budget>) {
        budgets = newBudgets
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BudgetViewHolder(binding)
    }

    /**
     * Bind budget data to the card views.
     * Sets category name, color, amounts, progress bar, and remaining amount.
     */
    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        val percentage = budget.percentageUsed ?: 0.0

        with(holder.binding) {
            // Category name
            tvCategoryName.text = budget.categoryName ?: "Unknown"

            // Category color dot
            if (!budget.categoryColor.isNullOrEmpty()) {
                try {
                    viewCategoryColor.setBackgroundColor(Color.parseColor(budget.categoryColor))
                } catch (e: IllegalArgumentException) {
                    viewCategoryColor.setBackgroundColor(Color.GRAY)
                }
            }

            // Amounts display
            tvAmounts.text = "${budget.spentAmount?.toInt() ?: 0} / ${budget.maxAmount.toInt()} MAD"

            // Progress bar value (0-100)
            progressBudget.progress = percentage.toInt()

            // Color-code the progress bar based on usage percentage
            val progressColor = when {
                percentage < 50 -> ContextCompat.getColor(holder.itemView.context, R.color.progress_low)
                percentage < 80 -> ContextCompat.getColor(holder.itemView.context, R.color.progress_medium)
                else -> ContextCompat.getColor(holder.itemView.context, R.color.progress_high)
            }
            progressBudget.progressTintList = android.content.res.ColorStateList.valueOf(progressColor)

            // Remaining amount
            val remaining = budget.remainingAmount?.toInt() ?: 0
            tvRemaining.text = "$remaining MAD remaining"
            tvRemaining.setTextColor(
                if (percentage >= 80)
                    ContextCompat.getColor(holder.itemView.context, R.color.progress_high)
                else
                    ContextCompat.getColor(holder.itemView.context, R.color.progress_low)
            )
        }
    }

    override fun getItemCount(): Int = budgets.size
}
