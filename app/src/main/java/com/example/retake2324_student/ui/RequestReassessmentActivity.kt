package com.example.retake2324_student.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.Score
import com.example.retake2324_student.data.Skill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.isNotEmpty
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class RequestReassessmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App
        setContent {
            val skillId = intent.getIntExtra("skillId", -1)
            val scoreId = intent.getIntExtra("scoreId", -1)
            val studentId = intent.getIntExtra("studentId", -1)
            RequestReassessmentLoader(app, scoreId, skillId, studentId)
        }
    }
}

private suspend fun fetchObjects(database: Database, studentId: Int): List<Component> {

    try {
        // Fetch the skill
        val components = withContext(Dispatchers.IO) {
            database.sequenceOf(Schemas.Components).toList()
        }
        val skills = withContext(Dispatchers.IO) {
            database.sequenceOf(Schemas.Skills).toList()
        }
        val scores = withContext(Dispatchers.IO) {
            database.sequenceOf(Schemas.Scores).filter { it.StudentId eq studentId }.toList()
        }

        // Associate the skills to their component
        if (components.isNotEmpty() and skills.isNotEmpty()) {
            components.forEach {component ->
                component.skills = skills.filter { it.component.id == component.id }
                // Associate the scores to the skills
                if (component.skills.isNotEmpty() and scores.isNotEmpty()) {
                    component.skills.forEach {skill ->
                        skill.scores = scores.filter { it.skill.id == skill.id }
                    }
                }

            }
            return components
        } else {
            Log.e("SQL FETCHING ERROR", "NO COMPONENT FOUND")
            return listOf<Component>()
        }
    } catch (e: Exception) {
        Log.e("SQL FETCHING ERROR", e.toString())
        return listOf<Component>()
    }
}


@Composable
fun RequestReassessmentLoader(app: App, skillId: Int, scoreId: Int, studentId: Int) {
    // MutableState to hold values
    var components by remember { mutableStateOf(listOf<Component>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = app.getDatabase() // Reuse the existing database connection
        var fetchedComponents = fetchObjects(database, studentId)

        // Update the states
        components = fetchedComponents
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        RequestReassessmentScreen(components)
    }
}




@Composable
fun RequestReassessmentScreen(components: List<Component>) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }


}

