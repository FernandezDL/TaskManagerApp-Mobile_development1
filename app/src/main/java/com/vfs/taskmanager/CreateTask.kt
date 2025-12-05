package com.vfs.taskmanager

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_task)

        val backBttn = findViewById<Button>(R.id.backBttn_id)
        val acceptBttn = findViewById<Button>(R.id.acceptBttn_id)

        backBttn.setOnClickListener {
            finish()
        }
        
        acceptBttn.setOnClickListener {
            finish()
        }
    }
}