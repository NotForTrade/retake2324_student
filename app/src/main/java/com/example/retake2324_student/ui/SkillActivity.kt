package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.Score
import com.example.retake2324_student.data.Skill
import com.example.retake2324_student.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.toList
import org.ktorm.entity.sequenceOf


class SkillActivity: ComponentActivity() {

    private val skillId = 1
    private val studentId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        val studentId = intent.getIntExtra("studentId", -1)
        val skillId = intent.getIntExtra("skillId", -1)

        Log.i("RETRIEVED IDs", "STUDENT ID: $studentId | SKILL ID: $skillId")

        setContent {
            MaterialTheme {
                //FetchSkillData(app, skillId, studentId)
            }
        }
    }
}
/*


private suspend fun fetchObject(database: Database, skillId: Int, studentId: Int): Pair<Skill, Score> {

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
fun FetchSkillData(app: App, skillId: Int, studentId: Int) {
    // MutableState to hold values
    var skill by remember { mutableStateOf(Skill()) }
    var score by remember { mutableStateOf(Score()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = app.getDatabase() // Reuse the existing database connection
        var (fetchedSkill, fetchedScore) = fetchObject(database, skillId, studentId)

        // Update the states
        skill = fetchedSkill
        score = fetchedScore
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        skillScreen(skill, score.id, studentId)
    }
}

@Composable
fun skillScreen(skill: Skill, scoreId: Int, studentId: Int) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(text = "Component: ${skill.component}", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Name: ${skill.name}", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Coefficient: ${skill.coefficient}", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Description: ${skill.description}", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(

            onClick = {
                // Start RequestReassessmentActivity and pass the scoreId
                val intent = Intent(context, RequestReassessmentActivity::class.java)
                intent.putExtra("skillId", skill.id)
                intent.putExtra("scoreId", scoreId)
                intent.putExtra("studentId", studentId)

                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Request Reassessment")
        }
    }

}


 */