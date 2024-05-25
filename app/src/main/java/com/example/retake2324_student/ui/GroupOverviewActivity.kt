package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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


class GroupOverviewActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        val studentId = intent.getIntExtra("studentId", -1)

        setContent {
            MaterialTheme {
                GroupOverviewLoader(app, studentId)
            }
        }
    }


    private suspend fun fetchObjects(
        database: Database,
        studentId: Int
    ): Pair<List<User>, List<Component>> {

        try {

            // Fetch all the students from the user's group
            val student = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Users).find { it.Id eq studentId }
            }

            if (student != null) {

                // Fetch the students from the same group
                val students = withContext(Dispatchers.IO) {
                    database.sequenceOf(Schemas.Users).filter { it.GroupId eq student.group.id }
                        .toList()
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
                val studentComponentScore: MutableList<Score> = mutableListOf()

                if (components.isNotEmpty()) {
                    // attribute skills to their corresponding component
                    components.forEach { component ->
                        component.skills =
                            skills.filter { it.component.id == component.id }.toList()
                        if (component.skills.isNotEmpty()) {
                            // attribute scores to each skills
                            component.skills.forEach { skill ->
                                skill.scores = scores.filter { it.skill.id == skill.id }.toList()
                            }
                            if (students.isNotEmpty() and scores.isNotEmpty()) {
                                // For each student, calculate their weighted average score of the currently explored component
                                students.forEach { student ->
                                    val studentScores =
                                        scores.filter { (it.student.id == student.id) and (it.skill.component.id == component.id) }
                                    if (studentScores.isNotEmpty()) {
                                        var weightedScoreSum = 0.0
                                        var coefficientSum = 0.0
                                        studentScores.forEach { studentScore ->
                                            weightedScoreSum += studentScore.value * studentScore.skill.coefficient
                                            coefficientSum += studentScore.skill.coefficient
                                        }
                                        // Create and add a new Score object with the weighted average score for the component
                                        studentComponentScore.add(Score {
                                            id // not used
                                            this.student = student
                                            skill // not used
                                            this.value =
                                                if (coefficientSum != 0.0) weightedScoreSum / coefficientSum else 0.0
                                            observation // not used
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

            } else {
                return Pair(listOf(), listOf())
            }
        } catch (e: Exception) {
            Log.e("SQL FETCHING ERROR", e.toString())
            return Pair(listOf(), listOf())
        }
    }


    @Composable
    fun GroupOverviewLoader(app: App, studentId: Int) {
        // MutableState to hold the lists
        var students by remember { mutableStateOf(listOf<User>()) }
        var components by remember { mutableStateOf(listOf<Component>()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            val database = app.getDatabase() // Reuse the existing database connection
            val (fetchedStudents, fetchedComponents) = fetchObjects(database, studentId)

            // Update the states
            students = fetchedStudents
            components = fetchedComponents
            isLoading = false
        }

        if (isLoading) {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        } else {
            GroupOverviewScreen(app, students, components, studentId)
        }
    }

    @Composable
    fun GroupOverviewScreen(app: App, students: List<User>, components: List<Component>, studentId: Int) {
        val context = LocalContext.current
        val expandedComponents = remember { mutableStateOf(components.map { it.id }.toSet()) }

        val columnWidths = listOf(200.dp) + List(students.size) { 100.dp }

        Scaffold(
            topBar = { Header("Group Overview", app) },
            bottomBar = { Footer(studentId) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Outer Box with horizontal scrolling
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Row for the group name and student names
                        if (students.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "Group: ${students[0].group.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.width(columnWidths[0])
                                )
                                students.forEachIndexed { index, student ->
                                    Text(
                                        text = student.firstName + " " + student.lastName,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.width(columnWidths[index + 1])
                                    )
                                }
                            }
                        }

                        // LazyColumn for components and skills
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(components) { component ->
                                val isExpanded = expandedComponents.value.contains(component.id)
                                // Component row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Component: ${component.name}",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier
                                            .width(columnWidths[0])
                                            .clickable {
                                                expandedComponents.value = if (isExpanded) {
                                                    expandedComponents.value - component.id
                                                } else {
                                                    expandedComponents.value + component.id
                                                }
                                            }
                                    )
                                    students.forEachIndexed { index, student ->
                                        val score = component.scores.find { it.student.id == student.id }?.value ?: 0.0
                                        Text(
                                            text = "$score",
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.width(columnWidths[index + 1])
                                        )
                                    }
                                }

                                // Skill rows under each component
                                if (isExpanded) {
                                    component.skills.forEach { skill ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp, bottom = 2.dp)
                                        ) {
                                            Text(
                                                text = "Skill: ${skill.name}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier
                                                    .width(columnWidths[0])
                                                    .clickable {
                                                        val intent = Intent(
                                                            context,
                                                            SkillActivity::class.java
                                                        ).apply {
                                                            putExtra("studentId", studentId)
                                                            putExtra("skillId", skill.id)
                                                        }
                                                        context.startActivity(intent)
                                                    }
                                            )
                                            students.forEachIndexed { index, student ->
                                                val skillScore = skill.scores.find { it.student.id == student.id }?.value ?: 0.0
                                                Text(
                                                    text = "$skillScore",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.width(columnWidths[index + 1])
                                                )
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
    }
}