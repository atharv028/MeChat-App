package com.tare.mechat.messages

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tare.mechat.R
import com.tare.mechat.entities.ChatFromItem
import com.tare.mechat.entities.ChatMessage
import com.tare.mechat.entities.ChatToItem
import com.tare.mechat.entities.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class ChatLog : AppCompatActivity() {
    companion object{
        const val TAG = "ChatLogACTIVITY"
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var button : Button
    private lateinit var db : FirebaseDatabase
    private lateinit var msg : EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : GroupAdapter<GroupieViewHolder>
    private lateinit var fromId : String
    private lateinit var toId : String
    private lateinit var keyGenerator : KeyGenerator
    private var secretKey : SecretKey? = null
    private var random : SecureRandom = SecureRandom()
    private var secureIV : ByteArray = ByteArray(16)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        val user = intent.getParcelableExtra<User>(NewMessages.USER_KEY)!!
        supportActionBar?.title = user.username
        //------------------------------VARS----------------------------//
        recyclerView = findViewById(R.id.RVChatLog)
        button = findViewById(R.id.BTSend)
        msg = findViewById(R.id.ETMessage)
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()
        recyclerView.adapter = adapter
        fromId = auth.uid!!
        toId = user.uid
        try {
            keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            secretKey = keyGenerator.generateKey()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        random.nextBytes(secureIV)
        //-------------------------------------------------------------//

        button.setOnClickListener {
            sendMessage()
        }
        listenForMessage(user)

    }

    private fun sendMessage()
    {
        val text = msg.editableText.toString()
        val cipherText = EncryptMessages().encrypt(text.toString().toByteArray(), secretKey!!, secureIV)
        Log.d("Encrypt", String(cipherText, Charsets.UTF_8))
        val decryptString = EncryptMessages().decrypt(cipherText, secretKey!!, secureIV)!!
        Log.d("Decrypt", decryptString)
        val timeStamp = System.currentTimeMillis() / 1000
        val reference = db.getReference("/user-messages/$fromId/$toId").push()
        val toReference = db.getReference("/user-messages/$toId/$fromId").push()
        val latestReference = db.getReference("/latest-messages/$fromId/$toId")
        val tolatestReference = db.getReference("/latest-messages/$toId/$fromId")
        val chatMessage = ChatMessage(text, reference.key!!, fromId, toId, timeStamp)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved message with ${reference.key}")
                msg.text.clear()
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)
        latestReference.setValue(chatMessage)
        tolatestReference.setValue(chatMessage)
    }

    private fun listenForMessage(user: User)
    {
        val reference = db.getReference("/user-messages/$fromId/$toId")
        reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if(chatMessage.fromId == auth.uid)
                    {
                        adapter.add(ChatToItem(chatMessage.text, LatestMessages.currentUser!!))

                    }
                    else
                        adapter.add(ChatFromItem(chatMessage.text, user))

                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}