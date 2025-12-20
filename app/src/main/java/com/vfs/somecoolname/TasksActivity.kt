package com.vfs.somecoolname

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TasksActivity : AppCompatActivity(), TasksListener {
    lateinit var taskAdapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.taks_layout)
        
        val index = intent.getIntExtra("index", -1)

        if (index == -1 || index >= AppData.groups.size) {
            finish()
            return
        }

        val thisGroup = AppData.groups[index]
        
        val grpTextView = findViewById<TextView>(R.id.grpNameTextView_id)
        grpTextView.text = thisGroup.name
        
        val tasksRv = findViewById<RecyclerView>(R.id.tasksRecyclerView_id)
        tasksRv.layoutManager = LinearLayoutManager(this)
        
        taskAdapter = TasksAdapter(this, thisGroup)
        tasksRv.adapter = taskAdapter

        val backBttn = findViewById<Button>(R.id.backBttn_id)
        backBttn.setOnClickListener {
            finish()
        }

        val newTaskBttn = findViewById<Button>(R.id.newTaskBttn_id)
        newTaskBttn.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("New task")
            builder.setMessage("Enter the name of the new task:")

            val input = EditText(this)
            builder.setView(input)

            builder.setPositiveButton("Add"){_, _ ->
                if(input.text.toString() == "") return@setPositiveButton

                val newTask = Task(input.text.toString(), false)
                val pos = taskAdapter.group.tasks.size
                taskAdapter.group.tasks.add(newTask)
                taskAdapter.notifyItemInserted(pos)
            }

            builder.setNegativeButton("Cancel"){_, _ -> }

            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        taskAdapter.notifyDataSetChanged()
    }

    override fun taskLongClicked(index: Int) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Edit task")
        builder.setMessage("Enter the new name for the task:")

        val input = EditText(this)
        val task = taskAdapter.group.tasks[index]

        input.setText(task.name)
        input.setSelection(task.name.length)

        builder.setView(input)

        builder.setPositiveButton("Edit") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isEmpty()) return@setPositiveButton

            task.name = newName
            taskAdapter.notifyItemChanged(index)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        builder.setNeutralButton("Delete") { _, _ ->
            taskAdapter.group.tasks.removeAt(index)
            taskAdapter.notifyItemRemoved(index)
        }

        builder.show()
    }

    override fun taskClicked(index: Int) {
        if(taskAdapter.group.tasks.count() == 0) return

        if(index >= 0 && index < taskAdapter.group.tasks.count()){
            val task = taskAdapter.group.tasks[index]
            task.completed = !task.completed
            taskAdapter.notifyDataSetChanged()
        }
    }
}
