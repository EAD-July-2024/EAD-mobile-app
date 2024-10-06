package com.ead.ecommerceapp.models

import java.io.Serializable

data class OrderItem(
    val orderId: String,
    val productId: String,
    val productName: String,
    val vendorId: String,
    val quantity: Int,
    val price: Double,
    val status: String,
    val createdDate: String,
    val updatedDate: String
) : Serializable // Ensure it implements Serializable
