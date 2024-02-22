package com.example.csc306b_cw

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter (private val itemsArrayList : MutableList<LogsData>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var activityName = itemView.findViewById<TextView>(R.id.activity)
        var activityTime = itemView.findViewById<TextView>(R.id.time)

        //Do something when the log is clicked
        init {

            itemView.setOnClickListener { view : View ->
                val position : Int = adapterPosition
                Toast.makeText(itemView.context, itemsArrayList[position].activityName, Toast.LENGTH_LONG).show()

                //Implement code to make popup to show details of log

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemsArrayList[position]

        holder.activityName.text = item.activityName
        holder.activityTime.text = item.activityTime

    }

    override fun getItemCount(): Int {
        return itemsArrayList.size
    }
}