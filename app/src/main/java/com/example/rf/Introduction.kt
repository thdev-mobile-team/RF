package com.example.rf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class Introduction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introduction)

        val buttonPrevious = findViewById<Button>(R.id.buttonPrevious)
        val buttonFinish = findViewById<Button>(R.id.buttonFinish)

        buttonPrevious.setOnClickListener {
            onBackPressed()
        }

        buttonFinish.setOnClickListener {
            val intent = Intent(this@Introduction, Login::class.java)
            startActivity(intent)
        }
    }
}
