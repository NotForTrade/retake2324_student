package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class OverviewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val tableLayout: TableLayout = findViewById(R.id.tableLayout)

        val students = listOf("Student 1", "Student 2", "Student 3", "Student 4") // This can vary
        val components = listOf("Component 1", "Skill 1", "Skill 2", "Skill 3")
        val scores = listOf(
            listOf("85", "88", "84", "81"), // Scores for Component 1
            listOf("90", "92", "89", "85"), // Scores for Skill 1
            listOf("78", "80", "76", "82"), // Scores for Skill 2
            listOf("91", "94", "89", "87")  // Scores for Skill 3
        )

        // Check if scores size matches components size
        if (scores.size != components.size) {
            Log.e("OverviewActivity", "Mismatch between components and scores size")
            return
        }

        // Create header row
        val headerRow = TableRow(this)
        val headerComponentTextView = TextView(this)
        headerComponentTextView.text = "Components"
        headerComponentTextView.setPadding(8, 8, 8, 8)
        headerComponentTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        headerRow.addView(headerComponentTextView)

        for (student in students) {
            val studentTextView = TextView(this)
            studentTextView.text = student
            studentTextView.setPadding(8, 8, 8, 8)
            studentTextView.setTypeface(null, android.graphics.Typeface.BOLD)
            headerRow.addView(studentTextView)
        }
        tableLayout.addView(headerRow)

        // Create data rows
        for (i in components.indices) {
            val tableRow = TableRow(this)

            val componentTextView = TextView(this)
            componentTextView.text = components[i]
            componentTextView.setPadding(8, 8, 8, 8)
            tableRow.addView(componentTextView)

            for (j in students.indices) {
                // Check if scores list for the component has the right size
                if (j < scores[i].size) {
                    val scoreTextView = TextView(this)
                    scoreTextView.text = scores[i][j]
                    scoreTextView.setPadding(8, 8, 8, 8)
                    tableRow.addView(scoreTextView)
                } else {
                    Log.e("OverviewActivity", "Mismatch between students and scores for component $i")
                }
            }

            tableLayout.addView(tableRow)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            // Handle other menu items here
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
