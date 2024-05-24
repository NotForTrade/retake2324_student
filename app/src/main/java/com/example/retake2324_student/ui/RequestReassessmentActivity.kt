package com.example.retake2324_student.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.Skill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList




class RequestReassessmentActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

private suspend fun fetchObjects(database: Database, studentId: Int, skillId: Int): Pair<List<Component>, Skill?> {

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
            val skill = skills.find { it.id == skillId }
            if (skill != null) {
                // Attribute the skill List to the skill's component
                skill.component.skills = skills.filter { it.component.id == skill.component.id }
            }

            return Pair(components, skill)
        } else {
            Log.e("SQL FETCHING ERROR", "NO COMPONENT FOUND")
            return Pair(listOf<Component>(), Skill())
        }
    } catch (e: Exception) {
        Log.e("SQL FETCHING ERROR", e.toString())
        return Pair(listOf<Component>(), Skill())
    }
}


@Composable
fun RequestReassessmentLoader(app: App, skillId: Int, scoreId: Int, studentId: Int) {
    // MutableState to hold values
    var components by remember { mutableStateOf(listOf<Component>()) }
    var skill by remember { mutableStateOf<Skill?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = app.getDatabase() // Reuse the existing database connection
        val (fetchedComponents, fetchedSkill) = fetchObjects(database, studentId, skillId)

        // Update the states
        components = fetchedComponents
        skill = fetchedSkill
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        RequestReassessmentScreen(app, components, skill, studentId, scoreId)
    }
}




@Composable
fun RequestReassessmentScreen(app: App, components: List<Component>, skill: Skill?, studentId: Int, scoreId: Int) {
    val context = LocalContext.current

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileBase64 by remember { mutableStateOf<String?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bytes = stream.readBytes()
                selectedFileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                Log.d("BASE64", "File converted to Base64: $selectedFileBase64")
            }
        }
    }

    // Components' box variables
    var selectedComponent by remember { mutableStateOf(skill?.component) }
    var componentsBoxExpanded by remember { mutableStateOf(false) }

    // Skills' box variables
    var selectedSkill by remember { mutableStateOf(skill) }
    var skillsBoxExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        // Components' DropDownBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { componentsBoxExpanded = !componentsBoxExpanded }
                .border(1.dp, Color.Gray)
                .padding(16.dp)
        ) {
            Text(text = selectedComponent?.name ?: "Select a Component")
        }
        DropdownMenu(
            expanded = componentsBoxExpanded,
            onDismissRequest = { componentsBoxExpanded = false }
        ) {
            components.forEach { component ->
                DropdownMenuItem(
                    { Text(text = component.name) },
                    onClick = {
                        selectedComponent = component
                        componentsBoxExpanded = false
                        selectedSkill = null
                        selectedFileUri = null
                        selectedFileBase64 = null
                        Log.d("SELECT CHECK", "Selected Component: ${component.name}") // Debug log

                    }
                )
            }
        }

        // Skill's DropDownBox
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(selectedComponent != null) { skillsBoxExpanded = !skillsBoxExpanded }
                .border(1.dp, if (selectedComponent != null) Color.Gray else Color.LightGray)
                .padding(16.dp)
        ) {
            Text(text = selectedSkill?.name ?: if (selectedComponent == null) "Select a Component first" else "Select a Skill")
        }

        DropdownMenu(
            expanded = skillsBoxExpanded,
            onDismissRequest = { skillsBoxExpanded = false }
        ) {
            selectedComponent?.skills?.forEach { skill ->
                DropdownMenuItem(
                    { Text(text = skill.name) },
                    onClick = {
                        selectedSkill = skill
                        skillsBoxExpanded = false
                        selectedFileUri = null
                        selectedFileBase64 = null
                    }
                )
            }
        }

        // Upload File Button
        Button(
            onClick = { filePickerLauncher.launch("*/*") },
            enabled = selectedComponent != null && selectedSkill != null,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Upload File")
        }

        // Display selected file URI
        selectedFileUri?.let {
            Text(text = "Selected File: ${it.path}", modifier = Modifier.padding(top = 8.dp))
        }

        // Submit Button
        Button(
            onClick = {
                // Handle submit action here
                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        val database = app.getDatabase()
                        database.insert(Schemas.Reassessments) {
                            set(it.StudentId, studentId)
                            set(it.SkillId, selectedSkill!!.id)
                            set(it.ScoreId, if (scoreId > 0) scoreId else null)
                            set(it.Document, selectedFileBase64!!)
                            set(it.Treated, false)
                        }
                        Log.d("SUBMIT", "File submitted with Base64: $selectedFileBase64")

                        // Reinitialize the selectors
                        selectedComponent = null
                        selectedSkill = null
                        selectedFileUri = null
                        selectedFileBase64 = null

                        // Show success dialog
                        dialogMessage = "Request submitted successfully"
                        showDialog = true

                    } catch (e: Exception) {
                        Log.e("SUBMIT", "Error submitting file: ${e.message}")
                        dialogMessage = "An error occurred: ${e.message}"
                        showDialog = true

                    }
                }
            },
            enabled = selectedFileUri != null,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Submit")
        }

        // Show Dialog if needed
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Submission Status") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Ok")
                    }
                }
            )
        }
    }
}


