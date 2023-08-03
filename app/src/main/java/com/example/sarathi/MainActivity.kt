package com.example.sarathi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.sarathi.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()

        actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navigationView.setNavigationItemSelectedListener(this@MainActivity)

        val navController = findNavController(R.id.fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.rateUs ->{
                Toast.makeText(this,"Rate us",Toast.LENGTH_SHORT).show()
            }
            R.id.developer ->  {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/in/narayanaditya21"))
                startActivity(browserIntent)
            }
            R.id.sighOut ->{
               //here is code for sighOut
                Toast.makeText(this@MainActivity,"See you Again",Toast.LENGTH_SHORT).show()
                auth.signOut()
                startActivity(Intent(this@MainActivity,LoginScreen::class.java))
                finish()
            }
            R.id.contactUs ->{
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+917607751292"))
                startActivity(browserIntent)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)){
            true
        }else
            super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.close()
        }else
            super.onBackPressed()
    }

}