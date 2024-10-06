package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.ecommerceapp.adapters.OrderAdapter
import com.ead.ecommerceapp.databinding.ActivityOrderListBinding
import com.ead.ecommerceapp.models.Order
import com.ead.ecommerceapp.models.OrderItem
import com.ead.ecommerceapp.repositories.HttpRepository
import com.ead.ecommerceapp.utils.SessionManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var orderAdapter: OrderAdapter
    private var orderList: List<Order> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId() ?: return
        val token = sessionManager.getToken() ?: return

        // Set up RecyclerView
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)

        // Fetch orders from API
        HttpRepository.getRequestWithToken("order/getByCustomerId/$userId", token) { response ->
            if (response != null) {
                val jsonArray = JSONArray(response)
                val orders = mutableListOf<Order>()

                for (i in 0 until jsonArray.length()) {
                    val jsonOrder = jsonArray.getJSONObject(i)

                    // Parse orderItems
                    val orderItemsArray = jsonOrder.getJSONArray("orderItems")
                    val orderItems = mutableListOf<OrderItem>()
                    for (j in 0 until orderItemsArray.length()) {
                        val jsonOrderItem = orderItemsArray.getJSONObject(j)
                        val orderItem = OrderItem(
                            orderId = jsonOrderItem.getString("orderId"),
                            productId = jsonOrderItem.getString("productId"),
                            productName = jsonOrderItem.getString("productName"),
                            vendorId = jsonOrderItem.getString("vendorId"),
                            quantity = jsonOrderItem.getInt("quantity"),
                            price = jsonOrderItem.getDouble("price"),
                            status = jsonOrderItem.getString("status"),
                            createdDate = jsonOrderItem.getString("createdDate"),
                            updatedDate = jsonOrderItem.getString("updatedDate")
                        )
                        orderItems.add(orderItem)
                    }

                    // Create Order object with orderItems
                    val order = Order(
                        orderId = jsonOrder.getString("orderId"),
                        customerId = jsonOrder.getString("customerId"),
                        totalPrice = jsonOrder.getDouble("totalPrice"),
                        status = jsonOrder.getString("status"),
                        note = jsonOrder.getString("note"),
                        createdDate = jsonOrder.getString("createdDate"),
                        updatedDate = jsonOrder.getString("updatedDate"),
                        orderItems = orderItems // Add order items here
                    )
                    orders.add(order)
                }

                // Update UI on the main thread
                runOnUiThread {
                    orderList = orders
                    orderAdapter = OrderAdapter(this, orderList) { order ->
                        // Navigate to order detail when an order is clicked
                        val intent = Intent(this, OrderDetailActivity::class.java)
                        intent.putExtra("orderId", order.orderId)
                        intent.putExtra("orderItems", order.orderItems as Serializable) // Pass orderItems as Serializable
                        startActivity(intent)
                    }
                    binding.recyclerViewOrders.adapter = orderAdapter
                }
            } else {
                Log.e("OrderListActivity", "Failed to fetch orders")
            }
        }
    }
}
