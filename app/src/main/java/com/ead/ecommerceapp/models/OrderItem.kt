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
    val updatedDate: String,
    val imageUrl: String  // New field for the product image URL
) : Serializable
