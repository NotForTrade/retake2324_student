package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.view.MenuItem
import android.widget.FrameLayout
import com.example.retake2324_student.DbAccessException
import com.example.retake2324_student.R
import com.example.retake2324_student.core.App
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Centralized exception handling
        val app = application as App
        lifecycleScope.launch {
            app.exceptionFlow.collect { exception ->
                exception?.let {
                    handleException(it)
                }
            }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        val container: FrameLayout = findViewById(R.id.container)
        layoutInflater.inflate(getLayoutResourceId(), container, true)

    }

    // Each child activity will provide its layout resource ID
    protected open fun getLayoutResourceId(): Int {
        return 0
    }


    // Code to control how exceptions are shown to the app user
    // We can filter out some exceptions and rename their messages if we want
    // Each activity can override this function to to custom routing (though it's better to have everything here)
    private fun handleException(exception: Exception) {
        when (exception) {
            is java.sql.SQLException -> displayException(DbAccessException("Couldn't connect to DB"))
            else -> displayException(exception)
        }
    }

    // Each activity can override how an exception gets displayed
    private fun displayException(exception: Exception) {
        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.ProfileFragment -> {

                // Avoid reloading the page if we currently are on it
                if (getLayoutResourceId() != R.layout.activity_profile) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                return true
            }

            R.id.DashboardFragment -> {

                // Avoid reloading the page if we currently are on it
                if (getLayoutResourceId() != R.layout.activity_dashboard){
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
                return true
            }

            R.id.LogoutFragment -> {
                // Handle Logout navigation
                return true
            }
        }
        return false
    }


}