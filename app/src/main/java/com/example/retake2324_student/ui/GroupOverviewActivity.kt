package com.example.retake2324_student.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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


class GroupOverviewActivity : ComponentActivity() {

    private val groupId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        setContent {
            MaterialTheme {
                GroupOverviewScreen(app, groupId)
            }
        }
    }
}



private suspend fun fetchObjects(database: Database, groupId: Int): Pair<List<User>, List<Component>> {

    try {

        // Fetch all the students from the user's group
        val students = withContext(Dispatchers.IO) {
            database.sequenceOf(Schemas.Users).filter { it.GroupId eq groupId }.toList()
        }

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
                    if (students.isNotEmpty() and scores.isNotEmpty()) {
                        // For each student, calculate their weighted average score of the currently explored component
                        students.forEach {student ->
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
        return Pair(students, components)
    } catch (e: Exception) {
        Log.e("SQL FETCHING ERROR", e.toString())
        return Pair(listOf<User>(), listOf<Component>())
    }
}


@Composable
fun GroupOverviewScreen(app: App, groupId: Int) {
    // MutableState to hold the lists
    var students by remember { mutableStateOf<List<User>>(emptyList()) }
    var components by remember { mutableStateOf<List<Component>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = app.getDatabase() // Reuse the existing database connection
        val (fetchedStudents, fetchedComponents) = fetchObjects(database, groupId)

        // Update the states
        students = fetchedStudents
        components = fetchedComponents
        isLoading = false
    }

    if (isLoading) {
        Text(text = "Loading...", modifier = Modifier.padding(16.dp))
    } else {
        GroupTable(students, components)
    }
}

@Composable
fun GroupTable(students: List<User>, components: List<Component>) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Row for the group name and student names
        Log.i("STUDENTS", students.toString())
        if (students.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(text = "Group: ${students[0].group.name}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(2f))
                students.forEach { student ->
                    Text(text = student.firstName + " " + student.lastName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                }
            }
        }

        // LazyColumn for each component under group
        Log.i("COMPONENTS", components.toString())
        if (components.isNotEmpty()) {
            LazyColumn(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
                items(components) { component ->
                    // Component row
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(text = "Component: ${component.name}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(2f))
                        if (students.isNotEmpty()) {
                            students.forEach { student ->
                                val score = component.scores.find { it.student.id == student.id }?.value ?: 0.0
                                Text(text = "$score", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Log.i("SKILLS", component.skills.toString())
                    if (component.skills.isNotEmpty()) {
                        // LazyColumn for each skill under the component
                        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                            items(component.skills) { skill ->
                                Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 2.dp, bottom = 2.dp)) {
                                    Text(text = "Skill: ${skill.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(2f))
                                    students.forEach { student ->
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
}
