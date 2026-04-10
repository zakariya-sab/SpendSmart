package com.spendsmart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spendsmart.R
import com.spendsmart.databinding.ItemAlertBinding
import com.spendsmart.models.Alert

/**
 * RecyclerView adapter for displaying a list of financial alerts.
 * Unread alerts are shown with an orange background.
 * Tapping an alert calls the onAlertClick callback (to mark it as read).
 * Alert type (WARNING/EXCEEDED) is shown as a colored badge.
 *
 * @param alerts        the mutable list of alerts to display
 * @param onAlertClick  callback invoked when an alert row is tapped
 */
class AlertAdapter(
    private val alerts: MutableList<Alert>,
    private val onAlertClick: (Alert) -> Unit
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    /**
     * ViewHolder for a single alert row.
     */
    inner class AlertViewHolder(val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AlertViewHolder(binding)
    }

    /**
     * Bind alert data to the row views.
     * Highlights unread alerts with orange background and applies type badge color.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the alert in the list
     */
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        with(holder.binding) {
            // Alert message text
            tvMessage.text = alert.message

            // Date and category context
            tvDate.text = alert.date.take(10)
            tvCategory.text = alert.categoryName ?: ""

            // Alert type badge — red for EXCEEDED, orange for WARNING
            tvType.text = alert.type
            val badgeColor = if (alert.type == "EXCEEDED") R.color.progress_high else R.color.progress_medium
            tvType.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, badgeColor))

            // Highlight unread alerts with a light orange tint
            val bgColor = if (!alert.isRead)
                ContextCompat.getColor(holder.itemView.context, R.color.alert_unread_bg)
            else
                ContextCompat.getColor(holder.itemView.context, android.R.color.white)
            holder.itemView.setBackgroundColor(bgColor)

            // Tap to mark as read
            holder.itemView.setOnClickListener { onAlertClick(alert) }
        }
    }

    override fun getItemCount(): Int = alerts.size
}
