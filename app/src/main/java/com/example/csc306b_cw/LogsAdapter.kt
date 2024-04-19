package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class LogsAdapter (private val itemsArrayList : MutableList<LogsData>, mainActivity: MainActivity, logsData: String) : RecyclerView.Adapter<LogsAdapter.ViewHolder>() {
    val mainAct = mainActivity
    var logsData = logsData

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var activityName = itemView.findViewById<TextView>(R.id.activity)
        var activityTime = itemView.findViewById<TextView>(R.id.time)
        var activityDuration = itemView.findViewById<TextView>(R.id.duration)
        var catColor = itemView.findViewById<Button>(R.id.catColor)

        //Do something when the log is clicked
        init {

            itemView.setOnClickListener { view : View ->


                val titleToSearch = itemView.findViewById<TextView>(R.id.activity).text
                val timeToSearch = itemView.findViewById<TextView>(R.id.time).text
                var detailsToShow: JSONObject? = null

                try {
                    val jsonString = mainAct.assets.open("test.json").bufferedReader().use { it.readText() }

                    val outputJson = JSONObject(jsonString)
                    val logs = outputJson.getJSONArray("logs") as JSONArray

                    for (i in 0 until logs.length()){
                        val date = logs.getJSONObject(i).getString("date")
                        val activityName = logs.getJSONObject(i).getString("activityName")

                        val startingTime = logs.getJSONObject(i).getString("startingTime")
                        val endingTime = logs.getJSONObject(i).getString("endingTime")
                        val formattedTime = "$startingTime - $endingTime"

                        if (activityName == titleToSearch && formattedTime == timeToSearch && date == logsData) {
                            detailsToShow = logs.getJSONObject(i)
                        }
                    }
                }catch (e: Exception) {
                    Log.d("LOL",e.message.toString())
                }

                if (detailsToShow == null) {
                    val toast = Toast.makeText(mainAct, "Details not found", Toast.LENGTH_LONG)
                    toast.show()
                }else {
                    //Implement code to make popup to show details of log
                    val detailPopUp = ShowDetailsPopUp(mainAct, detailsToShow)
                    detailPopUp.show(
                        (mainAct as AppCompatActivity).supportFragmentManager,
                        "showPopUp"
                    )
                }

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
        if (item.activityName == "Reading"){
            holder.catColor.setBackgroundColor(mainAct.getColor(R.color.purple))
        }

        holder.activityName.text = item.activityName
        holder.activityTime.text = "${item.startingTime} - ${item.endingTime}"
        holder.activityDuration.text = "${ item.duration.toString().replace(".", " Hours ") } Minutes"

    }

    override fun getItemCount(): Int {
        return itemsArrayList.size
    }
}