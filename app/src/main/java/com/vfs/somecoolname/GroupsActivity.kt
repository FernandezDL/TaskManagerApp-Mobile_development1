package com.vfs.somecoolname

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupsActivity : AppCompatActivity(), GroupListener {
    lateinit var groupAdapter: GroupsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.groups_layout)
        
        AppData.initialize()
        
        val groupsRv = findViewById<RecyclerView>(R.id.groupsRecyclerView_id)
        groupsRv.layoutManager = LinearLayoutManager(this)
        
        groupAdapter = GroupsAdapter(this)
        groupsRv.adapter = groupAdapter
    }
    
    fun addNewGroup(v: View){
        val builder = AlertDialog.Builder(this)
        
        builder.setTitle("New Group")
        builder.setMessage("Enter the name of the new group:")
        
        val input = EditText(this)
        builder.setView(input)
        
        builder.setPositiveButton("Add"){_, _ ->
            if(input.text.toString() == "") return@setPositiveButton

            val newGroup = Group(input.text.toString(), mutableListOf())
            AppData.groups.add(newGroup)
            
            groupAdapter.notifyDataSetChanged()
        }
        
        builder.setNegativeButton("Cancel"){_, _ -> }
        
        builder.show()
    }
    
    override fun groupLongClicked(index: Int) {
        val builder = AlertDialog.Builder(this)
        
        builder.setTitle("Delete this group?")
        builder.setMessage("Are you sure you want to delete this group?")
        
        builder.setPositiveButton("Delete"){_, _ ->
            AppData.groups.removeAt(index)
            groupAdapter.notifyDataSetChanged()
        }
        
        builder.setNegativeButton("Cancel"){_, _ -> }
        
        builder.show()
    }
    
    override fun groupClicked(index: Int) {
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra("index", index) //sends the index to the next activity
        startActivity(intent)
    }
}