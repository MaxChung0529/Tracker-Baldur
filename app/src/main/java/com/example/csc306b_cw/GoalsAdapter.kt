package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.text.Layout
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
import org.w3c.dom.Text
import java.io.File

class GoalsAdapter (private val goalsArrayList : MutableList<GoalsData>, mainActivity: MainActivity) : RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {
    var mainActivity = mainActivity

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        var goalName = itemView.findViewById<TextView>(R.id.goalTitle)
        var progress = itemView.findViewById<TextView>(R.id.actual_progress)
        var deadline = itemView.findViewById<TextView>(R.id.deadline_date)
        var catBar = itemView.findViewById<Button>(R.id.catBar)


        //Do something when the log is clicked
        init {

            itemView.setOnClickListener { view : View ->
                val position : Int = adapterPosition

                val titleToSearch = itemView.findViewById<TextView>(R.id.goalTitle).text
                val progressToSearch = itemView.findViewById<TextView>(R.id.actual_progress).text
                val deadlineToSearch = itemView.findViewById<TextView>(R.id.deadline_date).text

                var detailsToShow: JSONObject? = null

                try {
                    var file: File? = null
                    val root = mainActivity.getExternalFilesDir(null)?.absolutePath
                    var myDir = File("$root/TrackerBaldur")

                    val fileName = "goalsData.json"
                    file = File(myDir, fileName)
                    val jsonString = file.bufferedReader().use { it.readText() }

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
                    Log.d("Goals Empty",e.message.toString())
                }

                if (detailsToShow == null) {
                    val toast = Toast.makeText(mainActivity, "Details not found", Toast.LENGTH_LONG)
                    toast.show()
                }else {
                    //Implement code to make popup to show details of log
                    val detailPopUp = ShowGoalPopUp(mainActivity, detailsToShow)
                    detailPopUp.show(
                        (mainActivity as AppCompatActivity).supportFragmentManager,
                        "showPopUp"
                    )
                }
            }
        }

    }
    @SuppressLint("DiscouragedApi")
    fun findColour(name: String?): Int{
        val coloursJSONString = mainActivity.assets.open("catColors.json").bufferedReader().use {
            it.readText()
        }

        val outputJson = JSONObject(coloursJSONString)
        val colours = outputJson.getJSONArray("colours") as JSONArray

        for (i in 0 until colours.length()) {
            if (name == colours.getJSONObject(i).getString("Name")) {
                val colorName = colours.getJSONObject(i).getString("Colour")

                val res = mainActivity.getResources()
                val packageName: String = mainActivity.getPackageName()

                val colorId = res.getIdentifier(colorName, "color", packageName)
                return colorId
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.goal_cards, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = goalsArrayList[position]



        holder.catBar.setBackgroundColor(mainActivity.getColor(findColour(item.goalName.toString())))

        holder.goalName.text = item.goalName
        holder.progress.text =  "${item.progressNow}/${item.progressGoal}"
        holder.deadline.text = item.deadline

    }

    override fun getItemCount(): Int {
        return goalsArrayList.size
    }
}