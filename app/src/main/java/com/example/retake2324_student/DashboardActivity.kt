package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

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
    }
}
