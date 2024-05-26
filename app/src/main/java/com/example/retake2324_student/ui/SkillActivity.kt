package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
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


class SkillActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        val studentId = intent.getIntExtra("studentId", -1)
        val skillId = intent.getIntExtra("skillId", -1)

        Log.i("RETRIEVED IDs", "STUDENT ID: $studentId | SKILL ID: $skillId")

        setContent {
            MaterialTheme {
                SkillLoader(app, skillId, studentId)
            }
        }
    }


    private suspend fun fetchObjects(
        database: Database,
        skillId: Int,
        studentId: Int
    ): Pair<Skill, Score> {

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
                    Log.i(
                        "MISSING ENTRY",
                        "Score with skillId $skillId and studentId $studentId not found on the database!"
                    )
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
    fun SkillLoader(app: App, skillId: Int, studentId: Int) {
        // MutableState to hold values
        var skill by remember { mutableStateOf(Skill()) }
        var score by remember { mutableStateOf(Score()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            val database = app.getDatabase() // Reuse the existing database connection
            val (fetchedSkill, fetchedScore) = fetchObjects(database, skillId, studentId)

            // Update the states
            skill = fetchedSkill
            score = fetchedScore
            isLoading = false
        }

        if (isLoading) {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        } else {
            SkillScreen(app, skill, score, studentId)
        }
    }

    @Composable
    fun SkillScreen(app: App, skill: Skill, score: Score, studentId: Int) {
        val context = LocalContext.current

        Scaffold(
            topBar = { Header("Skill", app) },
            bottomBar = { Footer(studentId) }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Component: ${skill.component.name}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Name: ${skill.name}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Coefficient: ${skill.coefficient}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Description: ${skill.description}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (score.value != 0.0) "Score: ${score.value}" else "Not evaluated",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(

                    onClick = {
                        // Start RequestReassessmentActivity and pass the scoreId
                        val intent = Intent(context, RequestReassessmentActivity::class.java)
                        intent.putExtra("skillId", skill.id)
                        intent.putExtra("scoreId", score.id)
                        intent.putExtra("studentId", studentId)

                        context.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Request Reassessment")
                }
            }
        }
    }
}