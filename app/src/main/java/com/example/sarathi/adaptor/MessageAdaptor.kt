package com.example.sarathi.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sarathi.R
import com.example.sarathi.model.MessageModel
import com.example.sarathi.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageAdaptor(val context: Context, val list: List<MessageModel>)
    : RecyclerView.Adapter<MessageAdaptor.MessageViewHolder>(){

    val MSG_ITEM_RIGHT = 0
    val MSG_ITEM_LEFT = 1

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val text = itemView.findViewById<TextView>(R.id.messageTxt)
        val image = itemView.findViewById<ImageView>(R.id.messageImg)

    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == FirebaseAuth.getInstance().currentUser!!.phoneNumber){
            MSG_ITEM_LEFT
        }else{
            MSG_ITEM_RIGHT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == MSG_ITEM_RIGHT){
            MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.receiver,parent,false))
        }else{
            MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.sender,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.text.text = list[position].message
        FirebaseDatabase.getInstance().getReference("UserData").child(list[position].senderId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val data = snapshot.getValue(UserModel::class.java)
                        Glide.with(context).load(data!!.image).placeholder(R.drawable.man).into(holder.image)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

}




