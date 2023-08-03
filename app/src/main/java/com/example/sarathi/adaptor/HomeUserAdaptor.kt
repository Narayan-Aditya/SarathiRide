package com.example.sarathi.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sarathi.MessageActivity
import com.example.sarathi.model.UserModel
import com.example.sarathi.databinding.UserItemBinding
import com.google.firebase.auth.FirebaseAuth

class HomeUserAdaptor(val context: Context, val list: ArrayList<UserModel>) : RecyclerView.Adapter<HomeUserAdaptor.MessageUserViewHolder>() {
    inner class MessageUserViewHolder(val binding: UserItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageUserViewHolder {
        return MessageUserViewHolder(UserItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MessageUserViewHolder, position: Int) {

        Glide.with(context).load(list[position].image).into(holder.binding.dpImg)
        holder.binding.userName.text = list[position].name
        holder.binding.userCityAName.text = list[position].cityA
        holder.binding.userCityBName.text = list[position].cityB



// this is a item view button which click to open user chat layout and as send the user number.
        holder.itemView.setOnClickListener {
            val intent = Intent(context,MessageActivity::class.java)
            intent.putExtra("user_id",list[position].number)
            context.startActivity(intent)
        }
//        holder.binding.chat.setOnClickListener {
//
//        }
    }

}