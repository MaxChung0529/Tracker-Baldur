package com.example.csc306b_cw

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Goals.newInstance] factory method to
 * create an instance of this fragment.
 */
class Goals() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity : MainActivity
//    val mainActivity = mainActivity
    lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mainActivity = context as MainActivity
        contentView = inflater.inflate(R.layout.fragment_goals, container, false)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.goalsRecycler)

        val adapter = GoalsAdapter(getStoredGoals(), mainActivity)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager

        val addGoalBtn = contentView.findViewById<FloatingActionButton>(R.id.addGoal)
        addGoalBtn.setOnClickListener{
            showPopUp()
        }

        fillRecyclerView(contentView)
        return contentView
    }

    private fun showPopUp(){
        val showPopUp = AddGoalPopUp(mainActivity)
        showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }

    fun refresh() {
        fillRecyclerView(contentView)
    }

    private fun fillRecyclerView(contentView: View) {
        var adapter = GoalsAdapter(getStoredGoals(), mainActivity)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.goalsRecycler)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
    }


    fun getStoredGoals(): ArrayList<GoalsData> {
        val list = ArrayList<GoalsData>()
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "goalsData.json"
        file = File(myDir, fileName)

        try {
            val jsonString = file.bufferedReader().use { it.readText() }

            val outputJson = JSONObject(jsonString)
            val goals = outputJson.getJSONArray("goals") as JSONArray

            for (i in 0 until goals.length()) {
                val goalName = goals.getJSONObject(i).getString("goalName")
                val interval = goals.getJSONObject(i).getInt("interval")
                val intervalUnit = goals.getJSONObject(i).getString("unit")
                val durationPerUnit = goals.getJSONObject(i).getDouble("durationPerUnit")
                val progressNow = goals.getJSONObject(i).getDouble("progressNow")
                val progressGoal = goals.getJSONObject(i).getDouble("progressGoal")
                val deadline = goals.getJSONObject(i).getString("deadline")
                val description = goals.getJSONObject(i).getString("description")
                val imgSrc = goals.getJSONObject(i).getString("imgSrc")
                list.add(
                    GoalsData(goalName, interval, intervalUnit, durationPerUnit, progressNow, progressGoal, deadline, description, imgSrc)
                )
            }
            return list
        }catch (e: Exception) {
            file.createNewFile()
            return list
        }
    }

    private fun refreshStoredGoals(storedGoals: ArrayList<GoalsData>){

        val tmpJSONArray = JSONArray()
        for (i in 0 until storedGoals.size) {
            val tmpGoal = storedGoals[i]

            tmpJSONArray.put(
                createJsonData(tmpGoal.goalName, tmpGoal.interval.toString(), tmpGoal.unit,
                    tmpGoal.durationPerUnit, tmpGoal.progressNow, tmpGoal.progressGoal, tmpGoal.deadline,
                    tmpGoal.description, tmpGoal.imgSrc)
            )
        }


        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        val myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val goalsArray = JSONObject()

        goalsArray.put("goals",tmpJSONArray)

        val fileName = "goalsData.json"
        file = File(myDir, fileName)
        try {
            val output = BufferedWriter(FileWriter(file))
            output.write(goalsArray.toString())
            output.close()
        }catch (e: Exception) {
            Log.d("goal-saving", e.message.toString())
        }
    }

    fun addHourToGoal(goal: GoalsData, numToAdd: Double) {

        val storedGoals = getStoredGoals()

        for (i in 0 until storedGoals.size) {
            if (goal == storedGoals[i]) {
                storedGoals[i].progressNow?.plus(numToAdd)
                refreshStoredGoals(storedGoals)
            }
        }

    }

    private fun createJsonData(goalName: String?, interval: String?, unit: String?, duration: Double?
                               , progressNow: Double?, progressGoal: Double?, deadline: String?
                               , description: String?, imgSrc: String?): JSONObject {
        var json = JSONObject()

        json.put("goalName", goalName)
        json.put("interval", interval)
        json.put("unit", unit)
        json.put("durationPerUnit", duration)
        json.put("progressNow", progressNow)
        json.put("progressGoal", progressGoal)
        json.put("deadline", deadline)
        json.put("description", description)
        json.put("imgSrc", imgSrc)

        return json
    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment Goals.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            Goals().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}