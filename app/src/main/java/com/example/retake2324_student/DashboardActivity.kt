package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val buttonPersonalOverview: Button = findViewById(R.id.button_personal_overview)
        val buttonPersonalSynthesis: Button = findViewById(R.id.button_personal_synthesis)
        val buttonAnnouncements: Button = findViewById(R.id.button_announcements)
        val buttonGroupOverview: Button = findViewById(R.id.button_group_overview)
        val buttonGroupSynthesis: Button = findViewById(R.id.button_group_synthesis)

        buttonPersonalOverview.setOnClickListener {
            val intent = Intent(this, PersonalOverviewActivity::class.java)
            startActivity(intent)
        }

        buttonPersonalSynthesis.setOnClickListener {
            val intent = Intent(this, PersonalSynthesisActivity::class.java)
            startActivity(intent)
        }

        buttonAnnouncements.setOnClickListener {
            val intent = Intent(this, AnnouncementsActivity::class.java)
            startActivity(intent)
        }

        buttonGroupOverview.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        buttonGroupSynthesis.setOnClickListener {
            val intent = Intent(this, GroupSynthesisActivity::class.java)
            startActivity(intent)
        }


    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_dashboard
    }


}
