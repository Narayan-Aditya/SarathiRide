package com.example.sarathi.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sarathi.MessageActivity
import com.example.sarathi.databinding.ChatuserBinding
import com.example.sarathi.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatUserAdaptor (val context: Context,val list: ArrayList<String>, val chatKey: List<String>): RecyclerView.Adapter<ChatUserAdaptor.ChatUserViewHolder>()
{

    inner class ChatUserViewHolder(val binding: ChatuserBinding): RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        return ChatUserViewHolder(ChatuserBinding.inflate(LayoutInflater.from(context),parent,false))

    }


    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {

        FirebaseDatabase.getInstance().getReference("UserData").child(list[position])
            .addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(context).load(data!!.image).into(holder.binding.dpImg)
                    holder.binding.userName.text = data.name
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }
        })

        holder.itemView.setOnClickListener {
            val intent = Intent(context,MessageActivity::class.java)
            intent.putExtra("chat_id",chatKey[position])
            intent.putExtra("user_id",list[position])
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size

    }



}