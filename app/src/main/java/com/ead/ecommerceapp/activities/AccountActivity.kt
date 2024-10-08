package com.ead.ecommerceapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ead.ecommerceapp.databinding.ActivityAccountBinding
import com.ead.ecommerceapp.utils.SessionManager

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Display account details
//        binding.nameText.text = sessionManager.getToken() // Replace with actual user name
//        binding.emailText.text = sessionManager.getToken() // Replace with actual email
        binding.nameText.text = "Name - Amal" // Replace with actual user name
        binding.emailText.text = "Email  - amal@gmail.com" // Replace with actual email

        binding.logoutButton.setOnClickListener {
            sessionManager.logout()
            finish()  // Optionally navigate to LoginActivity if needed
        }
    }
}
