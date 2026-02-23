package com.vfs.somecoolname

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsActivity : AppCompatActivity(), GroupListener {

    lateinit var groupAdapter: GroupsAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var groupsListener: ValueEventListener? = null

    private val groupKeys: MutableList<String> = mutableListOf()

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        attachGroupsListener()
    }

    override fun onStop() {
        super.onStop()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null && groupsListener != null) {
            db.child("groups").child(uid).removeEventListener(groupsListener!!)
        }
        groupsListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.groups_layout)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        val groupsRv = findViewById<RecyclerView>(R.id.groupsRecyclerView_id)
        groupsRv.layoutManager = LinearLayoutManager(this)

        groupAdapter = GroupsAdapter(this, groupKeys)
        groupsRv.adapter = groupAdapter

        AppData.groups = mutableListOf()
        groupAdapter.notifyDataSetChanged()
    }

    private fun attachGroupsListener() {
        val uid = auth.currentUser?.uid ?: return
        val groupsRef = db.child("groups").child(uid)

        if (groupsListener != null) return

        groupsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newGroups = mutableListOf<Group>()
                val newKeys = mutableListOf<String>()

                for (groupSnap in snapshot.children) {
                    val key = groupSnap.key ?: continue
                    val name = groupSnap.child("name").getValue(String::class.java) ?: continue

                    newKeys.add(key)
                    newGroups.add(Group(name, mutableListOf()))
                }

                groupKeys.clear()
                groupKeys.addAll(newKeys)

                AppData.groups = newGroups
                groupAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        groupsRef.addValueEventListener(groupsListener as ValueEventListener)
    }

    fun addNewGroup(v: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Group")
        builder.setMessage("Enter the name of the new group:")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isEmpty()) return@setPositiveButton

            val uid = auth.currentUser?.uid ?: return@setPositiveButton
            val groupsRef = db.child("groups").child(uid)

            val newGroupRef = groupsRef.push()
            val newGroup = Group(name, mutableListOf())

            newGroupRef.setValue(newGroup)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }

    override fun groupLongClicked(index: Int) {
        if (index < 0 || index >= groupKeys.size) return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete this group?")
        builder.setMessage("Are you sure you want to delete this group?")

        builder.setPositiveButton("Delete") { _, _ ->
            val uid = auth.currentUser?.uid ?: return@setPositiveButton
            val groupId = groupKeys[index]
            deleteGroupAndItsTasks(uid, groupId)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }

    override fun groupClicked(index: Int) {
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("groupId", groupKeys[index])
        intent.putExtra("groupName", AppData.groups[index].name)
        startActivity(intent)
    }

    private fun deleteGroupAndItsTasks(uid: String, groupId: String) {
        val root = db
        val tasksRef = root.child("tasks").child(uid)

        tasksRef.orderByChild("groupId").equalTo(groupId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updates = mutableMapOf<String, Any?>()

                    updates["groups/$uid/$groupId"] = null

                    for (taskSnap in snapshot.children) {
                        val taskId = taskSnap.key ?: continue
                        updates["tasks/$uid/$taskId"] = null
                    }

                    root.updateChildren(updates)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun logout(v: View) {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}