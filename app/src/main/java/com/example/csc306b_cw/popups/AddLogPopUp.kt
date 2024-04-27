package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStream
import java.time.LocalDate
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
class AddLogPopUp() : DialogFragment() {
    lateinit var mainActivity: MainActivity
    val calendar = Calendar.getInstance()
    val calendarTime = Calendar.getInstance()
    var imageView: ImageView? = null
    var imageSrc: String? = null
    var btnChosen = false
    var bitMapUri: Uri? = null
    var chosenDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    var currentlyChosenYear : Int = calendar.get(Calendar.YEAR)
    var currentlyChosenMonth : Int = calendar.get(Calendar.MONTH)
    var currentlyChosenDay : Int = calendar.get(Calendar.DAY_OF_MONTH)
    var startingHour = 0
    var startingMinute = 0
    var endingHour = 0
    var endingMinute = 0

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = context as MainActivity

        val popUpView = inflater.inflate(R.layout.fragment_add_log_pop_up, container, false)
        val startTimeBtn = popUpView.findViewById<Button>(R.id.start_time_picker)
        val endTimeBtn = popUpView.findViewById<Button>(R.id.end_time_picker)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            popUpView.findViewById<ImageView>(R.id.log_image).setImageDrawable(
                mainActivity.getDrawable(R.drawable.backgroundstuffdark))
        }else {
            popUpView.findViewById<ImageView>(R.id.log_image).setImageDrawable(
                mainActivity.getDrawable(R.drawable.backgroundstuff))
        }

        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalTime.now()
        startTimeBtn.setText(currentTime.format(timeFormat))
        endTimeBtn.setText(currentTime.plusHours(1).format(timeFormat))

        val sharedPref = mainActivity.getSharedPreferences("TimerPref", Context.MODE_PRIVATE)
        val startingTime = sharedPref.getString("Starting Time", null)
        val endingTime = sharedPref.getString("Ending Time", null)

        if (startingTime != null && endingTime != null) {
            startingHour = startingTime.split(":")[0].toInt()
            startingMinute = startingTime.split(":")[1].toInt()
            endingHour = endingTime.split(":")[0].toInt()
            endingMinute = endingTime.split(":")[1].toInt()

            startTimeBtn.setText(startingTime)
            endTimeBtn.setText(endingTime)

            //Clear stored time
            sharedPref.edit().clear().apply()
        }

        val titleInput = popUpView.findViewById<EditText>(R.id.title_input)

        val btnsIcons = mainActivity.getButtonIcons()

        try {
            val buttonsScroll = popUpView.findViewById<LinearLayout>(R.id.catBtnScroll)
            var catBtns = java.util.ArrayList<Button>()
            for (i in 0..btnsIcons.size - 1) {
                val btn = Button(buttonsScroll.context)

                val btnIcon = mainActivity.getDrawable(btnsIcons.get(i).vector)
                if (btnIcon != null) {
                    btnIcon.setTintList(mainActivity.getColorStateList(mainActivity.findColour(btnsIcons.get(i).category)))
                }

                btn.setCompoundDrawablesWithIntrinsicBounds(btnIcon, null, null, null)

                btn.setText(btnsIcons.get(i).category)
                btn.height = 40
                btn.setTextColor(R.color.black)
                btn.elevation = 8F
                btn.isSelected = false
                btn.backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)

                catBtns.add(btn)

                btn.setOnClickListener{

                    if (!btnChosen) {
                        btn.isSelected = true
                        btn.backgroundTintList = mainActivity.getColorStateList(R.color.gray)
                        btnChosen = true
                        titleInput.setText(btn.text)

                    }else {
                        if (btn.isSelected) {
                            btnChosen = false
                            btn.isSelected = false
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)
                            titleInput.setText("")
                        }else {
                            for (i in 0 until catBtns.size) {
                                catBtns.get(i).isSelected = false
                                catBtns.get(i).backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)
                            }
                            btn.isSelected = true
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.gray)
                            btnChosen = true
                            titleInput.setText(btn.text)
                        }
                    }
                }

                buttonsScroll.addView(btn)
            }
        }catch (_: Exception) {
        }

        val datePicker = popUpView.findViewById<Button>(R.id.add_log_date_picker)
        datePicker.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString())
        datePicker.setOnClickListener{
            showDatePicker(datePicker)
        }

        imageView = popUpView.findViewById<ImageView>(R.id.log_image)

        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                bitMapUri = it
                imageView?.setImageURI(bitMapUri)
            })

        val uploadImageBtn = popUpView.findViewById<Button>(R.id.upload_image)
        uploadImageBtn.setOnClickListener{
            browseImage(galleryImage)
        }

        val cancel_btn = popUpView.findViewById<Button>(R.id.cancel_btn)
        cancel_btn.setOnClickListener{
            dismiss()
        }

        val submit_btn = popUpView.findViewById<Button>(R.id.submit_btn)
        submit_btn.setOnClickListener{
            val title = popUpView.findViewById<EditText>(R.id.title_input)
            val description = popUpView.findViewById<EditText>(R.id.description)
            val text = addLog(title, startTimeBtn, endTimeBtn, popUpView)
            if (text == "Log added"){

                val storedLogs = getStoredLogs()

                try {
                    imageSrc = saveImage(MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, bitMapUri))
                }catch (e: Exception) {
                    imageSrc = ""
                }

                val entry = createJsonData(datePicker.text.toString(), title.text.toString()
                    , startTimeBtn.text.toString(), endTimeBtn.text.toString()
                    , description.text.toString(), imageSrc)

                var file: File? = null
                val root = mainActivity.getExternalFilesDir(null)?.absolutePath
                var myDir = File("$root/TrackerBaldur")

                if (!myDir.exists()) {
                    myDir.mkdirs()
                }

                var tmpJSONArray = JSONArray()

                if (storedLogs.length() > 0) {
                    for (i in 0 until storedLogs.length()) {
                        tmpJSONArray.put(storedLogs.getJSONObject(i))
                    }
                }

                tmpJSONArray.put(entry)

                val logsArray = JSONObject()

                logsArray.put("logs",tmpJSONArray)

                val fileName = "logsData.json"
                file = File(myDir, fileName)
                try {
                    val output = BufferedWriter(FileWriter(file))
                    output.write(logsArray.toString())
                    output.close()
                }catch (_: Exception) {
                }

                checkGoalsMatch(entry)

                dismiss()
                Toast.makeText(mainActivity, "Log saved", Toast.LENGTH_LONG).show()

                mainActivity.logFragment.refresh()


            }else {
                var builder = AlertDialog.Builder(mainActivity)
                if (text == "Empty Title") {
                    builder.setTitle("Title cannot be empty! You can press on the buttons fill it in!")
                }else if (text == "Too Short"){
                    builder.setTitle("Log needs to be at least an hour!")
                }else if (text == "Overlap"){
                    builder.setTitle("Time overlapped with other logs! Try again!")
                }
                builder.setPositiveButton("Okay") { dialog, which ->
                }

                builder.show()
            }
        }

        startTimeBtn.setOnClickListener{
            pickStartTime(startTimeBtn, endTimeBtn)
        }

        endTimeBtn.setOnClickListener{
            pickEndTime(startTimeBtn, endTimeBtn)
        }

        // Inflate the layout for this fragment
        return popUpView
    }

    fun checkGoalsMatch(entry: JSONObject) {
        val storedGoals = getStoredGoals()

        for (i in 0 until storedGoals.size) {

            val logDuration =
                entry.getString("endingTime").replace(":",".").toDouble() -
                        entry.getString("startingTime").replace(":",".").toDouble()

            if (entry.getString("activityName") == storedGoals[i].goalName
                && logDuration >= storedGoals[i].durationPerUnit!!
                && storedGoals[i].progressNow!! < storedGoals[i].progressGoal!!
                && LocalDate.now().isBefore(LocalDate.parse(storedGoals[i].deadline, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            ){

                val oldGoal = storedGoals[i]
                storedGoals[i].progressNow?.plus(logDuration)

                storedGoals[i] = GoalsData(oldGoal.goalName, oldGoal.interval, oldGoal.unit
                    , oldGoal.durationPerUnit
                    , oldGoal.progressNow?.plus(logDuration)?.times(100.00)?.div(100)
                    , oldGoal.progressGoal, oldGoal.deadline, oldGoal.description, oldGoal.imgSrc)

                refreshStoredGoals(storedGoals)
            }
        }

    }

    private fun createGoalJsonData(goalName: String?, interval: String?, unit: String?, duration: Double?
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
                createGoalJsonData(tmpGoal.goalName, tmpGoal.interval.toString(), tmpGoal.unit,
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
        }catch (_: Exception) {
        }
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
    private fun saveImage(bitmap: Bitmap): String{
        var fileOutputStream : OutputStream
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val fileName = "Images-" + System.currentTimeMillis() + ".png"
        file = File(myDir, fileName)
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            Toast.makeText(mainActivity, "Image saved", Toast.LENGTH_LONG).show()
            fileOutputStream.flush()
            fileOutputStream.close()
        }catch (_: Exception) {
        }
        return file.toString()
    }

    private fun browseImage(galleryImage: ActivityResultLauncher<String>) {
        try {
            galleryImage.launch("image/*")
        }catch (_: Exception) {
        }
    }
    private fun showDatePicker(datePickerBtn: Button) {
        val datePickerDialog = DatePickerDialog(mainActivity,
            {DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)

                currentlyChosenYear = year
                currentlyChosenMonth = month
                currentlyChosenDay = day

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format((selectedDate.time))
                datePickerBtn.setText(formattedDate)
                chosenDate = formattedDate

            },
            currentlyChosenYear,
            currentlyChosenMonth,
            currentlyChosenDay

        )
        datePickerDialog.show()
    }

    private fun createJsonData(date: String, title: String?, startTime: String, endTime: String, description: String?, imgSrc: String?): JSONObject {
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

    private fun pickStartTime(startTimeBtn: Button, endTimeBtn: Button): String?{
        var startTime: String? = null
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendarTime.set(Calendar.HOUR_OF_DAY, hour)
            calendarTime.set(Calendar.MINUTE, minute)
            startTime = SimpleDateFormat("HH:mm").format(calendarTime.time)

            startTimeBtn.setText(startTime)

            calendarTime.add(Calendar.HOUR_OF_DAY, 1)

            endTimeBtn.setText(SimpleDateFormat("HH:mm").format(calendarTime.time))
        }

        if (startingHour == 0 || startingMinute == 0) {
            TimePickerDialog(
                mainActivity,
                timeSetListener,
                calendarTime.get(Calendar.HOUR_OF_DAY),
                calendarTime.get(Calendar.MINUTE),
                true
            ).show()
        }else {
            TimePickerDialog(
                mainActivity,
                timeSetListener,
                startingHour,
                startingMinute,
                true
            ).show()
        }

        return startTime
    }

    private fun pickEndTime(startTimeBtn: Button, endTimeBtn: Button){
        var endTime: String? = null
        var moreThanOneHour = false;


        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

            calendarTime.set(Calendar.HOUR_OF_DAY, hour)
            calendarTime.set(Calendar.MINUTE, minute)
                endTime = SimpleDateFormat("HH:mm").format(calendarTime.time)

                endTimeBtn.setText(endTime)
        }

        if (endingHour == 0 || endingMinute == 0) {
            TimePickerDialog(
                mainActivity,
                timeSetListener,
                endTimeBtn.text.split(":")[0].toInt(),
                calendarTime.get(Calendar.MINUTE),
                true
            ).show()
        }else {
            TimePickerDialog(
                mainActivity,
                timeSetListener,
                endingHour,
                endingMinute,
                true
            ).show()
        }
    }
    private fun addLog(title: EditText, startTimeBtn: Button, endTimeBtn: Button, popupView: View): String?{

        val startingTime = startTimeBtn.text.toString().replace(":",".").toDouble()
        val endingTime = endTimeBtn.text.toString().replace(":",".").toDouble()
        var textToShow = ""

        val storedLogs = getStoredLogs()

        if (storedLogs.length() > 0) {

            for (i in 0 until storedLogs.length()) {
                val tmpLog = storedLogs.getJSONObject(i)
                val tmpLogStart =
                    tmpLog.getString("startingTime").toString().replace(":", ".").toDouble()
                val tmpLogEnd =
                    tmpLog.getString("endingTime").toString().replace(":", ".").toDouble()

                if (endingTime - startingTime < 1.00
                    || title.text.toString() == ""
                    || (tmpLog.get("date") == chosenDate
                            && ((tmpLogStart <= startingTime && startingTime <= tmpLogEnd)
                            || (tmpLogStart <= endingTime && endingTime <= tmpLogEnd)))
                ) {
                    if (endingTime - startingTime < 1.00) {
                        textToShow = "Too Short"
                    } else if (title.text.toString() == "") {
                        textToShow = "Empty Title"
                    } else {
                        textToShow = "Overlap"
                    }
                } else {
                    textToShow = "Log added"
                }
            }
        }else {
            textToShow = "Log added"
        }
        return textToShow
    }
}