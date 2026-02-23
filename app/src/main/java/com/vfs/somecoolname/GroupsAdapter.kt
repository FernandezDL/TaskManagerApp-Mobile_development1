package com.vfs.somecoolname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsViewHolder(rootView: LinearLayout) : RecyclerView.ViewHolder(rootView) {
    val groupedNameTextView: TextView = rootView.findViewById(R.id.groupNameTextView_id)
    val groupCountTextView: TextView = rootView.findViewById(R.id.groupCountTextView_id)
    val dividerView: View = rootView.findViewById(R.id.dividerView_id)

    fun bind(group: Group, hideDivider: Boolean) {
        groupedNameTextView.text = group.name
        dividerView.visibility = if (hideDivider) View.GONE else View.VISIBLE
    }
}

class GroupsAdapter(
    private val listener: GroupListener,
    private val groupKeys: List<String>
) : RecyclerView.Adapter<GroupsViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_row, parent, false) as LinearLayout
        return GroupsViewHolder(root)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val thisGroup = AppData.groups[position]
        holder.bind(thisGroup, position == AppData.groups.count() - 1)

        val uid = auth.currentUser?.uid
        val groupId = groupKeys.getOrNull(position)

        holder.groupCountTextView.text = "… Active tasks"

        if (uid != null && groupId != null) {
            holder.itemView.tag = groupId

            db.child("tasks").child(uid)
                .orderByChild("groupId")
                .equalTo(groupId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var activeCount = 0
                        for (taskSnap in snapshot.children) {
                            val done = taskSnap.child("done").getValue(Boolean::class.java) ?: false
                            if (!done) activeCount++
                        }

                        if (holder.itemView.tag == groupId) {
                            holder.groupCountTextView.text = "$activeCount Active tasks"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (holder.itemView.tag == groupId) {
                            holder.groupCountTextView.text = "0 Active tasks"
                        }
                    }
                })
        } else {
            holder.groupCountTextView.text = "0 Active tasks"
        }

        holder.itemView.setOnLongClickListener {
            listener.groupLongClicked(holder.bindingAdapterPosition)
            true
        }

        holder.itemView.setOnClickListener {
            listener.groupClicked(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = AppData.groups.count()
}

interface GroupListener {
    fun groupLongClicked(index: Int)
    fun groupClicked(index: Int)
}