package com.vfs.taskmanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        var rv = findViewById<RecyclerView>(R.id.recyclerView_id)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = Adapter(arrayOf(
            Task("Task 1", "Description 1", false),
            Task("Task 2", "Description 2", false),
            Task("Task 3", "Description 3", false),
            Task("Task 4", "Description 4", false)
        ))
        
        val addTaskBttn = findViewById<Button>(R.id.addTaskBttn_id)
        addTaskBttn.setOnClickListener {
            val intent = Intent(this, CreateTask::class.java)
            startActivity(intent)
        }
    }

    class ViewHolder (val rootView: CardView) : RecyclerView.ViewHolder(rootView)

    class Adapter (val data: Array<Task>) : RecyclerView.Adapter<ViewHolder>()
    {
        override fun getItemCount(): Int = data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val thatTextView = LayoutInflater.from(parent.context)
                .inflate(R.layout.individual_task,
                    parent,
                    false) as CardView

            return ViewHolder(thatTextView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val task = data[position]

            val taskTitleTextView = holder.rootView.findViewById<TextView>(R.id.taskTitle_id)
            val taskDescriptionTextView = holder.rootView.findViewById<TextView>(R.id.taskDescription_id)
            val completedRadioButton = holder.rootView.findViewById<RadioButton>(R.id.completedRadioButton_id)

            // Asignar los datos a cada vista
            taskTitleTextView.text = task.title
            taskDescriptionTextView.text = task.description
            completedRadioButton.isChecked = task.completed
        }
    }

    data class Task(val title: String, val description: String, val completed: Boolean)
}