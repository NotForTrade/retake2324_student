package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Check if user is logged in
        val isLoggedIn = checkLoginStatus()

        if (!isLoggedIn) {
            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity
        }

    }

    private fun checkLoginStatus(): Boolean {
        // Replace with actual login status check logic
        // For example, checking SharedPreferences or a database
        return false // Default to false for this example
    }

}