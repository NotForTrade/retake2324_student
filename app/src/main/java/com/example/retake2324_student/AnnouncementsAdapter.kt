package com.example.retake2324_student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnnouncementsAdapter(private val announcements: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.bind(announcement)
    }

    override fun getItemCount(): Int {
        return announcements.size
    }

    inner class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textAuthor: TextView = itemView.findViewById(R.id.textAuthor)
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textContent: TextView = itemView.findViewById(R.id.textContent)

        fun bind(announcement: Announcement) {
            textAuthor.text = announcement.author
            textDate.text = announcement.date
            textTitle.text = announcement.title
            textContent.text = announcement.content

            itemView.setOnClickListener {
                if (textContent.visibility == View.GONE) {
                    textContent.visibility = View.VISIBLE
                } else {
                    textContent.visibility = View.GONE
                }
            }
        }
    }
}
