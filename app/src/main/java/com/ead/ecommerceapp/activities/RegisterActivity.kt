package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ead.ecommerceapp.databinding.ActivityRegisterBinding
import com.ead.ecommerceapp.repositories.HttpRepository
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listener for the register button
        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        // Get user input from the registration form
        val email = binding.emailInput.text.toString()
        val fullName = binding.fullNameInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val contactInfo = binding.contactInfoInput.text.toString()

        // Check if any input is empty
        if (email.isEmpty() || fullName.isEmpty() || password.isEmpty() || contactInfo.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Create JSON body for the request
        val requestBody = JSONObject().apply {
            put("email", email)
            put("fullName", fullName)
            put("password", password)
            put("contactInfo", contactInfo)
        }

        // Call the registration API
        HttpRepository.postRequest("auth/register", requestBody) { response ->
            if (response != null) {
                // On successful registration, navigate to ProductListActivity
                runOnUiThread {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProductListActivity::class.java)
                    startActivity(intent)
                    finish()  // Close the current activity
                }
            } else {
                // Show error message
                runOnUiThread {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
