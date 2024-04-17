package com.example.csc306b_cw

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Logs.newInstance] factory method to
 * create an instance of this fragment.
 */
class Logs(mainActivity: MainActivity) : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val mainAct = mainActivity
    val calendar = Calendar.getInstance()

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
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_logs, container,false)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)

//        fillRecyclerView(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), contentView)
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val dataList = populateList(date)
        var adapter = LogsAdapter(dataList, mainAct)
//        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager

        val datePickerBtn = contentView.findViewById<Button>(R.id.date_picker_btn)

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        datePickerBtn.setText(today.toString())
        datePickerBtn.setOnClickListener {
            showDatePicker(datePickerBtn, dataList, contentView)
        }

        val addLogBtn = contentView.findViewById<Button>(R.id.addLog)
        addLogBtn.setOnClickListener{
            showPopUp()
        }

        return contentView
    }

    private fun fillRecyclerView(date: String, contentView: View) {
        val dataList = populateList(date)
        var adapter = LogsAdapter(dataList, mainAct)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager
    }

    private fun populateList(dateToShow: String) : ArrayList<LogsData> {
        val list = ArrayList<LogsData>()

//        val file = mainAct.openFileInput("logsData.json")
//        val inputReader = InputStreamReader(file)
//
//        val text = inputReader.readText()
//        val log = Gson().fromJson(text, LogsData::class.java)

        try {
            val jsonString = mainAct.assets.open("test.json").bufferedReader().use { it.readText() }

            val outputJson = JSONObject(jsonString)
            val logs = outputJson.getJSONArray("logs") as JSONArray

            for (i in 0 until logs.length()){
                val date = logs.getJSONObject(i).getString("date")
                val activityName = logs.getJSONObject(i).getString("activityName")
                val startingTime = logs.getJSONObject(i).getString("startingTime")
                val endingTime = logs.getJSONObject(i).getString("endingTime")
                val description = logs.getJSONObject(i).getString("description")
                if (date == dateToShow) {
                    list.add(
                        LogsData(
                            date,
                            activityName,
                            "",
                            startingTime,
                            endingTime,
                            description
                        )
                    )
                }
            }


        }catch (e: Exception) {
            Log.d("LOL",e.message.toString())
        }

//        file.close()

        return list
    }

    private fun showPopUp(){

        val showPopUp = AddLogPopUp(mainAct)
        showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }

    private fun showDatePicker(datePickerBtn: Button, dataList: ArrayList<LogsData>, contentView: View) {
        val datePickerDialog = DatePickerDialog(mainAct,
            {DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format((selectedDate.time))

                if (formattedDate != datePickerBtn.text.toString()) {
                    //Update logs shown
                    dataList.clear()
                    val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)
                    recyclerView?.adapter?.notifyDataSetChanged()
                    fillRecyclerView(formattedDate, contentView)

                    datePickerBtn.setText(formattedDate)
                }

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog.show()
        }
    }