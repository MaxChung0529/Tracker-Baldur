package com.example.csc306b_cw

import android.app.Activity
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES

        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main_view)
        setContentView(binding.root)

        replaceFragment(Logs(this))

        binding.bottomNavMenu.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.logs -> replaceFragment(Logs(this))
                R.id.goals -> replaceFragment(Goals(this))
                R.id.stopwatch -> replaceFragment(Stopwatch())
                R.id.settings -> replaceFragment(Settings())

                else ->{

                }
            }
            true
        }

        if (isDarkModeOn) {
            replaceFragment(Settings())
        }
    }

    public fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}