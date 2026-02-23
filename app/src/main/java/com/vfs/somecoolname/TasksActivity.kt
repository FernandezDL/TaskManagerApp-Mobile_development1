package com.vfs.somecoolname

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TasksActivity : AppCompatActivity(), TasksListener {

    lateinit var taskAdapter: TasksAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    private var tasksListener: ValueEventListener? = null
    private val taskKeys: MutableList<String> = mutableListOf()

    private lateinit var groupId: String
    private lateinit var group: Group

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        attachTasksListener()
    }

    override fun onStop() {
        super.onStop()
        detachTasksListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.taks_layout)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        groupId = intent.getStringExtra("groupId") ?: ""

        // fallback por si entraste con index viejo (pero lo ideal es pasar groupId siempre)
        val groupNameFromIntent = intent.getStringExtra("groupName")
        val index = intent.getIntExtra("index", -1)

        val groupName = when {
            groupNameFromIntent != null -> groupNameFromIntent
            index in 0 until AppData.groups.size -> AppData.groups[index].name
            else -> ""
        }

        if (groupId.isBlank() || groupName.isBlank()) {
            finish()
            return
        }

        group = Group(groupName, mutableListOf())

        val grpTextView = findViewById<TextView>(R.id.grpNameTextView_id)
        grpTextView.text = group.name

        val tasksRv = findViewById<RecyclerView>(R.id.tasksRecyclerView_id)
        tasksRv.layoutManager = LinearLayoutManager(this)

        taskAdapter = TasksAdapter(this, group)
        tasksRv.adapter = taskAdapter

        findViewById<Button>(R.id.backBttn_id).setOnClickListener { finish() }

        findViewById<Button>(R.id.newTaskBttn_id).setOnClickListener { showAddTaskDialog() }
    }

    private fun attachTasksListener() {
        val uid = auth.currentUser?.uid ?: return
        if (tasksListener != null) return

        val tasksRef = db.child("tasks").child(uid)

        tasksListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newTasks = mutableListOf<Task>()
                val newKeys = mutableListOf<String>()

                for (taskSnap in snapshot.children) {
                    val key = taskSnap.key ?: continue
                    val tGroupId = taskSnap.child("groupId").getValue(String::class.java) ?: continue
                    if (tGroupId != groupId) continue

                    val title = taskSnap.child("title").getValue(String::class.java) ?: continue
                    val done = taskSnap.child("done").getValue(Boolean::class.java) ?: false

                    newKeys.add(key)
                    newTasks.add(Task(title, done)) // mapea a tu modelo actual: name/completed
                }

                taskKeys.clear()
                taskKeys.addAll(newKeys)

                group.tasks.clear()
                group.tasks.addAll(newTasks)
                taskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // opcional: mostrar error
            }
        }

        // Importante: como tu estructura no está indexada por groupId “nativamente” (no está anidado),
        // aquí escuchamos tasks/{uid} y filtramos por groupId.
        // (Luego podemos optimizar con una query + index si quieres.)
        tasksRef.addValueEventListener(tasksListener as ValueEventListener)
    }

    private fun detachTasksListener() {
        val uid = auth.currentUser?.uid ?: return
        val listener = tasksListener ?: return
        db.child("tasks").child(uid).removeEventListener(listener)
        tasksListener = null
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New task")
        builder.setMessage("Enter the name of the new task:")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val title = input.text.toString().trim()
            if (title.isEmpty()) return@setPositiveButton
            createTask(title)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
    }

    private fun createTask(title: String) {
        val uid = auth.currentUser?.uid ?: return
        val newRef = db.child("tasks").child(uid).push()

        val payload = mapOf(
            "title" to title,
            "done" to false,
            "groupId" to groupId,
            "createdAt" to ServerValue.TIMESTAMP
        )

        newRef.setValue(payload)
    }

    override fun taskLongClicked(index: Int) {
        if (index !in 0 until group.tasks.size) return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit task")
        builder.setMessage("Enter the new name for the task:")

        val input = EditText(this)
        val task = group.tasks[index]
        input.setText(task.name)
        input.setSelection(task.name.length)
        builder.setView(input)

        builder.setPositiveButton("Edit") { _, _ ->
            val newTitle = input.text.toString().trim()
            if (newTitle.isEmpty()) return@setPositiveButton
            updateTaskTitle(index, newTitle)
        }

        builder.setNegativeButton("Cancel") { _, _ -> }

        builder.setNeutralButton("Delete") { _, _ ->
            deleteTask(index)
        }

        builder.show()
    }

    override fun taskClicked(index: Int) {
        if (index !in 0 until group.tasks.size) return
        toggleTaskDone(index)
    }

    private fun updateTaskTitle(index: Int, newTitle: String) {
        val uid = auth.currentUser?.uid ?: return
        if (index !in 0 until taskKeys.size) return
        val taskId = taskKeys[index]

        db.child("tasks").child(uid).child(taskId).child("title").setValue(newTitle)
    }

    private fun deleteTask(index: Int) {
        val uid = auth.currentUser?.uid ?: return
        if (index !in 0 until taskKeys.size) return
        val taskId = taskKeys[index]

        db.child("tasks").child(uid).child(taskId).removeValue()
    }

    private fun toggleTaskDone(index: Int) {
        val uid = auth.currentUser?.uid ?: return
        if (index !in 0 until taskKeys.size) return
        val taskId = taskKeys[index]

        val current = group.tasks[index].completed
        db.child("tasks").child(uid).child(taskId).child("done").setValue(!current)
    }
}