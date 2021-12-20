package com.tare.mechat.entities

import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.tare.mechat.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_item.view.*

class LatestMessageItem(private val msg : ChatMessage) : Item<GroupieViewHolder>() {
    var chatPartner : User? = null
    override fun bind(p0: GroupieViewHolder, p1: Int) {
        p0.itemView.findViewById<TextView>(R.id.TVLatestUserMsg).text = msg.text
        var parterId = ""
        if(msg.fromId == FirebaseAuth.getInstance().uid)
        {
            parterId = msg.toId
        }else
        {
            parterId = msg.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$parterId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                chatPartner = user
                p0.itemView.findViewById<TextView>(R.id.TVLatestUserName).text = user.username
                Picasso.get().load(user.imgUrl).into(p0.itemView.CIVLatestUserImg)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_item
    }
}