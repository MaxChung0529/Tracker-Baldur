package com.example.csc306b_cw

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class GoalsAdapter (private val goalsArrayList : MutableList<GoalsData>) : RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        var goalName = itemView.findViewById<TextView>(R.id.goalTitle)
        var progress = itemView.findViewById<TextView>(R.id.actual_progress)
        var deadline = itemView.findViewById<TextView>(R.id.deadline_date)

        //Do something when the log is clicked
        init {

            itemView.setOnClickListener { view : View ->
                val position : Int = adapterPosition
                Toast.makeText(itemView.context, goalsArrayList[position].progress, Toast.LENGTH_LONG).show()

                //Implement code to make popup to show details of log

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.goal_cards, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = goalsArrayList[position]

        holder.goalName.text = item.goalName
        holder.progress.text = item.progress
        holder.deadline.text = item.deadline

    }

    override fun getItemCount(): Int {
        return goalsArrayList.size
    }
}