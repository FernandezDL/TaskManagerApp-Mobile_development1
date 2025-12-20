package com.vfs.somecoolname

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksViewHolder (rootView: LinearLayout) : RecyclerView.ViewHolder(rootView) {
    lateinit var taskNameTextView: TextView
    lateinit var taskCompletedCheckbox: CheckBox
    lateinit var taskDividerView: View
    
    init {
        taskNameTextView = rootView.findViewById<TextView>(R.id.taskTextView_id)
        taskCompletedCheckbox = rootView.findViewById<CheckBox>(R.id.taskCheckBox_id)
        taskDividerView = rootView.findViewById(R.id.dividerView_id2)
    }
    
    fun bind(task: Task, hideDivider: Boolean) {
        taskNameTextView.text = task.name
        taskCompletedCheckbox.isChecked = task.completed
        
        if(task.completed){
            taskNameTextView.paintFlags = taskNameTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            itemView.setBackgroundColor(Color.parseColor("#E6E6E6"))
        } else{
            taskNameTextView.paintFlags = taskNameTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        
        taskDividerView.visibility = View.VISIBLE
        
        if(hideDivider) taskDividerView.visibility = View.GONE
    }
}

class TasksAdapter (val listener: TasksListener, val group: Group) : RecyclerView.Adapter<TasksViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val rootLinearLayout = LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false) as LinearLayout
        
        return TasksViewHolder(rootLinearLayout)
    }
    
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val thisGroup = group.tasks[position]
        
        holder.bind(thisGroup, position == group.tasks.count() - 1)
        
        holder.itemView.setOnLongClickListener {
            listener.taskLongClicked(position)
            true
        }
        
        holder.itemView.setOnClickListener {
            listener.taskClicked(position)
        }
    }

    override fun getItemCount(): Int = group.tasks.count()
}

interface TasksListener{
    fun taskLongClicked(index: Int)
    fun taskClicked(index: Int)
}