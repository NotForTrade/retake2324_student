package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class AnnouncementsActivity : BaseActivity() {

    private lateinit var errorTextView: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Start the coroutine to call the database related instructions
        CoroutineScope(Dispatchers.Main).launch {
            val database = app.getDatabase() // Reuse the existing database connection
            loadAndDisplayComponents(database)
        }

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_announcements
    }


    private suspend fun loadAndDisplayComponents(database: Database) {
        val app = application as App

        try {
            val announcements = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Announcements)
                    .toList()
            }
            if (announcements.isEmpty()) {
                app.displayException(EmptyDbListResultException("No components found"))
            }
            val adapter = AnnouncementsAdapter(announcements)
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            app.displayException(e)
        }
    }

    private fun displayError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

}


