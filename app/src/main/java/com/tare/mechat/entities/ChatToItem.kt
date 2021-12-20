package com.tare.mechat.entities

import android.widget.TextView
import com.squareup.picasso.Picasso
import com.tare.mechat.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_item.view.*

class ChatToItem(val text : String, private val user: User) : Item<GroupieViewHolder>() {
    override fun bind(view: GroupieViewHolder, p1: Int) {
        view.itemView.findViewById<TextView>(R.id.TVUserMessageTo).text = text
        Picasso.get().load(user.imgUrl).into(view.itemView.CIVChatUserTo)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_item
    }
}