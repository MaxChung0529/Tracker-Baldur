package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class EditLogActivity : AppCompatActivity() {
//    lateinit var mainActivity: MainActivity
    var date: String? = null
    var activityName: String? = null
    var startingTime: String? = null
    var endingTime: String? = null
    var description: String? = null
    var imgSrc: String? = null
    val calendar = Calendar.getInstance()
    val calendarTime = Calendar.getInstance()
    var chosenDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    var currentlyChosenYear : Int = calendar.get(Calendar.YEAR)
    var currentlyChosenMonth : Int = calendar.get(Calendar.MONTH)
    var currentlyChosenDay : Int = calendar.get(Calendar.DAY_OF_MONTH)
//    var imageView: ImageView? = null
    var imageSrc: String? = null
    var btnChosen = false
    var bitMapUri: Uri? = null
    lateinit var titleInput: EditText

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_log)

        val extra = intent.extras
        if (extra != null) {
            date = extra.getString("date")
            activityName = extra.getString("activityName")
            startingTime = extra.getString("startingTime")
            endingTime = extra.getString("endingTime")
            description = extra.getString("description")
            imgSrc = extra.getString("imgSrc")
        }

        val datePickerBtn = findViewById<Button>(R.id.edit_log_date_picker)
        datePickerBtn.setText(date)
        datePickerBtn.setOnClickListener{
            showDatePicker(datePickerBtn)
        }

        titleInput = findViewById<EditText>(R.id.edit_title_input)
        titleInput.setText(activityName)


        val btnsIcons = getButtonIcons()


        try {
            val buttonsScroll = findViewById<LinearLayout>(R.id.catBtnScroll)
            var catBtns = java.util.ArrayList<Button>()
            for (i in 0..btnsIcons.size - 1) {
                val btn = Button(buttonsScroll.context)

                val btnIcon = getDrawable(btnsIcons.get(i).vector)
                Log.d("DrawableID", btnIcon.toString())
                if (btnIcon != null) {
                    btnIcon.setTintList(getColorStateList(findColour(btnsIcons.get(i).category)))
                }

                btn.setCompoundDrawablesWithIntrinsicBounds(btnIcon, null, null, null)

                btn.setText(btnsIcons.get(i).category)
                btn.height = 40
                btn.setTextColor(R.color.black)
                btn.elevation = 8F
                btn.isSelected = false
                btn.backgroundTintList = getColorStateList(R.color.light_gray)

                btn.backgroundTintList = getColorStateList(R.color.light_gray)

                if (btn.text.toString() == activityName) {
                    btn.isSelected = true
                    btnChosen = true
                    btn.backgroundTintList = getColorStateList(R.color.gray)
                }

                catBtns.add(btn)

                btn.setOnClickListener{

                    if (!btnChosen) {
                        btn.isSelected = true
                        btn.backgroundTintList = getColorStateList(R.color.gray)
                        btnChosen = true
                        titleInput.setText(btn.text)

                    }else {
                        if (btn.isSelected) {
                            btnChosen = false
                            btn.isSelected = false
                            btn.backgroundTintList = getColorStateList(R.color.light_gray)
                            titleInput.setText("")
                        }else {
                            for (i in 0 until catBtns.size) {
                                catBtns.get(i).isSelected = false
                                catBtns.get(i).backgroundTintList = getColorStateList(R.color.light_gray)
                            }
                            btn.isSelected = true
                            btn.backgroundTintList = getColorStateList(R.color.gray)
                            btnChosen = true
                            titleInput.setText(btn.text)
                        }
                    }
                }

                buttonsScroll.addView(btn)
            }
        }catch (e: Exception) {
            Log.d("IDK", e.message.toString())
        }

        val startingTimeBtn = findViewById<Button>(R.id.edit_start_time_picker)
        val endingTimeBtn = findViewById<Button>(R.id.edit_end_time_picker)

        startingTimeBtn.setText(startingTime)
        startingTimeBtn.setOnClickListener{
            pickStartTime(startingTimeBtn, endingTimeBtn)
        }

        endingTimeBtn.setText(endingTime)
        endingTimeBtn.setOnClickListener{
            pickEndTime(startingTimeBtn,endingTimeBtn)
        }

        val descriptionBox = findViewById<EditText>(R.id.editDescription)
        descriptionBox.setText(description)

        val imageView = findViewById<ImageView>(R.id.edit_log_image)
        if (imgSrc != "") {
            imageView.setImageURI(Uri.parse(imgSrc))
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                bitMapUri = it
                imageView?.setImageURI(bitMapUri)
            })

        val removeImgBtn = findViewById<Button>(R.id.removeImgBtn)
        removeImgBtn.setOnClickListener{
            val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

            if (!isDarkModeOn) {
                imageView.setImageDrawable(getDrawable(R.drawable.backgroundstuff))
            }else {
                imageView.setImageDrawable(getDrawable(R.drawable.backgroundstuffdark))
            }

            imgSrc = ""
        }

        val uploadImageBtn = findViewById<Button>(R.id.edit_upload_image)
        uploadImageBtn.setOnClickListener{
            browseImage(galleryImage)
        }

        val cancelEditBtn = findViewById<Button>(R.id.cancelEditBtn)
        val submitEditBtn = findViewById<Button>(R.id.submitEditBtn)

        cancelEditBtn.setOnClickListener{
            finish()
        }

        submitEditBtn.setOnClickListener{
            val description = findViewById<EditText>(R.id.editDescription)
            val text = editLog(startingTimeBtn, endingTimeBtn)
            if (text == "Log edited"){

                try {
                    imgSrc = saveImage(MediaStore.Images.Media.getBitmap(contentResolver, bitMapUri))
                }catch (e: Exception) {
                    Log.d("ImageURI", e.message.toString())
                }
                Toast.makeText(this, "Log Edited", Toast.LENGTH_LONG).show()

                replaceLog(startingTimeBtn, endingTimeBtn, descriptionBox)
                setResult(Activity.RESULT_OK)
                finish()

            }else {
                var builder = AlertDialog.Builder(this)
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
    }

    private fun showDatePicker(datePickerBtn: Button) {
        val datePickerDialog = DatePickerDialog(this,
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

    @SuppressLint("DiscouragedApi")
    fun findColour(name: String?): Int{

        var file: File? = null
        val root = getExternalFilesDir(null)?.absolutePath
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

                val res = getResources()
                val packageName: String = getPackageName()

                val colorId = res.getIdentifier(colorName, "color", packageName)
                return colorId
            }
        }
        return -1
    }

    @SuppressLint("DiscouragedApi")
    fun getButtonIcons(): ArrayList<ButtonIcons> {
        var btnList = ArrayList<ButtonIcons>()

        try {

            val coloursFile = "catColors.json"
            val jsonString = application.assets.open(coloursFile).bufferedReader().use {
                it.readText()
            }

            val outputJson = JSONObject(jsonString)
            val colours = outputJson.getJSONArray("colours") as JSONArray

            val res = getResources()
            val packageName: String = getPackageName()

            if (colours.length() > 0) {
                for (i in 0 until colours.length()) {
                    val tmpName = colours.getJSONObject(i).getString("Name")
                    val tmpColour = findColour(colours.getJSONObject(i).getString("Colour"))

                    val vectorID = res.getIdentifier(tmpName.lowercase(), "drawable", packageName)
                    btnList.add(ButtonIcons(tmpName, tmpColour, vectorID))
                }
            }
        }catch (e: Exception) {
            Log.d("Buttons-finding", e.message.toString())
        }
        return btnList
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
        TimePickerDialog(this, timeSetListener, calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE), true).show()
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

        TimePickerDialog(this, timeSetListener, calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE), true).show()
    }

    private fun browseImage(galleryImage: ActivityResultLauncher<String>) {
        try {
            galleryImage.launch("image/*")
        }catch (e: Exception) {
            Log.d("LOL", e.message.toString())
        }
    }

    private fun saveImage(bitmap: Bitmap): String{
        var fileOutputStream : OutputStream
        var file: File? = null
        val root = getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val fileName = "Images-" + System.currentTimeMillis() + ".png"
        file = File(myDir, fileName)
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        }catch (e: Exception) {
            Log.d("Images-saving", e.message.toString())
        }
        return file.toString()
    }

    private fun getStoredLogs(): JSONArray {
        var file: File? = null
        val root = getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "logsData.json"
        file = File(myDir, fileName)

        val jsonString = file.bufferedReader().use { it.readText() }

        val outputJson = JSONObject(jsonString)
        val logs = outputJson.getJSONArray("logs") as JSONArray
        return logs
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

    private fun replaceLog(startTimeBtn: Button, endTimeBtn: Button, descriptionBox: EditText) {
        deleteLog()

            val storedLogs = getStoredLogs()

            val entry = createJsonData(
                chosenDate,
                titleInput.text.toString(),
                startTimeBtn.text.toString(),
                endTimeBtn.text.toString(),
                descriptionBox.text.toString(),
                imgSrc
            )

            var file: File? = null
            val root = getExternalFilesDir(null)?.absolutePath
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

    private fun deleteLog() {
        val storedLogs = getStoredLogs()

        var file: File? = null
        val root = getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        var tmpJSONArray = JSONArray()
        for (i in 0 until storedLogs.length()) {
            val checkObj = storedLogs.getJSONObject(i)
            if (checkObj.get("date") != date
                || checkObj.get("activityName") != activityName
                || checkObj.get("startingTime") != startingTime
                || checkObj.get("endingTime") != endingTime) {
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

    private fun editLog(startTimeBtn: Button, endTimeBtn: Button): String?{

        val startingTime = startTimeBtn.text.toString().replace(":",".").toDouble()
        val endingTime = endTimeBtn.text.toString().replace(":",".").toDouble()
        var textToShow = ""

        val storedLogs = getStoredLogs()
        for (i in 0 until storedLogs.length()) {
            val tmpLog = storedLogs.getJSONObject(i)
            val tmpLogStart = tmpLog.getString("startingTime").toString().replace(":",".").toDouble()
            val tmpLogEnd = tmpLog.getString("endingTime").toString().replace(":",".").toDouble()

            if ((endingTime - startingTime < 1.00
                || titleInput.text.toString() == ""
                || (tmpLog.get("date") == chosenDate
                        && ((tmpLogStart <= startingTime && startingTime <= tmpLogEnd)
                        || (tmpLogStart <= endingTime && endingTime <= tmpLogEnd))))
                && (chosenDate != tmpLog.getString("date")
                        && startingTime != tmpLogStart
                        && endingTime != tmpLogEnd)) {
                if (endingTime - startingTime < 1.00) {
                    textToShow = "Too Short"
                }else if (titleInput.text.toString() == "") {
                    textToShow = "Empty Title"
                }else {
                    textToShow = "Overlap"
                }
            }else {
                textToShow = "Log edited"
            }
        }
        return textToShow

    }
}