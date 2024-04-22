package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShowGoalPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowGoalPopUp(mainAct: MainActivity, detailsObj: JSONObject) : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val mainActivity = mainAct
    val detailsObj = detailsObj

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
        val popupView = inflater.inflate(R.layout.fragment_show_goal_pop_up, container, false)

        val goalName = detailsObj.getString("goalName")
        popupView.findViewById<TextView>(R.id.detail_goal_title).setText(goalName)

        val catBar = popupView.findViewById<Button>(R.id.catColor)
        catBar.setBackgroundColor(mainActivity.getColor(findColour(goalName)))

        val now = detailsObj.getString("progressNow")
        val goal = detailsObj.getString("progressGoal")
        val formattedProgess = "$now/$goal Hours"

        popupView.findViewById<TextView>(R.id.detailGoalActualProgress).setText(formattedProgess)

        popupView.findViewById<TextView>(R.id.goal_deadline).setText(detailsObj.getString("deadline"))
        popupView.findViewById<TextView>(R.id.goalDetailDescContent).setText(detailsObj.getString("description"))


//        val startForResult = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult(),
//            ActivityResultCallback {
//                if (it.resultCode == Activity.RESULT_OK) {
//                    mainActivity.logFragment.refresh()
//                    dismiss()
//                }
//            })


//        val editLogBtn = popupView.findViewById<Button>(R.id.editBtn)
//        editLogBtn.setOnClickListener{
//            //Implicit Intent?
//            val editLogIntent = Intent(mainActivity, EditLogActivity::class.java)
//            editLogIntent.putExtra("date", detailsObj.getString("date"))
//            editLogIntent.putExtra("activityName", detailsObj.getString("activityName"))
//            editLogIntent.putExtra("startingTime", detailsObj.getString("startingTime"))
//            editLogIntent.putExtra("endingTime", detailsObj.getString("endingTime"))
//            editLogIntent.putExtra("description", detailsObj.getString("description"))
//            editLogIntent.putExtra("imgSrc",detailsObj.getString("imgSrc"))
//
////            startActivity(editLogIntent)
////            startForResult.launch(editLogIntent)
////            dismiss()
//        }

//        val deleteLogBtn = popUpView.findViewById<Button>(R.id.deleteBtn)
//        deleteLogBtn.setOnClickListener{
//            confirmDelete()
//        }

//        val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//        val startingTime = LocalTime.parse(start, dateTimeFormatter)
//        val endingTime = LocalTime.parse(end, dateTimeFormatter)
//
//        val hourDiff = endingTime.hour - startingTime.hour
//        val minuteDiff = endingTime.minute - startingTime.minute
//
//        if (hourDiff.toInt() > 1){
//            popUpView.findViewById<TextView>(R.id.detailHour).setText("$hourDiff hours")
//        }else {
//            popUpView.findViewById<TextView>(R.id.detailHour).setText("$hourDiff hour")
//        }
//        if (minuteDiff.toInt() > 1){
//            popUpView.findViewById<TextView>(R.id.detailMinute).setText("$minuteDiff minutes")
//        }else {
//            popUpView.findViewById<TextView>(R.id.detailMinute).setText("$minuteDiff minute")
//        }

        try {
            if (detailsObj.getString("imgSrc") != "") {
                val detailImage = popupView.findViewById<ImageView>(R.id.goalDetailImage)
                detailImage.setImageURI(Uri.parse(detailsObj.getString("imgSrc")))
            }
        }catch (e: Exception){
            Log.d("ImgSrcLOL", e.message.toString())
        }

        return popupView
    }

    @SuppressLint("DiscouragedApi")
    fun findColour(name: String?): Int{
        val coloursJSONString = mainActivity.assets.open("catColors.json").bufferedReader().use {
            it.readText()
        }

        val outputJson = JSONObject(coloursJSONString)
        val colours = outputJson.getJSONArray("colours") as JSONArray

        for (i in 0 until colours.length()) {
            if (name == colours.getJSONObject(i).getString("Name")) {
                val colorName = colours.getJSONObject(i).getString("Colour")

                val res = mainActivity.getResources()
                val packageName: String = mainActivity.getPackageName()

                val colorId = res.getIdentifier(colorName, "color", packageName)
                return colorId
            }
        }
        return -1
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ShowGoalPopUp.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ShowGoalPopUp().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}