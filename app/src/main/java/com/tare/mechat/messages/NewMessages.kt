package com.tare.mechat.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tare.mechat.R
import com.tare.mechat.entities.User
import com.tare.mechat.entities.UserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class NewMessages : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var db : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)
        supportActionBar?.title = "Select User"
        db = FirebaseDatabase.getInstance()

        //---------------------RecyclerView-----------------//
        recyclerView = findViewById(R.id.RVNewMessage)
        fetchUsers()
    }


    private fun fetchUsers()
    {
        val reference = db.getReference("/users")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem(user!!))
                }
                
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLog::class.java)
                    intent.putExtra(USER_KEY, userItem.getUser())
                    startActivity(intent)
                    finish()
                }
                recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {
        const val USER_KEY = "name"
    }
}
