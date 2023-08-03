package com.example.sarathi

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sarathi.model.UserModel
import com.example.sarathi.databinding.ActivityRegistrationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var progressBar: ProgressBar

    //save image from the gallery.
    private var imageUri: Uri? = null
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.dpImg.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progress_bar)

        //open gallery on img click
        binding.dpImg.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.userData.setOnClickListener {
            validateData()
            progressBar.visibility = View.VISIBLE
        }
    }

    //validateData function body is here
    private fun validateData() {
        if (binding.name.text.toString().isEmpty()
            || binding.carNumber.text.toString().isEmpty()
            || binding.cityA.text.toString().isEmpty()
            || binding.cityB.text.toString().isEmpty()
        ) {
            Toast.makeText(this@RegistrationActivity, "Enter all fields", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        } else if (imageUri == null) {
            Toast.makeText(this@RegistrationActivity, "image fields is empty", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
        else{
            uploadImage()

        }
    }

    private fun uploadImage() {
        val storeRef = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .child("userDP.jpg")

        storeRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storeRef.downloadUrl.addOnSuccessListener {
                    storeData(it)
                }.addOnFailureListener {
                    Toast.makeText(this@RegistrationActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this@RegistrationActivity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(imageUrl: Uri?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result

            val data = UserModel(
                image = imageUrl.toString(),
                name = binding.name.text.toString(),
                carNumber = binding.carNumber.text.toString(),
                cityA = binding.cityA.text.toString(),
                cityB = binding.cityB.text.toString(),
                number = FirebaseAuth.getInstance().currentUser!!.phoneNumber,
                fcmToken = token
            )
            FirebaseDatabase.getInstance().getReference("UserData")
                .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                .setValue(data).addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressBar.visibility = View.GONE
                        startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                        finish()
                        Toast.makeText(this@RegistrationActivity, "User register", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@RegistrationActivity,it.exception!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
        })


    }
}



