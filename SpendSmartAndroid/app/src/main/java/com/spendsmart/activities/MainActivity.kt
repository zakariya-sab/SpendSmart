package com.spendsmart.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spendsmart.R
import com.spendsmart.databinding.ActivityMainBinding
import com.spendsmart.fragments.AlertsFragment
import com.spendsmart.fragments.BudgetsFragment
import com.spendsmart.fragments.DashboardFragment
import com.spendsmart.fragments.ExpensesFragment
import com.spendsmart.utils.SessionManager

/**
 * Main activity hosting the BottomNavigationView and the four tab fragments.
 * Displays after a successful login.
 * Contains four tabs: Dashboard, Expenses, Budgets, Alerts.
 * Handles navigation between fragments and the logout action.
 */
class MainActivity : AppCompatActivity() {

    /** View binding for type-safe access to layout views */
    private lateinit var binding: ActivityMainBinding

    /** Session manager for checking login state and logout */
    private lateinit var sessionManager: SessionManager

    /**
     * Initialize the activity, set up bottom navigation, and load the default fragment.
     *
     * @param savedInstanceState the saved instance state bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Redirect to login if session expired
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Load the Dashboard as the default tab on startup
        loadFragment(DashboardFragment())

        // Handle bottom navigation tab selection
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_expenses -> loadFragment(ExpensesFragment())
                R.id.nav_budgets -> loadFragment(BudgetsFragment())
                R.id.nav_alerts -> loadFragment(AlertsFragment())
            }
            true
        }
    }

    /**
     * Replace the current fragment in the main content container.
     *
     * @param fragment the Fragment to display
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    /**
     * Log out the user: clear the session and navigate to the login screen.
     * Called from the action bar menu or fragments.
     */
    fun logout() {
        sessionManager.clearSession()
        navigateToLogin()
    }

    /**
     * Navigate to the LoginActivity and clear the back stack.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
