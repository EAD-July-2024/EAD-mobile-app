package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.ecommerceapp.adapters.CartAdapter
import com.ead.ecommerceapp.databinding.ActivityCartBinding
import com.ead.ecommerceapp.repositories.CartRepository
import com.ead.ecommerceapp.utils.SessionManager

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Set up RecyclerView
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(this, CartRepository.getCartItems(this).toMutableList()) // Pass context to CartAdapter
        binding.cartRecyclerView.adapter = cartAdapter

        // Handle total price calculation
        val totalPrice = CartRepository.calculateTotalPrice(this) // Pass context here
        binding.totalPriceText.text = "Total: $$totalPrice"

        // Checkout button action
        binding.checkoutButton.setOnClickListener {
            if (sessionManager.isLoggedIn()) {
                // User is logged in, navigate to payment screen
                startActivity(Intent(this, PaymentActivity::class.java))
            } else {
                // User is not logged in, navigate to login screen
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        // Allow users to modify or remove items in the cart
        cartAdapter.setOnItemRemovedListener { updatedItems ->
            CartRepository.updateCart(updatedItems, this)  // Pass context here
            binding.totalPriceText.text = "Total: $${CartRepository.calculateTotalPrice(this)}" // Pass context here
        }
    }
}
