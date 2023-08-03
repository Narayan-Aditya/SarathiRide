package com.example.sarathi.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sarathi.adaptor.HomeUserAdaptor
import com.example.sarathi.databinding.FragmentHomeBinding
import com.example.sarathi.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        getData()

        return binding.root
    }

    companion object{
        var list: ArrayList<UserModel>? = null
    }

    private fun getData() {
        FirebaseDatabase.getInstance().getReference("UserData")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        list = arrayListOf()
                        for (data in snapshot.children){
                            val model = data.getValue(UserModel::class.java)
                            if(model!!.number != FirebaseAuth.getInstance().currentUser!!.phoneNumber) {
                                list!!.add(model)
                            }
                        }
                        binding.recyclerVT.adapter = HomeUserAdaptor(requireContext(),list!!)
                    }else{
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }
            })
    }

}