package com.example.sarathi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sarathi.adaptor.MessageAdaptor
import com.example.sarathi.databinding.ActivityMessageBinding
import com.example.sarathi.model.ApiUtilities
import com.example.sarathi.model.MessageModel
import com.example.sarathi.model.NotificationData
import com.example.sarathi.model.PushNotification
import com.example.sarathi.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        getData(intent.getStringExtra("chat_id"))

        verifyChatId()
        binding.sendBtn.setOnClickListener {
            if (binding.yourMessage.text!!.isEmpty()){
                Toast.makeText(this@MessageActivity,"Enter your message",Toast.LENGTH_SHORT).show()
            }else{
                storeData(binding.yourMessage.text.toString())
            }
        }

    }

    private var senderId: String? = null
    private var chatId: String? = null
    private var receiverId: String? = null
    private fun verifyChatId() {
        senderId = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        receiverId = intent.getStringExtra("user_id")
        val reverseChatId = receiverId+senderId
        chatId = senderId+receiverId
        val reference =FirebaseDatabase.getInstance().getReference("chat")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(chatId!!)){
                    getData(chatId)
                }else if (snapshot.hasChild(reverseChatId)){
                    chatId = reverseChatId
                    getData(chatId)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getData(chatId: String?) {
        FirebaseDatabase.getInstance().getReference("chat").child(chatId!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = arrayListOf<MessageModel>()
                    for (show in snapshot.children){
                        list.add(show.getValue(MessageModel::class.java)!!)
                    }
                    binding.recyclerView.adapter = MessageAdaptor(this@MessageActivity,list)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MessageActivity,error.message,Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun storeData(msg: String) {
        val map = hashMapOf<String,String>()
        map["message"]=msg
        map["senderId"]= senderId!!
        val reference =FirebaseDatabase.getInstance().getReference("chat").child(chatId!!)
        reference.child(reference.push().key!!).setValue(map).addOnCompleteListener {
            if (it.isSuccessful){
                binding.yourMessage.text = null
                //here is code for notification
                sendNotification(msg)

            }else{
                Toast.makeText(this@MessageActivity,"Firebase problem",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotification(msg: String) {
        FirebaseDatabase.getInstance().getReference("UserData").child(receiverId!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    val notificationData = PushNotification(NotificationData("New Message", msg),data!!.fcmToken)
                    ApiUtilities.getInstance().sendNotification(
                        notificationData
                    ).enqueue(object : Callback<PushNotification>{
                        override fun onResponse(
                            call: Call<PushNotification>,
                            response: Response<PushNotification>
                        ) {
                            Toast.makeText(this@MessageActivity,"🔔",Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                            Toast.makeText(this@MessageActivity,"Notification Problem",Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity,error.message,Toast.LENGTH_SHORT).show()
            }

        })

    }

}