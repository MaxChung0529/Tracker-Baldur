package com.example.csc306b

import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [Logs.newInstance] factory method to
 * create an instance of this fragment.
 */
class Logs() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity: MainActivity

    //    val mainAct = mainActivity
    val calendar = Calendar.getInstance()
    var currentlyChosenYear: Int = calendar.get(Calendar.YEAR)
    var currentlyChosenMonth: Int = calendar.get(Calendar.MONTH)
    var currentlyChosenDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    var dateToShow: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    var currentLogList = ArrayList<LogsData>()
    lateinit var contentView: View
    lateinit var sortSpinner: Spinner
    lateinit var datePickerBtn: Button

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
        contentView = inflater.inflate(R.layout.fragment_logs, container, false)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                mainActivity,
                LinearLayoutManager.HORIZONTAL
            )
        )

        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val dataList = getDateLogs()
        var adapter = LogsAdapter(dataList, mainActivity, date)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager


        val filterLogBtn = contentView.findViewById<Button>(R.id.filter)
        filterLogBtn.setOnClickListener {
            showFilterPopUp()
        }

        datePickerBtn = contentView.findViewById<Button>(R.id.date_picker_btn)

        datePickerBtn.setText(dateToShow)
        datePickerBtn.setOnClickListener {
            showDatePicker(datePickerBtn, dataList, contentView)
        }

        val searchInput = contentView.findViewById<EditText>(R.id.searchInput)
        val searchBtn = contentView.findViewById<Button>(R.id.search)
        searchBtn.setOnClickListener {
            if (searchInput.text.toString() != "") {
                searchLogs(searchInput.text.toString(), datePickerBtn)
            }
        }

        sortSpinner = contentView.findViewById<Spinner>(R.id.sortSpinner)
        val sortValArray =
            arrayOf("Starting time(ASC)", "Starting time(DESC)", "Duration(ASC)", "Duration(DESC)")

        try {
            sortSpinner.adapter =
                ArrayAdapter(
                    mainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    sortValArray
                )


            sortSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val choice = parent?.getItemAtPosition(position).toString()
                    changeLogs(contentView, null, sortValArray[position], false)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val toast = Toast.makeText(mainActivity, "LOL", Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        } catch (_: Exception) {
        }

        val addLogBtn = contentView.findViewById<FloatingActionButton>(R.id.addLog)
        addLogBtn.setOnClickListener {
            showPopUp()
        }

        return contentView
    }

    fun refresh() {
        fillRecyclerView(sortLogs("ASC", getDateLogs()), contentView)
        datePickerBtn.setText(dateToShow)
    }

    private fun getStoredLogs(): JSONArray {
        var file: File? = null
        val root = mainActivity.getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")

        val fileName = "logsData.json"
        file = File(myDir, fileName)

        if (!file.exists()) {
            file.createNewFile()
        }

        val jsonString = file.bufferedReader().use { it.readText() }

        val outputJson = JSONObject(jsonString)
        val logs = outputJson.getJSONArray("logs") as JSONArray
        return logs
    }

    private fun getDateLogs(): ArrayList<LogsData> {
        val list = ArrayList<LogsData>()

        try {
            val root = mainActivity.getExternalFilesDir(null)?.absolutePath
            var myDir = File("$root/TrackerBaldur")

            val fileName = "logsData.json"
            val file = File(myDir, fileName)

            if (!file.exists()) {
                file.createNewFile()
            }

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
                if (date == dateToShow) {
                    list.add(
                        LogsData(
                            date,
                            activityName,
                            startingTime,
                            endingTime,
                            Math.round(
                                (endingTime.replace(":", ".").toDouble()
                                        - startingTime.replace(":", ".").toDouble()) * 100.00
                            )
                                    / 100.00,
                            description,
                            imgSrc
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
        currentLogList = list
        return list
    }

    private fun changeLogs(
        contentView: View,
        formattedDate: String?,
        sortString: String?,
        filter: Boolean?
    ) {
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
        recyclerView?.adapter?.notifyDataSetChanged()

        //Change Date
        if (formattedDate != null) {
            datePickerBtn.setText(dateToShow)
            fillRecyclerView(sortLogs("ASC", getDateLogs()), contentView)
        }
        //Change Sorting Order
        if (sortString != null) {
            fillRecyclerView(sortLogs(sortString, currentLogList), contentView)
        }
        //Filter Logs
        if (filter == true) {
            sortSpinner.setSelection(0)
            fillRecyclerView(sortLogs("ASC", filterLogs(contentView)), contentView)
        }
    }

    private fun fillRecyclerView(dataList: ArrayList<LogsData>, contentView: View) {
        var adapter = LogsAdapter(dataList, mainActivity, dateToShow)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
    }

    private fun searchLogs(words: String, datePickerBtn: Button) {
        val storedLogs = getStoredLogs()
        val searchedLogsList = ArrayList<LogsData>()

        for (i in 0 until storedLogs.length()) {
            val tmpLog = storedLogs.getJSONObject(i)
            val date = tmpLog.getString("date")
            val activityName = tmpLog.getString("activityName")
            val startingTime = tmpLog.getString("startingTime")
            val endingTime = tmpLog.getString("endingTime")
            val description = tmpLog.getString("description")
            val imgSrc = tmpLog.getString("imgSrc")

            if (activityName.lowercase().contains(words.lowercase())
                || description.lowercase().contains(words.lowercase())
            ) {

                searchedLogsList.add(
                    LogsData(
                        date,
                        activityName,
                        startingTime,
                        endingTime,
                        Math.round(
                            (endingTime.replace(":", ".").toDouble()
                                    - startingTime.replace(":", ".").toDouble()) * 100.00
                        )
                                / 100.00,
                        description,
                        imgSrc
                    )
                )
            }

        }

        datePickerBtn.setText(mainActivity.getString(R.string.allDate))
        fillRecyclerView(searchedLogsList, contentView)
    }

    private fun sortLogs(criteria: String, logList: ArrayList<LogsData>): ArrayList<LogsData> {

        if (criteria == "Starting time(DESC)") {
            logList.sortByDescending { it.startingTime }
        } else if (criteria == "Duration(ASC)") {
            logList.sortBy { it.duration }
        } else if (criteria == "Duration(DESC)") {
            logList.sortByDescending { it.duration }
        } else {
            logList.sortBy { it.startingTime }
        }

        return logList
    }

    private fun filterLogs(contentView: View): ArrayList<LogsData> {

        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
        val filterPref = mainActivity.getSharedPreferences("filterPref", Context.MODE_PRIVATE)

        val dateToFiter = filterPref.getString("filterDate", "Any Date")
        datePickerBtn.setText(dateToFiter)

        val filterName = filterPref.getString("filterTitle", "")
        val filterSymbol = filterPref.getString("logicSymbol", "")
        val filterHour = filterPref.getInt("filterHours", 0)

        val editor = filterPref.edit()
        editor.clear()
        editor.apply()


        val list = ArrayList<LogsData>()

        try {

            val root = mainActivity.getExternalFilesDir(null)?.absolutePath
            var myDir = File("$root/TrackerBaldur")

            val fileName = "logsData.json"
            val file = File(myDir, fileName)

            if (!file.exists()) {
                file.createNewFile()
            }

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

                val duration = Math.round(
                    (endingTime.replace(":", ".").toDouble()
                            - startingTime.replace(":", ".").toDouble()) * 100.00
                ) / 100.00

                val recordNotMatched = (dateToFiter != "Any Date" && date != dateToFiter)
                        || (filterName != "" && filterName != activityName)
                        || (filterSymbol == ">" && duration <= filterHour)
                        || (filterSymbol == ">=" && duration < filterHour)
                        || (filterSymbol == "<" && duration >= filterHour)
                        || (filterSymbol == "<=" && duration > filterHour)
                        || (filterSymbol == "=" && duration != filterHour.toDouble())


                if (!recordNotMatched) {
                    list.add(
                        LogsData(
                            date,
                            activityName,
                            startingTime,
                            endingTime,
                            duration,
                            description,
                            imgSrc
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
        currentLogList = list
        return list


    }

    private fun showFilterPopUp() {
        val showPopUp = FilterCriteriaPopUp()
        showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }

    private fun showPopUp() {
        val showPopUp = AddLogPopUp()
        showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }

    private fun showDatePicker(
        datePickerBtn: Button,
        dataList: ArrayList<LogsData>,
        contentView: View
    ) {

        val datePickerDialog = DatePickerDialog(
            mainActivity,
            { DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)

                currentlyChosenYear = year
                currentlyChosenMonth = month
                currentlyChosenDay = day

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format((selectedDate.time))

                if (formattedDate != datePickerBtn.text.toString()) {
                    //Update logs shown
                    dataList.clear()
                    dateToShow = formattedDate
                    changeLogs(contentView, formattedDate, null, false)

                    datePickerBtn.setText(formattedDate)
                }

            },
            currentlyChosenYear,
            currentlyChosenMonth,
            currentlyChosenDay

        )
        datePickerDialog.show()
    }

    fun needToFilter() {
        changeLogs(contentView, null, null, true)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Goals.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Logs().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}