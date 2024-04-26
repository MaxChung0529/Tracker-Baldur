package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import java.util.ArrayList
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilterCriteriaPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterCriteriaPopUp() : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity : MainActivity
    lateinit var logFragment : Logs
    val calendar = Calendar.getInstance()
    var currentlyChosenYear : Int = calendar.get(Calendar.YEAR)
    var currentlyChosenMonth : Int = calendar.get(Calendar.MONTH)
    var currentlyChosenDay : Int = calendar.get(Calendar.DAY_OF_MONTH)
    var dateToFilter: String? = "Any Date"
    var btnChosen = false
    var chosenGreaterLessSymbol = "="
    var chosenGreaterLessNum = 1
    var chosenTitle = ""

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
        logFragment = mainActivity.logFragment

        // Inflate the layout for this fragment
        val popupView = inflater.inflate(R.layout.fragment_filter_criteria_pop_up, container, false)

        val datePickerBtn = popupView.findViewById<Button>(R.id.filter_date_picker_btn)

//        datePickerBtn.setText(dateToShow)
        datePickerBtn.setOnClickListener {
            showDatePicker(datePickerBtn)
        }

        val cancelBtn = popupView.findViewById<Button>(R.id.filter_cancel_btn)
        cancelBtn.setOnClickListener{
            dismiss()
        }

        val submitBtn = popupView.findViewById<Button>(R.id.filter_submit_btn)
        submitBtn.setOnClickListener{
            submitFilter()
        }

        val filterTitle = popupView.findViewById<EditText>(R.id.filter_title_input)

        val btnsIcons = mainActivity.getButtonIcons()


        try {
            val buttonsScroll = popupView.findViewById<LinearLayout>(R.id.filterCatBtnScroll)
            var catBtns = ArrayList<Button>()
            for (i in 0..btnsIcons.size - 1) {
                val btn = Button(buttonsScroll.context)

                val btnIcon = mainActivity.getDrawable(btnsIcons.get(i).vector)
                Log.d("DrawableID", btnIcon.toString())
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
                        btn.backgroundTintList = mainActivity.getColorStateList(R.color.purple)
                        btnChosen = true
                        filterTitle.setText(btn.text)

                    }else {
                        if (btn.isSelected) {
                            btnChosen = false
                            btn.isSelected = false
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)
                            filterTitle.setText("")
                        }else {
                            for (i in 0 until catBtns.size) {
                                catBtns.get(i).isSelected = false
                                catBtns.get(i).backgroundTintList = mainActivity.getColorStateList(R.color.light_gray)
                            }
                            btn.isSelected = true
                            btn.backgroundTintList = mainActivity.getColorStateList(R.color.purple)
                            btnChosen = true
                            filterTitle.setText(btn.text)
                        }
                    }
                    chosenTitle = btn.text.toString()
                }

                buttonsScroll.addView(btn)
            }
        }catch (e: Exception) {
            Log.d("IDK", e.message.toString())
        }

        val greaterLessSpinner = popupView.findViewById<Spinner>(R.id.greaterOrLessThanSpinner)
        val greaterLessVal = arrayOf(">", ">=", "<", "<=", "=")

        greaterLessSpinner.adapter =
            ArrayAdapter(popupView.context, android.R.layout.simple_spinner_dropdown_item, greaterLessVal)

        greaterLessSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val choice = parent?.getItemAtPosition(position).toString()
                chosenGreaterLessSymbol = choice
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        val greaterLessNumSpinner = popupView.findViewById<Spinner>(R.id.greaterOrLessThanNumSpinner)
        var greaterLessHours = arrayOf<Int>()
        for (i in 0..24) {
            greaterLessHours += i
        }


        greaterLessNumSpinner.adapter =
            ArrayAdapter(popupView.context, android.R.layout.simple_spinner_dropdown_item, greaterLessHours)

        greaterLessNumSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val choice = parent?.getItemAtPosition(position).toString()
                chosenGreaterLessNum = choice.toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        return popupView
    }

    @SuppressLint("CommitPrefEdits")
    private fun submitFilter() {
        val sharedPref = mainActivity.getSharedPreferences("filterPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("filterDate", dateToFilter)
        editor.putString("filterTitle", chosenTitle)
        editor.putString("logicSymbol", chosenGreaterLessSymbol)
        editor.putInt("filterHours", chosenGreaterLessNum)
        editor.commit()
        dismiss()
        logFragment.needToFilter()
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

                if (formattedDate != datePickerBtn.text.toString()) {
                    //Update logs shown
                    dateToFilter = formattedDate
                    datePickerBtn.setText(formattedDate)
                }

            },
            currentlyChosenYear,
            currentlyChosenMonth,
            currentlyChosenDay

        )
        datePickerDialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilterCriteriaPopUp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilterCriteriaPopUp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}