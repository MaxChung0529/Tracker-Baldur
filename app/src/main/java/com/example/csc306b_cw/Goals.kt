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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Goals.newInstance] factory method to
 * create an instance of this fragment.
 */
class Goals(mainActivity: MainActivity) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val mainActivity = mainActivity

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
        val contentView = inflater.inflate(R.layout.fragment_goals, container, false)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.goalsRecycler)

        val adapter = GoalsAdapter(getGoals(), mainActivity)
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

    private fun fillRecyclerView(contentView: View) {
        var adapter = GoalsAdapter(getGoals(), mainActivity)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.goalsRecycler)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
    }

    private fun getGoals(): ArrayList<GoalsData> {
        val list = ArrayList<GoalsData>()

        try {
            val jsonString = mainActivity.assets.open("goalsTest.json").bufferedReader().use { it.readText() }

            val outputJson = JSONObject(jsonString)
            val logs = outputJson.getJSONArray("goals") as JSONArray

            for (i in 0 until logs.length()) {
                val goalName = logs.getJSONObject(i).getString("goalName")
                val interval = logs.getJSONObject(i).getInt("interval")
                val intervalUnit = logs.getJSONObject(i).getString("unit")
                val durationPerUnit = logs.getJSONObject(i).getDouble("durationPerUnit")
                val progress = logs.getJSONObject(i).getString("progress")
                val deadline = logs.getJSONObject(i).getString("deadline")
                val description = logs.getJSONObject(i).getString("description")
                val imgSrc = logs.getJSONObject(i).getString("imgSrc")
                list.add(
                    GoalsData(goalName, interval, intervalUnit, durationPerUnit, progress, deadline, description, imgSrc)
                )
                }
        }catch (e: Exception) {
            Log.d("LOL",e.message.toString())
        }
        return list

    }

    private fun populateList() : ArrayList<GoalsData> {

        val list = ArrayList<GoalsData>()

        val data = GoalsData()
        data.goalName = "Reading"
        val hoursNum = 12
        val goalHours = 36
        data.progress = "$hoursNum / $goalHours"
        //data.progress = "12 / 36"
        data.deadline = "12/12/2024"
        list.add(data)

        return list
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