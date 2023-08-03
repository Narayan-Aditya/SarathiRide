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
import com.example.sarathi.model.UserModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class OtpScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var validateBtn: Button
    private lateinit var resendBtn: Button
    private lateinit var inputOtp: EditText

    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_screen)
        otp = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        progressBar = findViewById(R.id.progress_bar)


        init()

        //validation working here
        validateBtn.setOnClickListener {
            val typedOtp = inputOtp.text.toString()
            progressBar.visibility = View.VISIBLE
            if (typedOtp.isNotEmpty() && typedOtp.length == 6){
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    otp , typedOtp
                )
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this@OtpScreen,"OTP Wrong 😒",Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }

        resendBtn.setOnClickListener {
            inputOtp = findViewById(R.id.edOtp)
            inputOtp.text = null
            progressBar.visibility = View.VISIBLE
            resendCode()
        }

    }

    //here is init function body
    private fun init(){
        auth = FirebaseAuth.getInstance()
        validateBtn = findViewById(R.id.validationBtn)
        resendBtn = findViewById(R.id.resendBtn)
        inputOtp = findViewById(R.id.edOtp)
    }

    //signInWithPhoneAuthCredential blocks
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressBar.visibility = View.GONE
                    //this is check user is already in our firebase or not.
                    FirebaseDatabase.getInstance().getReference("UserData")
                        .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                val data = it.getValue(UserModel::class.java)
                                if(data!!.name.toString().isEmpty() ||
                                        data.carNumber.toString().isEmpty() ||
                                        data.number.toString().isEmpty()){
                                    Toast.makeText(this@OtpScreen,"Enter your details",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@OtpScreen,RegistrationActivity::class.java))
                                    finish()
                                }else {
                                    Toast.makeText(
                                        this@OtpScreen,
                                        "Welcome " + data.name.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@OtpScreen, MainActivity::class.java))
                                    finish()
                                }
                            }else{
                                Toast.makeText(this@OtpScreen,"Hey New User",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@OtpScreen,RegistrationActivity::class.java))
                                finish()
                            }
                        }


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG","signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@OtpScreen,"OTP",Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                    // Update UI
                }
            }
    }

    //resend code block
    private fun resendCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    //callback body is here
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
            progressBar.visibility = View.GONE
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

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
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            progressBar.visibility = View.GONE
            // Save verification ID and resending token so we can use them later
            otp = verificationId
            resendToken = token

        }
    }



}