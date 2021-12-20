package com.tare.mechat.entities

import android.widget.TextView
import com.squareup.picasso.Picasso
import com.tare.mechat.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class UserItem(private val user : User) : Item<GroupieViewHolder>()
{
    fun getUser() : User
    {
        return user
    }
    override fun bind(viewHolder: GroupieViewHolder, p1: Int) {
        val userName = viewHolder.itemView.findViewById<TextView>(R.id.TVUserName)
        val img = viewHolder.itemView.findViewById<CircleImageView>(R.id.CIVUserImg)
        userName.text = user.username
        Picasso.get().load(user.imgUrl).into(img)
    }
    override fun getLayout(): Int {
        return R.layout.new_user
    }
}