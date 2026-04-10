package com.spendsmart.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.spendsmart.adapters.AlertAdapter
import com.spendsmart.api.ApiClient
import com.spendsmart.databinding.FragmentAlertsBinding
import com.spendsmart.models.Alert
import com.spendsmart.models.ApiResponse
import com.spendsmart.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Alerts fragment — displays financial budget alerts.
 * Features:
 *   - RecyclerView listing all unread alerts
 *   - Tap an alert to mark it as read
 *   - Unread alerts displayed with orange background
 *   - WARNING and EXCEEDED type badges
 */
class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    /** Session manager for API authentication */
    private lateinit var sessionManager: SessionManager

    /** Alert adapter for the RecyclerView */
    private lateinit var alertAdapter: AlertAdapter

    /** Mutable list of alerts */
    private val alerts = mutableListOf<Alert>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Set up RecyclerView with tap-to-mark-as-read behavior
        alertAdapter = AlertAdapter(alerts) { alert ->
            if (!alert.isRead) markAlertAsRead(alert.id)
        }
        binding.rvAlerts.adapter = alertAdapter

        loadAlerts()
    }

    /**
     * Load all unread alerts from the API.
     */
    private fun loadAlerts() {
        binding.progressBar.visibility = View.VISIBLE
        ApiClient.getApiService(sessionManager).getAlerts()
            .enqueue(object : Callback<ApiResponse<List<Alert>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<Alert>>>,
                    response: Response<ApiResponse<List<Alert>>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        alerts.clear()
                        alerts.addAll(response.body()?.data ?: emptyList())
                        alertAdapter.notifyDataSetChanged()
                        binding.tvEmpty.visibility = if (alerts.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<Alert>>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Failed to load alerts", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Mark a single alert as read and update the UI.
     *
     * @param alertId the ID of the alert to mark as read
     */
    private fun markAlertAsRead(alertId: Long) {
        ApiClient.getApiService(sessionManager).markAlertAsRead(alertId)
            .enqueue(object : Callback<ApiResponse<Alert>> {
                override fun onResponse(
                    call: Call<ApiResponse<Alert>>,
                    response: Response<ApiResponse<Alert>>
                ) {
                    if (response.isSuccessful) {
                        // Update the local list and refresh the adapter
                        val index = alerts.indexOfFirst { it.id == alertId }
                        if (index != -1) {
                            alerts[index] = alerts[index].copy(isRead = true)
                            alertAdapter.notifyItemChanged(index)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Alert>>, t: Throwable) {
                    Toast.makeText(context, "Failed to mark alert as read", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
