package com.example.csc306b_cw

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.csc306b_cw.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var justSwitched = false
    var logFragment = Logs(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main_view)
        setContentView(binding.root)

        replaceFragment(logFragment)

        binding.bottomNavMenu.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.logs -> replaceFragment(logFragment)
                R.id.goals -> replaceFragment(Goals(this))
                R.id.stopwatch -> replaceFragment(Stopwatch())
                R.id.overview -> replaceFragment(Overview())
                R.id.settings -> replaceFragment(Settings())

                else ->{

                }
            }
            true
        }

        if (justSwitched) {
            replaceFragment(Settings())
            justSwitchedMode()
        }
    }

    fun justSwitchedMode() {
        if (justSwitched) {
            justSwitched = false
        }else {
            justSwitched = true
        }
    }

    fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}