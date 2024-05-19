package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val buttonOverview: Button = findViewById(R.id.button_overview)
        val buttonSynthesis: Button = findViewById(R.id.button_synthesis)
        val buttonAnnouncements: Button = findViewById(R.id.button_announcements)

        buttonOverview.setOnClickListener {
            val intent = Intent(this, OverviewActivity::class.java)
            startActivity(intent)
        }

        buttonSynthesis.setOnClickListener {
            val intent = Intent(this, SynthesisActivity::class.java)
            startActivity(intent)
        }

        buttonAnnouncements.setOnClickListener {
            val intent = Intent(this, AnnouncementsActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.DashboardFragment -> {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.ProfileFragment -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
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
