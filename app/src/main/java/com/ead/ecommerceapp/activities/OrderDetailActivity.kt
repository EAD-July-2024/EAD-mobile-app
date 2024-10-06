package com.ead.ecommerceapp.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.ecommerceapp.adapters.OrderItemAdapter
import com.ead.ecommerceapp.databinding.ActivityOrderDetailBinding
import com.ead.ecommerceapp.models.OrderItem
import com.ead.ecommerceapp.repositories.HttpRepository
import com.ead.ecommerceapp.utils.SessionManager
import org.json.JSONObject

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var sessionManager: SessionManager
    private var orderId: String? = null
    private lateinit var orderItems: List<OrderItem>  // List to hold order items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        orderId = intent.getStringExtra("orderId")

        // Display order details based on orderId passed from OrderListActivity
        displayOrderDetails()

        // Set up RecyclerView for order items
        binding.orderItemsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Cancel order functionality
        binding.cancelOrderButton.setOnClickListener {
            showCancellationNoteDialog()  // Show dialog to get user input
        }
    }

    private fun displayOrderDetails() {
        // Fetch and display the order details (can implement fetching from an API if needed)
        binding.orderIdText.text = "Order ID: $orderId"

        // Assume `orderItems` are passed via intent or fetched from an API
        orderItems = intent.getSerializableExtra("orderItems") as List<OrderItem>

        // Set the adapter for RecyclerView
        val adapter = OrderItemAdapter(this, orderItems)
        binding.orderItemsRecyclerView.adapter = adapter
    }

    private fun showCancellationNoteDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Cancel Order")

        // Set up the input
        val input = EditText(this)
        input.hint = "Enter cancellation note"
        dialogBuilder.setView(input)

        // Set up the buttons
        dialogBuilder.setPositiveButton("Confirm") { _, _ ->
            val cancellationNote = input.text.toString()
            cancelOrder(cancellationNote)  // Send the custom note
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()  // Dismiss dialog
        }

        dialogBuilder.show()
    }

    private fun cancelOrder(cancellationNote: String) {
        val token = sessionManager.getToken() ?: return
        val jsonBody = JSONObject().apply {
            put("note", cancellationNote)
        }

        HttpRepository.patchRequestWithToken("order/updateStatus/$orderId", jsonBody, token) { response ->
            if (response != null) {
                runOnUiThread {
                    Toast.makeText(this, "Order cancelled successfully", Toast.LENGTH_SHORT).show()
                    finish()  // Go back to the previous screen after canceling
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
