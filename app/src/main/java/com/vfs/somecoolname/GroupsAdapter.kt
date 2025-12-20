package com.vfs.somecoolname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupsViewHolder (rootView: LinearLayout) : RecyclerView.ViewHolder(rootView) {
    lateinit var groupedNameTextView: TextView
    lateinit var groupCountTextView: TextView
    lateinit var dividerView: View
    
    init {
        groupCountTextView = rootView.findViewById(R.id.groupCountTextView_id)
        groupedNameTextView = rootView.findViewById(R.id.groupNameTextView_id)
        dividerView = rootView.findViewById(R.id.dividerView_id)
    }
    
    fun bind(group: Group, hideDivider: Boolean) {
        groupedNameTextView.text = group.name
        groupCountTextView.text = "${group.tasks.count()} Active tasks"
        
        dividerView.visibility = View.VISIBLE
        
        if(hideDivider) dividerView.visibility = View.GONE
    }
}

class GroupsAdapter (val listener: GroupListener) : RecyclerView.Adapter<GroupsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val rootLinearLayout = LayoutInflater.from(parent.context).inflate(R.layout.group_row, parent, false) as LinearLayout
        
        return GroupsViewHolder(rootLinearLayout)
    }
    
    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val thisGroup = AppData.groups[position]
        
        holder.bind(thisGroup, position == AppData.groups.count() - 1)
        
        holder.itemView.setOnLongClickListener {
            listener.groupLongClicked(position)
            true
        }
        
        holder.itemView.setOnClickListener {
            listener.groupClicked(position)
        }
    }
    
    override fun getItemCount(): Int = AppData.groups.count()
}

interface GroupListener{
    fun groupLongClicked(index: Int)
    fun groupClicked(index: Int)
}