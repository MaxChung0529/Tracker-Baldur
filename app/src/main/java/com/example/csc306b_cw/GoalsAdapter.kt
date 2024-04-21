package com.example.csc306b_cw

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text

class GoalsAdapter (private val goalsArrayList : MutableList<GoalsData>, mainActivity: MainActivity) : RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {
    var mainActivity = mainActivity

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        var goalName = itemView.findViewById<TextView>(R.id.goalTitle)
        var progress = itemView.findViewById<TextView>(R.id.actual_progress)
        var deadline = itemView.findViewById<TextView>(R.id.deadline_date)


        //Do something when the log is clicked
        init {

            itemView.setOnClickListener { view : View ->
                val position : Int = adapterPosition
//                Toast.makeText(itemView.context, goalsArrayList[position].progress, Toast.LENGTH_LONG).show()

                val titleToSearch = itemView.findViewById<TextView>(R.id.goalTitle).text
                val progressToSearch = itemView.findViewById<TextView>(R.id.actual_progress).text
                val deadlineToSearch = itemView.findViewById<TextView>(R.id.deadline_date).text
                var detailsToShow: JSONObject? = null

                try {
                    val jsonString = mainActivity.assets.open("goalTest.json").bufferedReader().use { it.readText() }

                    val outputJson = JSONObject(jsonString)
                    val goals = outputJson.getJSONArray("goals") as JSONArray

                    for (i in 0 until goals.length()){
                        val goalName = goals.getJSONObject(i).getString("goalName")
                        val interval = goals.getJSONObject(i).getString("interval")
                        val intervalUnit = goals.getJSONObject(i).getString("unit")
                        val durationPerUnit = goals.getJSONObject(i).getDouble("durationPerUnit")
                        val progressNow = goals.getJSONObject(i).getDouble("progressNow")
                        val progressGoal = goals.getJSONObject(i).getDouble("progressGoal")
                        val deadline = goals.getJSONObject(i).getString("deadline")
                        var description = goals.getJSONObject(i).getString("description")
                        var imgSrc = goals.getJSONObject(i).getString("imgSrc")

                        val formattedProgress = "${progressNow}/${progressGoal}"


                        if (goalName == titleToSearch && formattedProgress == progressToSearch && deadline == deadlineToSearch) {
                            detailsToShow = goals.getJSONObject(i)
                        }
                    }
                }catch (e: Exception) {
                    Log.d("LOL",e.message.toString())
                }

                if (detailsToShow == null) {
                    val toast = Toast.makeText(mainActivity, "Details not found", Toast.LENGTH_LONG)
                    toast.show()
                }else {
                    //Implement code to make popup to show details of log
                    val detailPopUp = ShowDetailsPopUp(mainActivity, detailsToShow)
                    detailPopUp.show(
                        (mainActivity as AppCompatActivity).supportFragmentManager,
                        "showPopUp"
                    )
                }

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
        holder.progress.text =  "${item.progressNow}/${item.progressGoal}"
        holder.deadline.text = item.deadline

    }

    override fun getItemCount(): Int {
        return goalsArrayList.size
    }
}