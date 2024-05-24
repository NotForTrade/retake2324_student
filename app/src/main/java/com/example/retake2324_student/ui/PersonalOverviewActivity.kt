package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


class PersonalOverviewActivity : ComponentActivity() {

    private val studentId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        setContent {
            MaterialTheme {
                PersonalOverviewLoader(app, studentId)
            }
        }
    }
}



private suspend fun fetchObjects(database: Database, studentId: Int): Pair<User, List<Component>> {

    try {

        // Fetch all the students from the user's group
        val student = withContext(Dispatchers.IO) {
            database.sequenceOf(Schemas.Users).find { it.Id eq studentId }
        }

        if (student != null) {

            // Fetch components data
            val components = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Components)
                    .toList()
            }

            // Fetch skills data
            val skills = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Skills)
                    .toList()
            }

            // Fetch scores data
            val scores = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Scores)
                    .toList()
            }

            // Set-up the studentComponentScore mutable list to build the list of students' score per component
            val studentComponentScore: MutableList<Score> = mutableListOf<Score>()

            if (components.isNotEmpty()) {
                // attribute skills to their corresponding component
                components.forEach {component ->
                    component.skills = skills.filter{ it.component.id == component.id }.toList()
                    if (component.skills.isNotEmpty()) {
                        // attribute scores to each skills
                        component.skills.forEach { skill ->
                            skill.scores = scores.filter { it.skill.id == skill.id }.toList()
                        }
                        if (component.skills.isNotEmpty()) {
                            // Calculate the student's weighted average score of the currently explored component
                            val studentScores = scores.filter { (it.student.id == student.id) and (it.skill.component.id == component.id) }
                            if (studentScores.isNotEmpty()){
                                var weightedScoreSum: Double = 0.0
                                var coefficientSum: Double = 0.0
                                studentScores.forEach {studentScore ->
                                    weightedScoreSum += studentScore.value * studentScore.skill.coefficient
                                    coefficientSum += studentScore.skill.coefficient
                                }
                                // Create and add a new Score object with the weighted average score for the component
                                studentComponentScore.add(Score{
                                    id // not used
                                    this.student = student
                                    skill // not used
                                    this.value = if (coefficientSum!=0.0) weightedScoreSum/coefficientSum else 0.0
                                    observation // not used
                                    document // not used
                                })
                            }
                            // Assign the new built score list to the component
                            component.scores = studentComponentScore.toList()
                            // Clear studentComponentScore for future calculation
                            studentComponentScore.clear()
                            // Log.i("COMPONENT SCORE", component.scores.toString())
                        }
                    }
                }
            }
            return Pair(student, components)
        } else {
            Log.e("SQL FETCHING ERROR", "User with $studentId not found on the database!")
            return Pair(User(), listOf<Component>())
        }
    } catch (e: Exception) {
        Log.e("SQL FETCHING ERROR", e.toString())
        return Pair(User(), listOf<Component>())
    }
}


@Composable
fun PersonalOverviewLoader(app: App, studentId: Int) {
    // MutableState to hold the lists
    var student by remember { mutableStateOf(User()) }
    var components by remember { mutableStateOf<List<Component>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = app.getDatabase() // Reuse the existing database connection
        val (fetchedStudent, fetchedComponents) = fetchObjects(database, studentId)

        // Update the states
        student = fetchedStudent
        components = fetchedComponents
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        PersonalOverviewScreen(student, components)
    }
}

@Composable
fun PersonalOverviewScreen(student: User, components: List<Component>) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {

        // Row for the group name and student names
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text(text = "Group: ${student.group.name}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(2f))
            Text(text = student.firstName + " " + student.lastName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))

        }


        // LazyColumn for each component under group
        if (components.isNotEmpty()) {
            LazyColumn(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
                items(components) { component ->
                    // Component row
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(text = "Component: ${component.name}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(2f))
                        val score = component.scores.find { it.student.id == student.id }?.value ?: 0.0
                        Text(text = "$score", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    }
                    if (component.skills.isNotEmpty()) {
                        // LazyColumn for each skill under the component
                        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                            items(component.skills) { skill ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
                                    .clickable{
                                        val intent = Intent(context, SkillActivity::class.java).apply {
                                            putExtra("studentId", student.id)
                                            putExtra("skillId", skill.id)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text(text = "Skill: ${skill.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(2f))
                                    val skillScore = skill.scores.find { it.student.id == student.id }?.value ?: 0.0
                                    Text(text = "$skillScore", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
