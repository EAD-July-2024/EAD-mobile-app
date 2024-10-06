package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ead.ecommerceapp.databinding.ActivityPaymentBinding
import com.ead.ecommerceapp.models.CartItem
import com.ead.ecommerceapp.repositories.CartRepository
import com.ead.ecommerceapp.repositories.HttpRepository
import com.ead.ecommerceapp.utils.SessionManager
import org.json.JSONArray
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.payNowButton.setOnClickListener {
            // Validate card details
            val cardNumber = binding.cardNumberEditText.text.toString().trim()
            val expiryDate = binding.expiryDateEditText.text.toString().trim()
            val cvv = binding.cvvEditText.text.toString().trim()

            if (validateCardDetails(cardNumber, expiryDate, cvv)) {
                placeOrder()
            } else {
                Toast.makeText(this, "Invalid card details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCardDetails(cardNumber: String, expiryDate: String, cvv: String): Boolean {
        // Implement validation logic for card number, expiry date, and CVV
        return cardNumber.length == 16 && expiryDate.matches(Regex("\\d{2}/\\d{2}")) && cvv.length == 3
    }

    private fun placeOrder() {
        val cartItems: List<CartItem> = CartRepository.getCartItems(this)
        val customerId = sessionManager.getUserId() ?: return
        val token = sessionManager.getToken() ?: return

        val productList = JSONArray().apply {
            cartItems.forEach { cartItem ->
                put(JSONObject().apply {
                    put("productId", cartItem.product.id)
                    put("quantity", cartItem.quantity)
                })
            }
        }

        val jsonBody = JSONObject().apply {
            put("customerId", customerId)
            put("productList", productList)
        }

        HttpRepository.postRequestWithToken("order", jsonBody, token) { response ->
            runOnUiThread {
                if (response != null) {
                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()

                    // Clear the cart
                    CartRepository.updateCart(emptyList(), this)  // Clear the cart by passing an empty list

                    // Navigate to ProductListActivity
                    val intent = Intent(this, ProductListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
