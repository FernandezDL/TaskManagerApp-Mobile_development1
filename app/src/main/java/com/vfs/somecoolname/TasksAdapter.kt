package com.vfs.somecoolname

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksViewHolder(rootView: LinearLayout) : RecyclerView.ViewHolder(rootView) {
    val taskNameTextView: TextView = rootView.findViewById(R.id.taskTextView_id)
    val taskCompletedCheckbox: CheckBox = rootView.findViewById(R.id.taskCheckBox_id)
    val taskDividerView: View = rootView.findViewById(R.id.dividerView_id2)

    fun bind(task: Task, hideDivider: Boolean) {
        taskNameTextView.text = task.name

        taskCompletedCheckbox.isChecked = task.completed

        if (task.completed) {
            taskNameTextView.paintFlags =
                taskNameTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            itemView.setBackgroundColor(Color.parseColor("#E6E6E6"))
        } else {
            taskNameTextView.paintFlags =
                taskNameTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        taskDividerView.visibility = if (hideDivider) View.GONE else View.VISIBLE
    }
}

class TasksAdapter(private val listener: TasksListener, private val group: Group) :
    RecyclerView.Adapter<TasksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val rootLinearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_row, parent, false) as LinearLayout
        return TasksViewHolder(rootLinearLayout)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = group.tasks[position]
        holder.bind(task, position == group.tasks.count() - 1)

        holder.itemView.setOnLongClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) listener.taskLongClicked(pos)
            true
        }

        holder.taskCompletedCheckbox.setOnCheckedChangeListener(null)

        holder.taskCompletedCheckbox.setOnCheckedChangeListener { _, _ ->
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener.taskClicked(pos)
            }
        }

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            holder.taskCompletedCheckbox.isChecked = !holder.taskCompletedCheckbox.isChecked
        }
    }

    override fun getItemCount(): Int = group.tasks.count()
}

interface TasksListener {
    fun taskLongClicked(index: Int)
    fun taskClicked(index: Int)
}