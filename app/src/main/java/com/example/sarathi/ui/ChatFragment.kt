package com.example.sarathi.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sarathi.adaptor.ChatUserAdaptor
import com.example.sarathi.databinding.FragmentChatBinding
import com.example.sarathi.ui.HomeFragment.Companion.list
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(layoutInflater)

        getData()

        return binding.root
    }

    private fun getData() {

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        FirebaseDatabase.getInstance().getReference("chat")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = arrayListOf<String>()
                    val newList = arrayListOf<String>()
                    for (data in snapshot.children){
                        if (data.key!!.contains(currentUserId!!)){
                            list.add(data.key!!.replace(currentUserId,""))
                            newList.add(data.key!!)
                        }
                    }
                    try {
                        binding.chatRecyclerView.adapter = ChatUserAdaptor(requireContext(), list,newList)
                    } catch (_: Exception){
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }

            })
    }


}