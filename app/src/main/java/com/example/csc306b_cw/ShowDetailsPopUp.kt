package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLogPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowDetailsPopUp(mainAct: MainActivity, detailsObj: JSONObject) : DialogFragment() {
    val mainActivity = mainAct
    val detailsObj = detailsObj

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val popUpView = inflater.inflate(R.layout.fragment_show_details_pop_up, container, true)

        popUpView.findViewById<TextView>(R.id.detailCatName).setText(detailsObj.getString("activityName"))
        val start = detailsObj.getString("startingTime")
        val end = detailsObj.getString("endingTime")
        val formattedTime = "$start - $end"
        popUpView.findViewById<TextView>(R.id.detailDate).setText(detailsObj.getString("date"))
        popUpView.findViewById<TextView>(R.id.detailCatTimePeriod).setText(formattedTime)
        popUpView.findViewById<TextView>(R.id.detailDescContent).setText(detailsObj.getString("description"))


        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    mainActivity.logFragment.refresh()
                    dismiss()
                }
            })


        val editLogBtn = popUpView.findViewById<Button>(R.id.editBtn)
        editLogBtn.setOnClickListener{
            //Implicit Intent?
            val editLogIntent = Intent(mainActivity, EditLogActivity::class.java)
            editLogIntent.putExtra("date", detailsObj.getString("date"))
            editLogIntent.putExtra("activityName", detailsObj.getString("activityName"))
            editLogIntent.putExtra("startingTime", detailsObj.getString("startingTime"))
            editLogIntent.putExtra("endingTime", detailsObj.getString("endingTime"))
            editLogIntent.putExtra("description", detailsObj.getString("description"))
            editLogIntent.putExtra("imgSrc",detailsObj.getString("imgSrc"))

//            startActivity(editLogIntent)
            startForResult.launch(editLogIntent)
//            dismiss()
        }

        val deleteLogBtn = popUpView.findViewById<Button>(R.id.deleteBtn)
        deleteLogBtn.setOnClickListener{
            confirmDelete()
        }

        val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val startingTime = LocalTime.parse(start, dateTimeFormatter)
        val endingTime = LocalTime.parse(end, dateTimeFormatter)

        val hourDiff = endingTime.hour - startingTime.hour
        val minuteDiff = endingTime.minute - startingTime.minute

        if (hourDiff.toInt() > 1){
            popUpView.findViewById<TextView>(R.id.detailHour).setText("$hourDiff hours")
        }else {
            popUpView.findViewById<TextView>(R.id.detailHour).setText("$hourDiff hour")
        }
        if (minuteDiff.toInt() > 1){
            popUpView.findViewById<TextView>(R.id.detailMinute).setText("$minuteDiff minutes")
        }else {
            popUpView.findViewById<TextView>(R.id.detailMinute).setText("$minuteDiff minute")
        }

        try {
            if (detailsObj.getString("imgSrc") != "") {
                val detailImage = popUpView.findViewById<ImageView>(R.id.detailImage)
                detailImage.setImageURI(Uri.parse(detailsObj.getString("imgSrc")))
            }
        }catch (e: Exception){
            Log.d("ImgSrcLOL", e.message.toString())
        }

        // Inflate the layout for this fragment
        return popUpView
    }

    private fun replaceLog(newLog: Intent) {
        deleteLog()

        if (newLog != null) {
            val storedLogs = getStoredLogs()

            val entry = createJsonData(
                newLog.getStringExtra("date"),
                newLog.getStringExtra("activityName"),
                newLog.getStringExtra("startingTime"),
                newLog.getStringExtra("endingTime"),
                newLog.getStringExtra("description"),
                newLog.getStringExtra("imgSrc")
            )

            var file: File? = null
            val root = mainActivity.getExternalFilesDir(null)?.absolutePath
            var myDir = File("$root/TrackerBaldur")

            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            var tmpJSONArray = JSONArray()
            for (i in 0 until storedLogs.length()) {
                tmpJSONArray.put(storedLogs.getJSONObject(i))
            }
            tmpJSONArray.put(entry)

            val logsArray = JSONObject()

            logsArray.put("logs", tmpJSONArray)

            val fileName = "logsData.json"
            file = File(myDir, fileName)
            try {
                val output = BufferedWriter(FileWriter(file))
                output.write(logsArray.toString())
                output.close()
            } catch (e: Exception) {
                Log.d("logs-saving", e.message.toString())
            }
        }
    }

    private fun confirmDelete() {
        var builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("You sure you want to delete the log?")
        builder.setPositiveButton("Yes") { dialog, which ->
            deleteLog()
            dismiss()
            mainActivity.logFragment.refresh()
        }
        builder.setNegativeButton("No") {dialog, which ->
        }
        builder.show()
    }

    private fun deleteLog() {
        val storedLogs = getStoredLogs()

        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        var tmpJSONArray = JSONArray()
        for (i in 0 until storedLogs.length()) {
            val checkObj = storedLogs.getJSONObject(i)
            if (checkObj.get("date") != detailsObj.get("date")
                || checkObj.get("activityName") != detailsObj.get("activityName")
                || checkObj.get("startingTime") != detailsObj.get("startingTime")
                || checkObj.get("endingTime") != detailsObj.get("endingTime")) {
                tmpJSONArray.put(storedLogs.getJSONObject(i))
            }
        }
        val logsArray = JSONObject()

        logsArray.put("logs",tmpJSONArray)

        val fileName = "logsData.json"
        file = File(myDir, fileName)
        try {
            val output = BufferedWriter(FileWriter(file))
            output.write(logsArray.toString())
            output.close()
        }catch (e: Exception) {
            Log.d("logs-saving", e.message.toString())
        }
    }

    private fun createJsonData(date: String?, title: String?, startTime: String?, endTime: String?, description: String?, imgSrc: String?): JSONObject {
        var json = JSONObject()

        json.put("date", date)
        json.put("activityName", title)
        json.put("startingTime", startTime)
        json.put("endingTime", endTime)
        if (description != null){
            json.put("description", description)
        }else {
            json.put("description", "")
        }
        json.put("imgSrc", imgSrc)

        return json
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
}