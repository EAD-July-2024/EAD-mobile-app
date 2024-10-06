package com.ead.ecommerceapp.models

data class Order(
    val orderId: String,
    val customerId: String,
    val totalPrice: Double,
    val status: String,
    val note: String,
    val createdDate: String,
    val updatedDate: String,
    val orderItems: List<OrderItem> // Include list of OrderItems
)
