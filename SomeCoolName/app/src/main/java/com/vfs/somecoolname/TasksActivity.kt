package com.vfs.somecoolname

import android.os.Bundle
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
        
        val index = intent.getIntExtra("index", 0)
        val thisGroup = AppData.groups[index]
        
        val grpTextView = findViewById<TextView>(R.id.grpNameTextView_id)
        grpTextView.text = thisGroup.name
        
        val tasksRv = findViewById<RecyclerView>(R.id.tasksRecyclerView_id)
        tasksRv.layoutManager = LinearLayoutManager(this)
        
        taskAdapter = TasksAdapter(this, thisGroup)
        tasksRv.adapter = taskAdapter
    }
    
    override fun taskLongClicked(index: Int) {
        val builder = AlertDialog.Builder(this)
        
        builder.setTitle("Delete this task?")
        builder.setMessage("Are you sure you want to delete this task?")
        
        builder.setPositiveButton("Delete"){_, _ ->
            // AppData.groups.removeAt(index) <== CAMBIAR
            
            taskAdapter.notifyDataSetChanged()
        }
        
        builder.setNegativeButton("Cancel"){_, _ -> }
        
        builder.show()
    }
    
    override fun taskClicked(index: Int) {
        
        taskAdapter.notifyDataSetChanged()
    }
}