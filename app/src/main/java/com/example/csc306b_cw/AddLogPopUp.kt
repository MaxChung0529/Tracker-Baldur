package com.example.csc306b_cw

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLogPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddLogPopUp(mainAct: MainActivity) : DialogFragment() {
    val mainActivity = mainAct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val popUpView = inflater.inflate(R.layout.fragment_add_log_pop_up, container, false)
        val startTimeBtn = popUpView.findViewById<Button>(R.id.start_time_picker)
        val endTimeBtn = popUpView.findViewById<Button>(R.id.end_time_picker)

        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalTime.now()
        startTimeBtn.setText(currentTime.format(timeFormat))
        endTimeBtn.setText(currentTime.plusHours(1).format(timeFormat))

//        val buttonsScroll = popUpView.findViewById<LinearLayout>(R.id.catBtnsLayout)
//        val childNum = buttonsScroll.childCount
//        var catBtns = listOf<View>()
//        for (i in 0..childNum){
//            catBtns += buttonsScroll.getChildAt(i)
//
//            val btn = catBtns.get(i)
//            btn.setOnClickListener {
//                var color = Color.rgb(0, 100, 0)
//                btn.setBackgroundColor(color)
//            }
//        }

        val cancel_btn = popUpView.findViewById<Button>(R.id.cancel_btn)
        cancel_btn.setOnClickListener{
            dismiss()
        }

        val submit_btn = popUpView.findViewById<Button>(R.id.submit_btn)
        submit_btn.setOnClickListener{
            val title = popUpView.findViewById<EditText>(R.id.title_input)
            val description = popUpView.findViewById<EditText>(R.id.description)
            val text = addLog(title, startTimeBtn, endTimeBtn, description)
            if (text == "Wrong data"){
                var builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("Inputted time is wrong! Try again!")

                builder.setPositiveButton("Okay") { dialog, which ->
                }

                builder.show()
            }else{
                val entry = createJsonData(title.text.toString(), "Reading", startTimeBtn.text.toString(), endTimeBtn.text.toString(), description.text.toString())

                var fileOut = mainActivity.openFileOutput("logsData.json", Context.MODE_PRIVATE)
//                fileOut.close()
//                fileOut = mainActivity.openFileOutput("logsData.json", Context.MODE_PRIVATE)

                val outputWriter = OutputStreamWriter(fileOut)
                outputWriter.write(entry.toString() + ",\n")
                outputWriter.close()
                dismiss()

                getInternal()

            }
        }

        startTimeBtn.setOnClickListener{
            val time = pickStartTime(startTimeBtn, endTimeBtn)
        }

        endTimeBtn.setOnClickListener{
            val time = pickEndTime(startTimeBtn, endTimeBtn)
        }

        // Inflate the layout for this fragment
        return popUpView
    }

    private fun getInternal() {
        val file = mainActivity.openFileInput("logsData.json")
        val inputReader = InputStreamReader(file)

        val toast = Toast.makeText(mainActivity, inputReader.readText(), Toast.LENGTH_LONG)
        file.close()
        toast.show()

    }

    private fun createJsonData(title: String?, category: String?, startTime: String, endTime: String, description: String?): JSONObject {
        var json = JSONObject()

        json.put("date", "16-04-2024")

        if (title == null) {
            json.put("activityName", category)
        }else {
            json.put("activityName", title)
        }
        json.put("startingTime", startTime)
        json.put("endingTime", endTime)
        if (description != null){
            json.put("description", description)
        }

        return json
    }

    private fun pickStartTime(startTimeBtn: Button, endTimeBtn: Button): String?{
        val cal = Calendar.getInstance()
        var startTime: String? = null
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            startTime = SimpleDateFormat("HH:mm").format(cal.time)

            startTimeBtn.setText(startTime)

            cal.add(Calendar.HOUR_OF_DAY, 1)

            endTimeBtn.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
//        TimePickerDialog(mainActivity, R.style.ScrollTimePicker, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        TimePickerDialog(mainActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        return startTime
    }

    private fun pickEndTime(startTimeBtn: Button, endTimeBtn: Button){
        val cal = Calendar.getInstance()
        var endTime: String? = null
        var moreThanOneHour = false;


        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                endTime = SimpleDateFormat("HH:mm").format(cal.time)

                endTimeBtn.setText(endTime)
        }

//        TimePickerDialog(mainActivity, R.style.ScrollTimePicker, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        TimePickerDialog(mainActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }
    private fun addLog(title: EditText, startTimeBtn: Button, endTimeBtn: Button, description: EditText): String?{

        val startingTime = startTimeBtn.text.split(":").toTypedArray()
        val endingTime = endTimeBtn.text.split(":").toTypedArray()
        var textToShow = ""

        if ((endingTime[0].toInt() > startingTime[0].toInt() + 1)
            or ((endingTime[0].toInt() == startingTime[0].toInt() + 1) and (endingTime[1].toInt() >= startingTime[1].toInt()))
        ) {
            textToShow = "Log added"
        }else {
            textToShow = "Wrong data"
        }
        return textToShow
    }
}