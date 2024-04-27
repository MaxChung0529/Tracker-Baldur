package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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
 * Use the [ShowGoalPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowGoalPopUp(detailsObj: JSONObject) : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity : MainActivity
    val detailsObj = detailsObj

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
        mainActivity = context as MainActivity

        // Inflate the layout for this fragment
        val popupView = inflater.inflate(R.layout.fragment_show_goal_pop_up, container, false)

        val goalName = detailsObj.getString("goalName")
        popupView.findViewById<TextView>(R.id.detail_goal_title).setText(goalName)

        val catBar = popupView.findViewById<Button>(R.id.catColor)
        catBar.setBackgroundColor(mainActivity.getColor(findColour(goalName)))

        val now = detailsObj.getString("progressNow")
        val goal = detailsObj.getString("progressGoal")
        val formattedProgess = "$now/$goal Hours"

        popupView.findViewById<TextView>(R.id.detailGoalActualProgress).setText(formattedProgess)

        popupView.findViewById<TextView>(R.id.goal_deadline).setText(detailsObj.getString("deadline"))
        popupView.findViewById<TextView>(R.id.goalDetailDescContent).setText(detailsObj.getString("description"))

        try {
            val detailImage = popupView.findViewById<ImageView>(R.id.goalDetailImage)

            if (detailsObj.getString("imgSrc") != "") {
                detailImage.setImageURI(Uri.parse(detailsObj.getString("imgSrc")))
            }else {
                val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

                if (!isDarkModeOn) {
                    detailImage.setImageDrawable(mainActivity.getDrawable(R.drawable.backgroundstuff))
                }else {
                    detailImage.setImageDrawable(mainActivity.getDrawable(R.drawable.backgroundstuffdark))
                }
            }
        }catch (_: Exception){
        }


        val deleteGoalBtn = popupView.findViewById<Button>(R.id.deleteGoalBtn)
        deleteGoalBtn.setOnClickListener{
            deleteGoal()
        }

        return popupView
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

    private fun getStoredGoals(): JSONArray {
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "goalsData.json"
        file = File(myDir, fileName)

        val jsonString = file.bufferedReader().use { it.readText() }

        val outputJson = JSONObject(jsonString)
        val goals = outputJson.getJSONArray("goals") as JSONArray
        return goals
    }

    private fun deleteGoal() {
        val storedGoals = getStoredGoals()

        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        var tmpJSONArray = JSONArray()
        for (i in 0 until storedGoals.length()) {
            val checkObj = storedGoals.getJSONObject(i)
            if (checkObj.get("deadline") != detailsObj.get("deadline")
                || checkObj.get("interval") != detailsObj.get("interval")
                || checkObj.get("unit") != detailsObj.get("unit")
                || checkObj.get("goalName") != detailsObj.get("goalName")
                || checkObj.get("description") != detailsObj.get("description")) {
                tmpJSONArray.put(storedGoals.getJSONObject(i))
            }
        }
        val goalsArray = JSONObject()

        goalsArray.put("goals",tmpJSONArray)

        val fileName = "goalsData.json"
        file = File(myDir, fileName)
        try {
            val output = BufferedWriter(FileWriter(file))
            output.write(goalsArray.toString())
            output.close()
        }catch (_: Exception) {
        }
        mainActivity.goalFragment.refresh()
        dismiss()
        Toast.makeText(mainActivity, "Goal deleted", Toast.LENGTH_LONG).show()
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ShowGoalPopUp.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ShowGoalPopUp().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}