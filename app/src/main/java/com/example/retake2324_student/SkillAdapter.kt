package com.example.retake2324_student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SkillAdapter(private val skills: List<Skill>) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    class SkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val skillName: TextView = view.findViewById(R.id.tv_skill_name)
        val scoresRecyclerView: RecyclerView = view.findViewById(R.id.recycler_view_scores)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skills[position]
        holder.skillName.text = skill.name

        // Set up scores RecyclerView
        holder.scoresRecyclerView.layoutManager = LinearLayoutManager(holder.scoresRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.scoresRecyclerView.adapter = ScoreAdapter(skill.scores)
    }

    override fun getItemCount() = skills.size
}
