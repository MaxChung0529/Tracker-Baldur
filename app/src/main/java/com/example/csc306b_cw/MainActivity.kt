package com.example.csc306b_cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val baldurGreet = findViewById<TextView>(R.id.greetTexts)

        baldurGreet.text = getText(R.string.askName)
    }

    fun updateText(view : View) {
        val baldurGreet = findViewById<TextView>(R.id.greetTexts)
        val enteredName = findViewById<EditText>(R.id.nameInput)

        baldurGreet.text = getString(R.string.reply1, enteredName.text)

        baldurGreet.textSize = 20.0F
        enteredName.visibility = View.INVISIBLE

        val updateBtn = findViewById<Button>(R.id.update)
        updateBtn.visibility = View.INVISIBLE


    }
}