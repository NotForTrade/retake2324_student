package com.example.retake2324_student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Score
import com.example.retake2324_student.data.User


class ComponentAdapter(private val components: List<Component>, private val students: List<User>) : RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder>() {

    // Store the order of students to organize scores in recyclerViews
    private val studentsOrder = students.map { it.id }

    class ComponentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val componentName: TextView = view.findViewById(R.id.tv_component_name)
        val scoresRecyclerView: RecyclerView = view.findViewById((R.id.recycler_view_scores))
        val skillsRecyclerView: RecyclerView = view.findViewById(R.id.recycler_view_skills)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_component, parent, false)
        return ComponentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComponentViewHolder, position: Int) {
        val component = components[position]
        holder.componentName.text = component.name


        // Calculate the component score for each student
        setComponentScores(component, students)


        // Set up component's scores RecyclerView
        holder.scoresRecyclerView.layoutManager = LinearLayoutManager(holder.scoresRecyclerView.context)
        holder.scoresRecyclerView.adapter = ScoreAdapter(component.scores)

        // Set up skills RecyclerView
        holder.skillsRecyclerView.layoutManager = LinearLayoutManager(holder.skillsRecyclerView.context)
        holder.skillsRecyclerView.adapter = SkillAdapter(component.skills)
    }

    override fun getItemCount() = components.size

    private fun setComponentScores(component: Component, students: List<User>) {

        val skills = component.skills

        val studentScores = mutableListOf<Score>()

        for (student in students) {

            // Collect all coefficients for skills that have scores for the given student
            val scores = skills.flatMap { skill ->
                skill.scores.filter { it.student.id == student.id }.map { it.value }
            }

            // Collect all coefficients for skills that have scores for the given student
            val coefficients = skills.filter { skill ->
                skill.scores.any { it.student.id == student.id }
            }.map { it.coefficient }

            // Concatenate all the scores, taking into account the coefficient
            val weightedSum: Double = scores.zip(coefficients).sumOf { it.first * it.second }.toDouble()
            // Calculate the sum of coefficients
            val totalCoefficient: Double = coefficients.sum().toDouble()
            // Divide the weighted sum by the sum of coefficients to get the component score
            val componentScore = if (totalCoefficient != 0.0) weightedSum / totalCoefficient else 0.0

            val studentScore: Score = Score {
                id
                student
                skill
                value = componentScore
                observation
                document
            }

            studentScores.add(studentScore)

        }

        component.scores = studentScores
    }

}


