package com.example.csc306b_cw

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings(mainActivity: MainActivity) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mainActivity = mainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val settingView =  inflater.inflate(R.layout.fragment_settings, container, false)

        val themeSwitch = settingView.findViewById<Switch>(R.id.theme_switch)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

        themeSwitch.setOnClickListener{

            val parent = activity as MainActivity
            parent.justSwitchedMode()

            if (isDarkModeOn) {
                themeSwitch.isChecked = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }else{
                themeSwitch.isChecked = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        val exportBtn = settingView.findViewById<Button>(R.id.exportBtn)
        exportBtn.setOnClickListener{
            FileOutputStream("logsData.cs").apply { writeCsv() }
        }

        return settingView
    }
    fun OutputStream.writeCsv() {

        val logsData = ArrayList<LogsData>()
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "logsData.json"
        val file = File(myDir, fileName)

        val jsonString = file.bufferedReader().use { it.readText() }
        val outputJson = JSONObject(jsonString)
        val logs = outputJson.getJSONArray("logs") as JSONArray

        for (i in 0 until logs.length()) {
            val date = logs.getJSONObject(i).getString("date")
            val activityName = logs.getJSONObject(i).getString("activityName")
            val startingTime = logs.getJSONObject(i).getString("startingTime")
            val endingTime = logs.getJSONObject(i).getString("endingTime")
            val description = logs.getJSONObject(i).getString("description")
            val imgSrc = logs.getJSONObject(i).getString("imgSrc")
            logsData.add(
                    LogsData(
                        date,
                        activityName,
                        startingTime,
                        endingTime,
                        Math.round((endingTime.replace(":",".").toDouble()
                                - startingTime.replace(":",".").toDouble()) * 100.00)
                                / 100.00,
                        description,
                        imgSrc
                    )
                )
        }

        val outFile = File(myDir, "logsData.csv")

        val writer = outFile.bufferedWriter()
        writer.write(""""Date", "Activity Name", "Time", "Starting Time", "Ending Time", "Description", "Image"""")
        writer.newLine()
        logsData.forEach {
            writer.write("${it.date}, ${it.activityName}, \"${it.startingTime}\", ${it.endingTime}, ${it.duration}, ${it.description}, ${it.imgSrc}")
            writer.newLine()
        }
        writer.flush()
        writer.close()
    }
    private fun getStoredLogs(): JSONArray {
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "logsData.json"
        file = File(myDir, fileName)

        val jsonString = file.bufferedReader().use { it.readText() }

        val outputJson = JSONObject(jsonString)
        val logs = outputJson.getJSONArray("logs") as JSONArray
        return logs
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment Settings.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String, mainActivity: MainActivity) =
//            Settings().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}