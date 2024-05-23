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
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.Score
import com.example.retake2324_student.data.Skill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class RequestReassessmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App
        setContent {
            val skillId = intent.getIntExtra("skillId", -1)
            val scoreId = intent.getIntExtra("scoreId", -1)
            val studentId = intent.getIntExtra("studentId", -1)
            //FetchRequestReassessmentData(app, scoreId, skillId, studentId)
        }
    }
}
/*
private suspend fun fetchObjects(database: Database, skillId: Int, studentId: Int): Pair<Skill, Score> {

    try {
        // Fetch the skill
        val skill = withContext(Dispatchers.IO) {
            database.sequenceOf(Schemas.Skills).find { it.Id eq skillId }
        }
        if (skill != null) {
            // Fetch the associated score
            val score = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Scores)
                    .filter { it.SkillId eq skillId }
                    .find { it.StudentId eq studentId }
            }
            if (score != null) {
                return Pair(skill, score)
            } else {
                Log.i("MISSING ENTRY", "Score with skillId $skillId and studentId $studentId not found on the database!")
                return Pair(skill, Score())
            }
        } else {
            Log.e("MISSING ENTRY", "Skill with id $skillId not found on the database!")
            return Pair(Skill(), Score())
        }
    } catch (e: Exception) {
        Log.e("SQL FETCHING ERROR", e.toString())
        return Pair(Skill(), Score())
    }
}


@Composable
fun FetchRequestReassessmentData(app: App, skillId: Int, scoreId: Int, studentId: Int) {
    // MutableState to hold values
    var skill by remember { mutableStateOf(Skill()) }
    var score by remember { mutableStateOf(Score()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = app.getDatabase() // Reuse the existing database connection
        var (fetchedSkill, fetchedScore) = fetchObjects(database, skillId, studentId)

        // Update the states
        skill = fetchedSkill
        score = fetchedScore
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        RequestReassessmentScreen(skill, score)
    }
}




@Composable
fun RequestReassessmentScreen(skill: Skill, score: Score) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Component: $skill.component.name", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Skill: $skill.name", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { filePickerLauncher.launch("") },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Upload File")
        }

        Button(
            onClick = {
                // Handle the file submission logic here
            },
            enabled = selectedFileUri != null
        ) {
            Text(text = "Submit")
        }
    }
}

 */