package com.example.csc306b_cw

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main_view)
        setContentView(binding.root)

        replaceFragment(Logs())



        binding.bottomNavMenu.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.logs -> replaceFragment(Logs())
                R.id.goals -> replaceFragment(Goals())
                R.id.stopwatch -> replaceFragment(Stopwatch())
                R.id.settings -> replaceFragment(Settings())

                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}