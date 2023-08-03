package com.example.sarathi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sendOtpBtn: Button
    private lateinit var phoneNumEd: EditText
    private lateinit var number: String
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        progressBar = findViewById(R.id.progress_bar)

        //initialize firebase
        auth = FirebaseAuth.getInstance()

        //init function for perform the send otp to user mobile number
        inti()

        //get number for intent
        sendOtpBtn.setOnClickListener {
            number = phoneNumEd.text.trim().toString()
            if (number.isNotEmpty() && number.length == 10){
                //In firebase always use number with country code.
                number="+91$number"
                progressBar.visibility = View.VISIBLE
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }else{
                Toast.makeText(this@LoginScreen,"Mobile No. 😢",Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun inti(){
        sendOtpBtn = findViewById(R.id.optBtn)
        phoneNumEd = findViewById(R.id.number)
    }

    //call back block
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            progressBar.visibility= View.GONE
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            progressBar.visibility = View.GONE
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG","onVerificationFailed,$e")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG","onVerificationFailed,$e")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            progressBar.visibility = View.GONE
            val intent = Intent(this@LoginScreen,OtpScreen::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber",number)
            startActivity(intent)
        }
    }

    //signInWithPhoneAuthCredential blocks
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginScreen,"Good to Go",Toast.LENGTH_SHORT).show()
                }
                else {
                    Log.d("TAG","signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this@LoginScreen,"Enter OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}