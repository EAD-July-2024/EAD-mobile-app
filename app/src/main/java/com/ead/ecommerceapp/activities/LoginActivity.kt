package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ead.ecommerceapp.databinding.ActivityLoginBinding
import com.ead.ecommerceapp.repositories.HttpRepository
import com.ead.ecommerceapp.utils.SessionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        HttpRepository.postRequest("auth/login", jsonBody) { response ->
            if (response != null) {
                val jsonResponse = JSONObject(response)
                val token = jsonResponse.getString("token")
                val role = jsonResponse.getString("role")
                val email = jsonResponse.getString("email")
                val userId = jsonResponse.getString("userId")

                // Save user session
                sessionManager.saveUserSession(token, role, email, userId)

                // Navigate to the payment screen
                startActivity(Intent(this, PaymentActivity::class.java))
                finish()
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Login failed, please try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
