package com.example.retake2324_student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ComponentAdapter(private val components: List<Component>, private val students: List<User>) : RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder>() {

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

        // Set up component's score RecyclerView
        // holder.scoresRecyclerView.layoutManager = LinearLayoutManager(holder.scoresRecyclerView.context)



        // holder.scoresRecyclerView.adapter = ScoreAdapter(scores)

        // Set up skills RecyclerView
        // holder.skillsRecyclerView.layoutManager = LinearLayoutManager(holder.skillsRecyclerView.context)
        // holder.skillsRecyclerView.adapter = SkillAdapter(component.skills)
    }

    override fun getItemCount() = components.size
}
