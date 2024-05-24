package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen()
        }
    }

    @Composable
    fun DashboardScreen() {
        // Define your buttons here
        Button(onClick = {
            val intent = Intent(this@DashboardActivity, PersonalOverviewActivity::class.java)
            startActivity(intent)
        }) {
            Text("Personal Overview")
        }

        Button(onClick = {
            val intent = Intent(this@DashboardActivity, PersonalSynthesisActivity::class.java)
            startActivity(intent)
        }) {
            Text("Personal Synthesis")
        }

        Button(onClick = {
            val intent = Intent(this@DashboardActivity, AnnouncementsActivity::class.java)
            startActivity(intent)
        }) {
            Text("Announcements")
        }

        Button(onClick = {
            val intent = Intent(this@DashboardActivity, GroupOverviewActivity::class.java)
            startActivity(intent)
        }) {
            Text("Group Overview")
        }

        Button(onClick = {
            val intent = Intent(this@DashboardActivity, GroupSynthesisActivity::class.java)
            startActivity(intent)
        }) {
            Text("Group Synthesis")
        }
    }

}
