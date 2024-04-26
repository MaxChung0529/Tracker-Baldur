package com.example.csc306b_cw

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import java.text.MessageFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.math.min

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Stopwatch.newInstance] factory method to
 * create an instance of this fragment.
 */
class Stopwatch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity: MainActivity
    lateinit var stopwatchView: View
    lateinit var time : TextView
    lateinit var timer : CountDownTimer
    var currentTimeLeft = 0L
    var timeToCount = 0L
    var hourSet = false
    var minuteset = false
    private lateinit var sharedPref : SharedPreferences

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

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

        mainActivity = context as MainActivity

        // Inflate the layout for this fragment
        stopwatchView = inflater.inflate(R.layout.fragment_stopwatch, container, false)
        time = stopwatchView.findViewById<TextView>(R.id.timer)

        val startTimer = stopwatchView.findViewById<Button>(R.id.startTimer)
        startTimer.isEnabled = false

        val hourInput = stopwatchView.findViewById<EditText>(R.id.timerHourInput)
        val minuteInput = stopwatchView.findViewById<EditText>(R.id.timerMinuteInput)

        sharedPref = mainActivity.getSharedPreferences("TimerPref", Context.MODE_PRIVATE)

        hourInput.addTextChangedListener{
            hourSet = true

            if (minuteset) {
                checkInput(hourInput, minuteInput, startTimer)
            }
        }

        minuteInput.addTextChangedListener {
            minuteset = true

            if (hourSet) {
                checkInput(hourInput, minuteInput, startTimer)
            }
        }

        startTimer.setOnClickListener{
            startTimer.isEnabled = false

            if (startTimer.text == "Start") {
                timer.start()

                val editor = sharedPref.edit()
                editor.putString("Starting Time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                editor.commit()

            }else {
                startTimer.setText(getString(R.string.startTimer))
                makeTimer(currentTimeLeft)
                timer.start()
            }
        }

        val pauseTimer = stopwatchView.findViewById<Button>(R.id.pauseTimer)
        pauseTimer.setOnClickListener{
            pauseTimer()
            startTimer.setText(getString(R.string.resumetimer))
            startTimer.isEnabled = true
        }

        val resetTimer = stopwatchView.findViewById<Button>(R.id.resetTimer)
        resetTimer.setOnClickListener{
            timer.cancel()
            timeToCount = 0L
            currentTimeLeft = 0L

            startTimer.isEnabled = true
            startTimer.setText(getString(R.string.startTimer))

            time.setText("00:00:00")
        }

        return stopwatchView
    }

    private fun checkInput(hourInput: EditText, minuteInput: EditText, startTimer: Button) {
        if (hourInput.text.toString() != "" && minuteInput.text.toString() != "") {

            if (hourInput.text.toString().toInt() <= 24
                && minuteInput.text.toString().toInt() <= 59
                && !hourInput.text.toString().contains(":")
                && !minuteInput.text.toString().contains(":")
            ) {

                startTimer.isEnabled = true

                //Add hours in seconds
                timeToCount += hourInput.text.toString().toInt() * 3600

                //Add minutes in seconds
                timeToCount += minuteInput.text.toString().toInt() * 60

                //Convert to milliseconds
                timeToCount *= 1000
                makeTimer(timeToCount)
            }
        }
    }

    private fun makeTimer(timeCount: Long) {

        timer = object : CountDownTimer(timeCount, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                currentTimeLeft = millisUntilFinished

                val sec = millisUntilFinished / 1000

                val hour = sec / 3600
                val minute = (sec % 3600) / 60
                var second = (sec % 3600 % 60)

                time.setText(String.format("%02d:%02d:%02d", hour, minute, second))
            }

            override fun onFinish() {
                time.setText("Done!")

                val editor = sharedPref.edit()
                editor.putString("Ending Time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
                editor.commit()

                showPopUp()
            }
        }
    }

    private fun pauseTimer() {
        timer.cancel()
        val sec = currentTimeLeft / 1000

        val hour = sec / 3600
        val minute = (sec % 3600) / 60
        var second = (sec % 3600 % 60)

        time.setText(String.format("%02d:%02d:%02d", hour, minute, second))
    }

    private fun showPopUp() {
        val showPopUp = AddLogPopUp()
        showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StopwatchT.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Stopwatch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}