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
import android.util.Log
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
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
class AddLogPopUp(mainAct: MainActivity) : DialogFragment() {
    val mainActivity = mainAct
    val calendar = Calendar.getInstance()
    var imageView: ImageView? = null
    var imageSrc: String? = null
    var btnChosen = false
    var bitMapUri: Uri? = null
    var chosenDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    var currentlyChosenYear : Int = calendar.get(Calendar.YEAR)
    var currentlyChosenMonth : Int = calendar.get(Calendar.MONTH)
    var currentlyChosenDay : Int = calendar.get(Calendar.DAY_OF_MONTH)

    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val popUpView = inflater.inflate(R.layout.fragment_add_log_pop_up, container, false)
        val startTimeBtn = popUpView.findViewById<Button>(R.id.start_time_picker)
        val endTimeBtn = popUpView.findViewById<Button>(R.id.end_time_picker)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            popUpView.findViewById<ImageView>(R.id.log_image).setImageDrawable(
                mainActivity.getDrawable(R.drawable.backgroundstuffdark))
        }

        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = LocalTime.now()
        startTimeBtn.setText(currentTime.format(timeFormat))
        endTimeBtn.setText(currentTime.plusHours(1).format(timeFormat))

        val titleInput = popUpView.findViewById<EditText>(R.id.title_input)

        val btnColorPairs = ArrayList<Pair<String, Int>>()
        btnColorPairs.add(Pair("Reading", R.color.purple))
        btnColorPairs.add(Pair("Work", R.color.blue))
        btnColorPairs.add(Pair("Church", R.color.green))
        btnColorPairs.add(Pair("Workout", R.color.red))

        val btnsIcons = ArrayList<ButtonIcons>()
        btnsIcons.add(ButtonIcons("Reading", R.color.purple, R.drawable.baseline_book_24))
        btnsIcons.add(ButtonIcons("Work", R.color.blue, R.drawable.baseline_work_24))
        btnsIcons.add(ButtonIcons("Church", R.color.green, R.drawable.baseline_church_24))
        btnsIcons.add(ButtonIcons("Workout", R.color.red, R.drawable.baseline_directions_bike_24))


        try {
            val buttonsScroll = popUpView.findViewById<LinearLayout>(R.id.catBtnScroll)
            var catBtns = java.util.ArrayList<Button>()
            for (i in 0..btnsIcons.size - 1) {
                val btn = Button(buttonsScroll.context)
                btn.layoutParams = createParams()
                btn.setBackgroundColor(R.color.light_gray)

                btn.setText(btnColorPairs.get(i).first)
                btn.height = 40
                btn.setTextColor(R.color.black)
                btn.elevation = 8F
                btn.isSelected = false

                val btnIcon = mainActivity.getDrawable(btnsIcons.get(i).vector)
                if (btnIcon != null) {
//                    btnIcon.setTint(btnColorPairs.get(i).second)
                    btnIcon.setTintList(mainActivity.getColorStateList(btnsIcons.get(i).color))
                }

                btn.setCompoundDrawablesWithIntrinsicBounds(btnIcon, null, null, null)

//                btn.setBackgroundColor()
                btn.backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)

                catBtns.add(btn)

                btn.setOnClickListener{

                    if (!btnChosen) {
                        btn.isSelected = true
//                        btn.backgroundTintList = mainActivity.getColorStateList(btnColorPairs.get(i).second)
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
//                            btn.backgroundTintList = mainActivity.getColorStateList(btnColorPairs.get(i).second)
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.gray)
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
//            val text = addLog(title, startTimeBtn, endTimeBtn)
            val text = addLog(title, startTimeBtn, endTimeBtn, popUpView)
            if (text == "Wrong data"){
                var builder = AlertDialog.Builder(mainActivity)
                if (title.text.toString() == "") {
                    builder.setTitle("Title cannot be empty! You can press on the buttons fill it in!")
                }else {
                    builder.setTitle("Inputted time is wrong! Try again!")
                }
                builder.setPositiveButton("Okay") { dialog, which ->
                }

                builder.show()
            }else{

                val storedLogs = getStoredLogs()

                try {
                    imageSrc = saveImage(MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, bitMapUri))
                }catch (e: Exception) {
                    Log.d("ImageURI", e.message.toString())
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
                for (i in 0 until storedLogs.length()) {
                    tmpJSONArray.put(storedLogs.getJSONObject(i))
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
                }catch (e: Exception) {
                    Log.d("logs-saving", e.message.toString())
                }
                dismiss()
                Toast.makeText(mainActivity, "Log saved", Toast.LENGTH_LONG).show()


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
        }catch (e: Exception) {
            Log.d("Images-saving", e.message.toString())
        }
        return fileName
    }

    private fun browseImage(galleryImage: ActivityResultLauncher<String>) {
        try {
            galleryImage.launch("image/*")
        }catch (e: Exception) {
            Log.d("LOL", e.message.toString())
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
    private fun addLog(title: EditText, startTimeBtn: Button, endTimeBtn: Button, popupView: View): String?{

        val startingTime = startTimeBtn.text.toString().replace(":",".").toDouble()
        val endingTime = endTimeBtn.text.toString().replace(":",".").toDouble()
        var textToShow = ""

        val storedLogs = getStoredLogs()
        for (i in 0 until storedLogs.length()) {
            val tmpLog = storedLogs.getJSONObject(i)
            val tmpLogStart = tmpLog.getString("startingTime").toString().replace(":",".").toDouble()
            val tmpLogEnd = tmpLog.getString("endingTime").toString().replace(":",".").toDouble()

            if (endingTime - startingTime < 1.00 || title.text.toString() == "" || (tmpLog.get("date") == chosenDate
                && ((tmpLogStart <= startingTime
                        || startingTime >= tmpLogEnd)
                || (tmpLogStart <= endingTime
                        || endingTime >= tmpLogEnd)))) {
                textToShow = "Wrong data"
            }else {
                textToShow = "Log added"
            }
        }
        return textToShow
    }
}