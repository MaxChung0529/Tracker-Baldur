package com.example.csc306b

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.ArrayList
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddGoalPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddGoalPopUp() : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var imageView: ImageView? = null
    var imageSrc: String? = null
    var btnChosen = false
    var bitMapUri: Uri? = null
    lateinit var mainActivity : MainActivity
    val calendar = Calendar.getInstance()
    var chosenInterval: Int = 1
    var chosenIntervalUnit: String? = "Day(s)"
    var chosenHour: Int = 1
    var chosenMinute: Double = 0.00
    var chosenDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    var totalHrs = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = context as MainActivity
        // Inflate the layout for this fragment
        val popupView = inflater.inflate(R.layout.fragment_add_goal_pop_up, container, false)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES
        if (isDarkModeOn) {
            popupView.findViewById<ImageView>(R.id.goal_image).setImageDrawable(
                mainActivity.getDrawable(R.drawable.backgroundstuffdark))
        }else {
            popupView.findViewById<ImageView>(R.id.goal_image).setImageDrawable(
                mainActivity.getDrawable(R.drawable.backgroundstuff))
        }

        val goalTitle = popupView.findViewById<TextView>(R.id.goal_title_input)
        val totalHours = popupView.findViewById<TextView>(R.id.goal_total_hours)
        val goalDescription = popupView.findViewById<EditText>(R.id.goal_description)

        val btnsIcons = mainActivity.getButtonIcons()

        //Populate the list of category buttons
        try {
            val buttonsScroll = popupView.findViewById<LinearLayout>(R.id.goalCatBtnScroll)
            var catBtns = ArrayList<Button>()
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
                        goalTitle.setText(btn.text)

                    }else {
                        if (btn.isSelected) {
                            btnChosen = false
                            btn.isSelected = false
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)
                            goalTitle.setText("")
                        }else {
                            for (i in 0 until catBtns.size) {
                                catBtns.get(i).isSelected = false
                                catBtns.get(i).backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)
                            }
                            btn.isSelected = true
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.gray)
                            btnChosen = true
                            goalTitle.setText(btn.text)
                        }
                    }
                }

                buttonsScroll.addView(btn)
            }
        }catch (_: Exception) {
        }


        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                bitMapUri = it
                imageView?.setImageURI(bitMapUri)
            })

        imageView = popupView.findViewById<ImageView>(R.id.goal_image)
        val uploadImgBtn = popupView.findViewById<Button>(R.id.goal_upload_image)
        uploadImgBtn.setOnClickListener{
            browseImage(galleryImage)
        }


        val deadLineDatePicker = popupView.findViewById<Button>(R.id.deadline_picker)
        deadLineDatePicker.setOnClickListener{
            showDatePicker(deadLineDatePicker, totalHours)
        }

        val intervalSpinner = popupView.findViewById<Spinner>(R.id.intervalSpinner)
        val intervalValues = arrayOf("1","2","3","4","5","6")

        try {
            intervalSpinner.adapter =
                ArrayAdapter(popupView.context, android.R.layout.simple_spinner_dropdown_item, intervalValues)


            intervalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val choice = parent?.getItemAtPosition(position).toString()
                    chosenInterval = choice.toInt()
                    updateTotalHours(totalHours)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }catch (_: Exception) {
        }


        val intervalUnitSpinner = popupView.findViewById<Spinner>(R.id.intervalUnitSpinner)
        val intervalUnits = arrayOf("Day(s)", "Week(s)", "Month(s)")

        try {
            intervalUnitSpinner.adapter =
                ArrayAdapter(popupView.context, android.R.layout.simple_spinner_dropdown_item, intervalUnits)

            intervalUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val choice = parent?.getItemAtPosition(position).toString()
                    chosenIntervalUnit = choice.toString()
                    updateTotalHours(totalHours)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }catch (_: Exception) {
        }

        val intervalLogDurationSpinner = popupView.findViewById<Spinner>(R.id.intervalDurationHourSpinner)
        var intervalDurationHours = arrayOf<Int>()
        for (i in 1..24) {
            intervalDurationHours += i
        }

        try {
            intervalLogDurationSpinner.adapter =
                ArrayAdapter(popupView.context, android.R.layout.simple_spinner_dropdown_item, intervalDurationHours)

            intervalLogDurationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val choice = parent?.getItemAtPosition(position).toString()
                    chosenHour = choice.toInt()
                    updateTotalHours(totalHours)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }catch (_: Exception) {
        }

        val logDurationMinuteSpinner = popupView.findViewById<Spinner>(R.id.intervalDurationMinuteSpinner)
        var intervalDurationMinute = arrayOf<Int>()
        for (i in 0..3) {
            intervalDurationMinute += (i * 15)
        }

        try {
            logDurationMinuteSpinner.adapter =
                ArrayAdapter(popupView.context, android.R.layout.simple_spinner_dropdown_item, intervalDurationMinute)

            logDurationMinuteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val choice = parent?.getItemAtPosition(position).toString()
                    chosenMinute = choice.toDouble() / 100.00
                    updateTotalHours(totalHours)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }catch (_: Exception) {
        }

        val submitBtn = popupView.findViewById<Button>(R.id.goal_submit_btn)
        submitBtn.setOnClickListener{
            if (goalTitle.text.toString() != ""
                    && goalTitle.text != null
                    && totalHrs > 0) {
                Toast.makeText(mainActivity, "Goal added!", Toast.LENGTH_LONG).show()

                try {
                    imageSrc = saveImage(
                        MediaStore.Images.Media.getBitmap(
                            mainActivity.contentResolver,
                            bitMapUri
                        )
                    )
                }catch (e: Exception) {
                    imageSrc = ""
                }

                submitGoals(goalTitle.text.toString(), chosenInterval.toString(), chosenIntervalUnit
                    , chosenHour.toDouble() + chosenMinute, 0.0
                    , totalHrs, chosenDate, goalDescription.text.toString(), imageSrc)

                dismiss()
                mainActivity.goalFragment.refresh()
            }else if (goalTitle.text == null) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("Title cannot be empty! You can press on the buttons fill it in!")
                builder.setPositiveButton("Okay") { dialog, which ->
                }
                builder.show()
            }else if (totalHrs <= 0) {
                val builder = AlertDialog.Builder(mainActivity)
                builder.setTitle("Total hours must be more than 0! Please choose the deadline again! ")
                builder.setPositiveButton("Okay") { dialog, which ->
                }
                builder.show()
            }
        }

        val cancelBtn = popupView.findViewById<Button>(R.id.goal_cancel_btn)
        cancelBtn.setOnClickListener{
            dismiss()
        }

        return popupView
    }

    private fun updateTotalHours(totalView: TextView) {

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).split("-")
        val deadline = chosenDate.split("-")

        val fromDate = LocalDate.of(today[2].toInt(), today[1].toInt(), today[0].toInt())
        val toDate = LocalDate.of(deadline[2].toInt(), deadline[1].toInt(), deadline[0].toInt())

        val dayDifference = ChronoUnit.DAYS.between(fromDate, toDate)

        var days = chosenInterval
        if (days != null) {
                days *= when (chosenIntervalUnit) {

                "Day(s)" -> 1
                "Week(s)" -> 7
                "Month(s)" -> ChronoUnit.DAYS.between(fromDate, fromDate.plusMonths(days.toLong())).toInt()
                    else -> {1}
                }
        }

        val hourDifference = (dayDifference / days)
        val eachLogDuration = chosenHour + (chosenMinute / 0.6)

        totalHrs = eachLogDuration * hourDifference
        totalView.setText("Total hour(s): ${totalHrs}")
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker(datePickerBtn: Button, totalHours: TextView) {
        val datePickerDialog = DatePickerDialog(mainActivity,
            {DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format((selectedDate.time))
                datePickerBtn.setText("Deadline: $formattedDate")
                chosenDate = formattedDate
                updateTotalHours(totalHours)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog.show()
    }

    private fun browseImage(galleryImage: ActivityResultLauncher<String>) {
        try {
            galleryImage.launch("image/*")
        }catch (_: Exception) {
        }
    }

    private fun saveImage(bitmap: Bitmap): String{
        var fileOutputStream : OutputStream
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur/Images")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val fileName = "Images-" + System.currentTimeMillis() + ".png"
        file = File(myDir, fileName)

        if (!file.exists()) {
            file.createNewFile()
        }
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

    private fun createJsonData(goalName: String?, interval: String?, unit: String?, duration: Double?
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


    private fun getStoredGoals(): JSONArray {
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "goalsData.json"
        file = File(myDir, fileName)

        if (!file.exists()) {
            file.createNewFile()
        }

        try {
            val jsonString = file.bufferedReader().use { it.readText() }

            val outputJson = JSONObject(jsonString)
            val goals = outputJson.getJSONArray("goals") as JSONArray
            return goals
        }catch (e: Exception) {
            file.createNewFile()
            val goals = JSONArray()
            return goals
        }
    }
    private fun submitGoals(goalName: String?, interval: String?, unit: String?, duration: Double?
                          , progressNow: Double?, progressGoal: Double?, deadline: String?
                          , description: String?, imgSrc: String?) {

        val storedGoals = getStoredGoals()

        val entry = createJsonData(goalName, interval
            , unit, duration, progressNow, progressGoal, deadline
            , description, imageSrc)


        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        val myDir = File("$root/TrackerBaldur")

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val tmpJSONArray = JSONArray()

        if (storedGoals.length() > 0) {
            for (i in 0 until storedGoals.length()) {
                tmpJSONArray.put(storedGoals.getJSONObject(i))
            }
        }

        tmpJSONArray.put(entry)

        val goalsArray = JSONObject()

        goalsArray.put("goals",tmpJSONArray)

        val fileName = "goalsData.json"
        file = File(myDir, fileName)

        if (!file.exists()) {
            file.createNewFile()
        }
        try {
            val output = BufferedWriter(FileWriter(file))
            output.write(goalsArray.toString())
            output.close()
        }catch (_: Exception) {
        }
        dismiss()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddGoalPopUp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddGoalPopUp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}