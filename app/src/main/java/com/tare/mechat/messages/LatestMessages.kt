package com.tare.mechat.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.internal.InternalTokenProvider
import com.tare.mechat.R
import com.tare.mechat.entities.ChatMessage
import com.tare.mechat.entities.LatestMessageItem
import com.tare.mechat.entities.User
import com.tare.mechat.entities.UserItem
import com.tare.mechat.login.RegisterPage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class LatestMessages : AppCompatActivity() {
    companion object{
        var currentUser : User? = null
    }
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseDatabase
    private lateinit var adapter : GroupAdapter<GroupieViewHolder>
    private lateinit var recyclerView: RecyclerView
    private var latestMessageMap = HashMap<String, ChatMessage>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        //--------------------------------VARS-----------------------//
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()
        recyclerView = findViewById(R.id.RVLatestMessage)
        recyclerView.adapter = adapter
        //---------------------------------------------------------//

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, _ ->
            val row = item as LatestMessageItem
            val intent = Intent(this, ChatLog::class.java)
            intent.putExtra(NewMessages.USER_KEY, row.chatPartner)
            startActivity(intent)
        }
        check()
        fetchCurrentUser()
        latestMessageListener(auth.uid)
    }

    private fun latestMessageListener(fromId : String?)
    {
        val reference = db.getReference("/latest-messages/$fromId")
        reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)!!
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)!!
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun refreshRecyclerView()
    {
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageItem(it))
        }
    }

    private fun fetchCurrentUser()
    {
        val uid = auth.uid
        val reference = db.getReference("/users/$uid")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("LatestMessage", "Current User : ${currentUser?.username!!}")
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.signOut -> {
                auth.signOut()
                val intent = Intent(this, RegisterPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
                finish()
            }
            R.id.newMsg -> {
                val intent = Intent(this, NewMessages::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun check()
    {
        if(auth.currentUser == null)
        {
            val intent = Intent(this, RegisterPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


}