package com.example.retake2324_student

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList


class GroupOverviewActivity : BaseActivity() {

    private lateinit var recyclerViewStudents: RecyclerView
    private lateinit var recyclerViewComponents: RecyclerView
    private val groupId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App

        // Set up the recyclerViews
        recyclerViewStudents = findViewById(R.id.recycler_view_students)
        recyclerViewStudents.layoutManager = LinearLayoutManager(this)
        recyclerViewComponents = findViewById(R.id.recycler_view_components)
        recyclerViewComponents.layoutManager = LinearLayoutManager(this)


        // Start the coroutine to call the database related instructions
        CoroutineScope(Dispatchers.Main).launch {
            val database = app.getDatabase() // Reuse the existing database connection
            loadAndDisplayComponents(database, groupId)
        }

    }



    private suspend fun loadAndDisplayComponents(database: Database, groupId: Int) {
        val app = application as App

        try {

            // Fetch all the students from the user's group
            val students = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Users).filter { it.GroupId eq groupId }
                    .toList()
            }

            // Fetch components data
            val components = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Components)
                    .toList()
            }

            // Fetch all skills for each components
            for (component: Component in components) {
                component.skills = withContext(Dispatchers.IO) {
                    database.sequenceOf(Schemas.Skills)
                        .filter { it.ComponentId eq component.id }.toList()
                }

                // Fetch all scores for each skills and for each student
                for (skill: Skill in component.skills) {
                    for (student: User in students) {
                        skill.scores = withContext(Dispatchers.IO) {
                            database.sequenceOf(Schemas.Scores)
                                .filter { it.SkillId eq skill.id }
                                .filter { it.StudentId eq student.id }
                                .toList()
                        }
                    }
                }
            }

            if (components.isEmpty()) {
                app.displayException(EmptyDbListResultException("No components found"))
            }
            else if(students.isEmpty()){
                app.displayException(EmptyDbListResultException("No student found"))
            }
            else {

                // Apply the recyclerViews layout
                val studentAdapter = StudentAdapter(students)
                recyclerViewStudents.adapter = studentAdapter
                val componentAdapter = ComponentAdapter(components, students)
                recyclerViewComponents.adapter = componentAdapter


            }

        } catch (e: Exception) {
            app.displayException(e)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_group_overview
    }

}
