package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Layout.Alignment
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Overview.newInstance] factory method to
 * create an instance of this fragment.
 */
class Overview() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity: MainActivity

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
        val overviewView = inflater.inflate(R.layout.fragment_overview, container, false)

        addData(overviewView)

        return overviewView
    }

    private fun addData(overviewView: View) {
        val groupedList = analyseLogs()
        val chartDetailBox = overviewView.findViewById<LinearLayout>(R.id.chartDetailsBox)
        val detailHoursBox = overviewView.findViewById<LinearLayout>(R.id.hoursList)
        val totalHourText = overviewView.findViewById<TextView>(R.id.overviewTotalHours)

        var totalHour = 0.0

        val pieChart = overviewView.findViewById<PieChart>(R.id.piechart);

        for (i in 0 until groupedList.size) {

            val colour = mainActivity.getColor(findColour(groupedList[i].first))
            pieChart.addPieSlice(
                PieModel(
                    groupedList[i].first, groupedList[i].second.toFloat(),
                    colour
                )
            )
            totalHour += groupedList[i].second


            val chartDetailRow = LinearLayout(chartDetailBox.context)

            val rowParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            rowParam.setMargins(15,15,15,15)

            chartDetailRow.layoutParams = rowParam

            val hourRow = LinearLayout(detailHoursBox.context)
            hourRow.layoutParams = rowParam

            val colourView = View(chartDetailRow.context)
            val hourColourView = View(hourRow.context)

            val colourParam = LinearLayout.LayoutParams(50,
                50)
            colourParam.setMargins(5,15,5,15)

            colourView.layoutParams = colourParam
            hourColourView.layoutParams = colourParam

            colourView.setBackgroundColor(colour)
            hourColourView.setBackgroundColor(colour)

            chartDetailRow.addView(colourView)
            hourRow.addView(hourColourView)

            val rowTextParam = LinearLayout.LayoutParams(250,
                LinearLayout.LayoutParams.WRAP_CONTENT)

            val rowText = TextView(chartDetailRow.context)
            rowText.setText(groupedList[i].first)
            rowText.layoutParams = rowTextParam

            chartDetailRow.addView(rowText)

            chartDetailBox.addView(chartDetailRow)

            val hourRowText = TextView(hourRow.context)
            hourRowText.setText(groupedList[i].first)
            hourRowText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19.5f)
            hourRowText.layoutParams = rowTextParam

            hourRow.addView(hourRowText)


            val hourLoggedParam = LinearLayout.LayoutParams(500,
                LinearLayout.LayoutParams.WRAP_CONTENT)

            val hoursLogged = TextView(hourRow.context)
            hoursLogged.setText("${groupedList[i].second.toString()} Hour(s)")
            hoursLogged.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19.5f)
            hoursLogged.gravity = Gravity.CENTER
            hoursLogged.layoutParams = hourLoggedParam

            hourRow.addView(hoursLogged)

            detailHoursBox.addView(hourRow)

        }
        totalHourText.setText("${mainActivity.getText(R.string.totalHoursLogged)} ${totalHour}")
        totalHourText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f)

        pieChart.startAnimation()
    }


    private fun createParams() : ViewGroup.LayoutParams {
        var left = 10
        var right = 10

        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(left, 0, right, 0)

        return params
    }


    @SuppressLint("DiscouragedApi")
    fun findColour(name: String?): Int {

        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")
        val fileName = "colours.json"
        file = File(myDir, fileName)

        val coloursJSONString = file.bufferedReader().use {
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

    private fun analyseLogs(): ArrayList<Pair<String, Double>> {
        val storedLogs = getStoredLogs()

        val pairList = ArrayList<Pair<String, Double>>()
        val activityList = ArrayList<String>()
        val hourList = ArrayList<Double>()

        if (storedLogs.length() > 0) {
            for (i in 0 until storedLogs.length()) {
                val tmpLog = storedLogs.getJSONObject(i)
                val tmpLogActName = tmpLog.getString("activityName")

                val duration =
                    tmpLog.getString("endingTime").replace(":",".").toDouble() -
                            tmpLog.getString("startingTime").replace(":",".").toDouble()

                if (!activityList.contains(tmpLogActName)) {
                    activityList.add(tmpLogActName)

                    hourList.add(duration)
                }else {
                    val position = activityList.indexOf(tmpLogActName)
                    hourList[position] += duration
                }
            }

            for (j in 0 until activityList.size) {
                pairList.add(Pair(activityList[j],hourList[j]))
            }
        }

        return pairList
    }


    private fun getStoredLogs(): JSONArray {
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "logsData.json"
        file = File(myDir, fileName)

        try {

            val jsonString = file.bufferedReader().use { it.readText() }

            val outputJson = JSONObject(jsonString)
            val logs = outputJson.getJSONArray("logs") as JSONArray
            return logs
        }catch (e: Exception) {
            file.createNewFile()
            val logs = JSONArray()
            return logs
        }
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment Overview.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            Overview().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}