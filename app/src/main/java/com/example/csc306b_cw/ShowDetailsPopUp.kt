package com.example.csc306b_cw

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.json.JSONObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLogPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowDetailsPopUp(mainAct: MainActivity, detailsObj: JSONObject) : DialogFragment() {
    val mainActivity = mainAct
    val detailsObj = detailsObj

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val popUpView = inflater.inflate(R.layout.fragment_show_details_pop_up, container, true)

        popUpView.findViewById<TextView>(R.id.detailCatName).setText(detailsObj.getString("activityName"))
        val start = detailsObj.getString("startingTime")
        val end = detailsObj.getString("endingTime")
        val formattedTime = "$start - $end"
        popUpView.findViewById<TextView>(R.id.detailDate).setText(detailsObj.getString("date"))
        popUpView.findViewById<TextView>(R.id.detailCatTimePeriod).setText(formattedTime)
        popUpView.findViewById<TextView>(R.id.detailDescContent).setText(detailsObj.getString("description"))

        val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val startingTime = LocalTime.parse(start, dateTimeFormatter)
        val endingTime = LocalTime.parse(end, dateTimeFormatter)

        val hourDiff = endingTime.hour - startingTime.hour
        val minuteDiff = endingTime.minute - startingTime.minute

        if (hourDiff.toInt() > 1){
            popUpView.findViewById<TextView>(R.id.detailHour).setText("$hourDiff hours")
        }else {
            popUpView.findViewById<TextView>(R.id.detailHour).setText("$hourDiff hour")
        }
        if (minuteDiff.toInt() > 1){
            popUpView.findViewById<TextView>(R.id.detailMinute).setText("$minuteDiff minutes")
        }else {
            popUpView.findViewById<TextView>(R.id.detailMinute).setText("$minuteDiff minute")
        }

        try {
            if (detailsObj.getString("imgSrc") != "") {
                val detailImage = popUpView.findViewById<ImageView>(R.id.detailImage)
                detailImage.setImageURI(Uri.parse(detailsObj.getString("imgSrc")))
//                detailImage.set
            }
        }catch (e: Exception){
            Log.d("ImgSrcLOL", e.message.toString())
        }

        // Inflate the layout for this fragment
        return popUpView
    }
}