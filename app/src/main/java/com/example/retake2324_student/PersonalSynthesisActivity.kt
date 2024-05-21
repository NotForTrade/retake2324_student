package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PersonalSynthesisActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val tableLayout: TableLayout = findViewById(R.id.tableLayout)

        val students = listOf("Student 1", "Student 2", "Student 3", "Student 4") // This can vary
        val components = listOf("Component 1") // Only show component scores
        val scores = listOf(
            listOf("85", "88", "84", "81") // Scores for Component 1
        )

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
                if (j < scores[i].size) {
                    val scoreTextView = TextView(this)
                    scoreTextView.text = scores[i][j]
                    scoreTextView.setPadding(8, 8, 8, 8)
                    tableRow.addView(scoreTextView)
                } else {
                    Log.e("SynthesisActivity", "Mismatch between students and scores for component $i")
                }
            }

            tableLayout.addView(tableRow)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_personal_synthesis
    }

}
