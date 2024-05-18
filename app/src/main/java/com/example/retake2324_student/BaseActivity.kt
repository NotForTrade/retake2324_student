package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    protected lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        // Clear default selection with a delay
        Handler(Looper.getMainLooper()).post {
            val menu = bottomNavigationView.menu
            for (i in 0 until menu.size()) {
                menu.getItem(i).isChecked = false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.DashboardFragment -> {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.ProfileFragment -> {
                // Handle Profile navigation
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
