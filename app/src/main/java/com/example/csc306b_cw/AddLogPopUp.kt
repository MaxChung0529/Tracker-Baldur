package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Instrumentation.ActivityResult
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.marginLeft
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Locale
import kotlin.Exception


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
    var pickedPhoto: Uri? = null
    var pickedBitMap: Bitmap? = null
    var imageView: ImageView? = null
    var imageSrc: String? = null
    private val PICK_IMAGE_CODE = 200
    private val REQUEST_STORAGE_PERMISSION = 100
    val contentResolver = mainActivity.contentResolver
    var btnChosen = false

    @SuppressLint("ResourceAsColor")
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

        val titleInput = popUpView.findViewById<EditText>(R.id.title_input)

        val btnsValues = ArrayList<String>()
        btnsValues.add("Reading")
        btnsValues.add("Work")
        btnsValues.add("Church")
        btnsValues.add("Workout")

        try {
            val buttonsScroll = popUpView.findViewById<LinearLayout>(R.id.catBtnScroll)
            var catBtns = java.util.ArrayList<Button>()
            for (i in 0..btnsValues.size - 1) {
                val btn = Button(buttonsScroll.context)

//                val drawableImg = mainActivity.getDrawable(R.drawable.baseline_book_24)
                val drawableImg = btn.context.resources.getDrawable(R.drawable.baseline_book_24)
                drawableImg?.setBounds(5, 5, 5, 5)

                btn.setCompoundDrawables(drawableImg, null,null,null)

                btn.setText(btnsValues.get(i))
                btn.height = 40
                btn.setTextColor(R.color.black)
                btn.elevation = 8F
                btn.isSelected = false
                btn.backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)

                catBtns.add(btn)

                btn.setOnClickListener{

                    if (!btnChosen) {
                        btn.isSelected = true
                        btn.backgroundTintList = mainActivity.getColorStateList(R.color.purple)
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
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.purple)
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

        val date_picker = popUpView.findViewById<Button>(R.id.add_log_date_picker)
        date_picker.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString())
        date_picker.setOnClickListener{
            showDatePicker(date_picker)
        }

        imageView = popUpView.findViewById<ImageView>(R.id.imageView)
        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                imageView?.setImageURI(it)
//
//                val resolver = mainActivity.contentResolver
//                val imgName = System.currentTimeMillis().toString()
//                val contentValue = ContentValues()
//                contentValue.put(MediaStore.Images.Media.DISPLAY_NAME, "$imgName.jpg")
//                contentValue.put(MediaStore.Images.Media.RELATIVE_PATH, "Images/")
//                val finalUri = it?.let { it1 -> resolver.insert(it1, contentValue) }

//                imageSrc = finalUri.toString()
                imageSrc = it.toString()
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
            val text = addLog(title, startTimeBtn, endTimeBtn, description)
            if (text == "Wrong data"){
                var builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("Inputted time is wrong! Try again!")

                builder.setPositiveButton("Okay") { dialog, which ->
                }

                builder.show()
            }else{
                val entry = createJsonData(title.text.toString(), "Reading", startTimeBtn.text.toString(), endTimeBtn.text.toString(), description.text.toString(), imageSrc)

                var fileOut = mainActivity.openFileOutput("logsData.json", Context.MODE_PRIVATE)
//                fileOut.close()
//                fileOut = mainActivity.openFileOutput("logsData.json", Context.MODE_PRIVATE)

                val outputWriter = OutputStreamWriter(fileOut)
                outputWriter.write(entry.toString() + ",\n")
                Log.d("LogText", entry.toString())
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

        val outputJson = JSONObject(inputReader.readText())
        val logs = outputJson.getJSONArray("logs") as JSONArray

        file.close()

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
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format((selectedDate.time))
                datePickerBtn.setText(formattedDate)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog.show()
    }

    private fun createJsonData(title: String?, category: String?, startTime: String, endTime: String, description: String?, imgSrc: String?): JSONObject {
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
        json.put("imgSrsc", imgSrc)

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