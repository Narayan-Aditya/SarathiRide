package com.example.sarathi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val user = FirebaseAuth.getInstance().currentUser

        //splash screen code
        Handler(Looper.getMainLooper()).postDelayed({
            if (user == null) {
                startActivity(Intent(this@SplashScreen, LoginScreen::class.java))
                finish()
            }
                else {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }
        },2000)
    }
}