package com.example.sarathi.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sarathi.R
import com.example.sarathi.model.UserModel
import com.example.sarathi.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(layoutInflater)

        //Get Data from firebase to show in the application
        FirebaseDatabase.getInstance().getReference("UserData")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {
                if (it.exists()){
                    val data = it.getValue(UserModel::class.java)
                    binding.name.setText(data!!.name.toString())
                    binding.carNumber.setText(data.carNumber.toString())
                    binding.UserNumber.setText(data.number.toString())
                    binding.cityA.setText(data.cityA.toString())
                    binding.cityB.setText(data.cityB.toString())
                    Glide.with(this).load(data.image).placeholder(R.drawable.profile).into(binding.dpImg)
                }
            }

        binding.userData.setOnClickListener {
            updateData()
        }


        return binding.root
    }

    private fun updateData() {
        if (binding.cityA.text.toString().isEmpty() || binding.cityB.text.toString().isEmpty()){
            Toast.makeText(requireContext(),"City name not found",Toast.LENGTH_SHORT).show()
        }
        else
            storeData()
    }


    private fun storeData() {
        FirebaseDatabase.getInstance().getReference("UserData")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .child("cityA")
            .setValue(binding.cityA.text.toString())
            .addOnSuccessListener {
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            }

        FirebaseDatabase.getInstance().getReference("UserData")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .child("cityB")
            .setValue(binding.cityB.text.toString())
            .addOnSuccessListener {
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            }

    }
}

