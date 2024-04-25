package com.example.csc306b_cw

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.csc306b_cw.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavBar: BottomNavigationView
    var justSwitched = false
    var logFragment = Logs()
    var goalFragment = Goals()
    var overviewFragment = Overview()
    var stopwatchFragment = Stopwatch()
    var settingFragment = Settings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDefaultColours()

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(logFragment)

        binding.bottomNavMenu.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.logs -> replaceFragment(logFragment)
                R.id.goals -> replaceFragment(goalFragment)
                R.id.stopwatch -> replaceFragment(stopwatchFragment)
                R.id.overview -> replaceFragment(overviewFragment)
                R.id.settings -> replaceFragment(settingFragment)

                else ->{
                }
            }
            true
        }

        val sharedPreferences = getSharedPreferences("DarkModePref", Context.MODE_PRIVATE)
        justSwitched = sharedPreferences.getBoolean("justSwitched", false)

        if (justSwitched) {
            replaceFragment(settingFragment)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.putBoolean("justSwitched", false)
            editor.commit()
        }
    }

    fun replaceFragment(fragment : Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commitAllowingStateLoss()
        }
    }

    fun getDefaultColours() {
        var file: File? = null
        val root = getExternalFilesDir(null)?.absolutePath
        var myDir = File("$root/TrackerBaldur")
        val fileName = "colours.json"
        file = File(myDir, fileName)

        if (!file.exists()) {

            val coloursFile = "catColors.json"
            val jsonString = application.assets.open(coloursFile).bufferedReader().use {
                it.readText()
            }

            val outputJson = JSONObject(jsonString)
            val colours = outputJson.getJSONArray("colours") as JSONArray


            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            var tmpJSONArray = JSONArray()

            if (colours.length() > 0) {
                for (i in 0 until colours.length()) {
                    tmpJSONArray.put(colours.getJSONObject(i))
                }
            }

            val coloursArray = JSONObject()

            coloursArray.put("colours", tmpJSONArray)

            try {
                val output = BufferedWriter(FileWriter(file))
                output.write(coloursArray.toString())
                output.close()
            } catch (e: Exception) {
                Log.d("logs-saving", e.message.toString())
            }
        }
    }
}