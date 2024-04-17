package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class LogsAdapter (private val itemsArrayList : MutableList<LogsData>, mainActivity: MainActivity) : RecyclerView.Adapter<LogsAdapter.ViewHolder>() {
    val mainAct = mainActivity

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var activityName = itemView.findViewById<TextView>(R.id.activity)
        var activityTime = itemView.findViewById<TextView>(R.id.time)
        var catColor = itemView.findViewById<Button>(R.id.catColor)

        //Do something when the log is clicked
        init {

            itemView.setOnClickListener { view : View ->

                //Implement code to make popup to show details of log
                val detailPopUp = ShowDetailsPopUp(mainAct)
                detailPopUp.show((mainAct as AppCompatActivity).supportFragmentManager, "showPopUp")

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemsArrayList[position]

        //Set category colour
        if (item.activityName == "Reading" || item.category == "Reading"){
            holder.catColor.setBackgroundColor(mainAct.getColor(R.color.purple))
        }

        if (item.activityName == null) {
            holder.activityName.text = item.category
        }else {
            holder.activityName.text = item.activityName
        }
        holder.activityTime.text = "${item.startingTime} - ${item.endingTime}"

    }

    override fun getItemCount(): Int {
        return itemsArrayList.size
    }
}