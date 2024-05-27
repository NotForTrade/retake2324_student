package com.example.retake2324_student.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.Score
import com.example.retake2324_student.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.toList
import org.ktorm.entity.sequenceOf


class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        val studentId = intent.getIntExtra("studentId", -1)
        // Only fetching from profileId to avoid risks
        val profileId = intent.getIntExtra("studentId", -1)

        setContent {
            MaterialTheme {
                ProfileLoader(app, studentId, profileId)
            }
        }
    }


    private suspend fun fetchObjects(database: Database, profileId: Int): User {

        try {
            // Fetch the profile
            val profile = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Users).find { it.Id eq profileId }
            }
            if (profile != null) {
                return profile
            } else {
                Log.e("SQL FETCHING ERROR", "User with $profileId not found on the database!")
                return User()
            }
        } catch (e: Exception) {
            Log.e("SQL FETCHING ERROR", e.toString())
            return User()
        }
    }


    @Composable
    fun ProfileLoader(app: App, studentId: Int, profileId: Int) {
        // MutableState to hold the lists
        var profile by remember { mutableStateOf(User()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            val database = app.getDatabase() // Reuse the existing database connection
            val fetchedProfile = fetchObjects(database, studentId)

            // Update the states
            profile = fetchedProfile
            isLoading = false
        }

        if (isLoading) {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        } else {
            ProfileScreen(app, profile, studentId)
        }
    }

    @Composable
    fun Base64Image(base64String: String): ImageBitmap? {

        try {
            // Decode Base64 string to byte array
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)

            // Convert byte array to Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Convert Bitmap to ImageBitmap
            return bitmap.asImageBitmap()
        }catch(e: Exception) {
            Log.e("Error decoding image",  "$e")
            return null
        }
    }

    @Composable
    fun ProfileScreen(app: App, profile: User, studentId: Int) {
        val context = LocalContext.current
        val columnWidths = listOf(200.dp) + listOf(100.dp)
        
        val image = Base64Image(base64String = profile.photo)

        Scaffold(
            topBar = { Header("Profile", app) },
            bottomBar = { Footer(studentId) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = "Role: ${profile.role.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (profile.role.name == "Student") "Group: ${profile.group.name}" else "Component: ${profile.component.name}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Name: ${profile.firstName} ${profile.lastName}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Email: ${profile.email}",
                            fontSize = 16.sp,
                            modifier = Modifier.clickable { }
                        )
                    }
                    if (image != null) {
                        Image(
                            bitmap = image,
                            contentDescription = "${profile.photo}'s photo",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(16.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(text = "No photo found!")
                    }
                }

            }
        }
    }
}