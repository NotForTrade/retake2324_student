package com.example.retake2324_student.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Announcement
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.Score
import com.example.retake2324_student.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
import org.ktorm.entity.sequenceOf


class AnnouncementsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        val studentId = intent.getIntExtra("studentId", -1)

        setContent {
            MaterialTheme {
                AnnouncementsLoader(app, studentId)
            }
        }
    }


    private suspend fun fetchObjects(database: Database): List<Announcement> {
        try {
            // Fetch the students from the same group
            val announcements = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Announcements).toList()
            }
            return announcements
        } catch (e: Exception) {
            Log.e("SQL FETCHING ERROR", e.toString())
            return listOf()
        }
    }


    @Composable
    fun AnnouncementsLoader(app: App, studentId: Int) {
        // MutableState to hold the lists
        var announcements by remember { mutableStateOf(listOf<Announcement>()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            val database = app.getDatabase() // Reuse the existing database connection
            val fetchedAnnouncements = fetchObjects(database)

            // Update the states
            announcements = fetchedAnnouncements
            isLoading = false
        }

        if (isLoading) {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        } else {
            AnnouncementsScreen(announcements, studentId)
        }
    }

    @Composable
    fun AnnouncementsScreen(announcements: List<Announcement>, studentId: Int) {
        val context = LocalContext.current
        var expandedStates by remember { mutableStateOf(List(announcements.size) { false }) }

        Scaffold(
            topBar = { Header("Announcements") },
            bottomBar = { Footer(studentId) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {

                if (announcements.isNotEmpty()) {
                    announcements.forEachIndexed {index, announcement ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .animateContentSize(),
                            onClick = {
                                expandedStates = expandedStates.toMutableList().apply {
                                    this[index] = !this[index]
                                }
                            }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = announcement.title, style = MaterialTheme.typography.titleLarge)
                                        Text(text = "By ${announcement.tutor.firstName + " " + announcement.tutor.lastName}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = announcement.datetime.toString(), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (expandedStates[index]) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = announcement.content, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}