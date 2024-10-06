package com.ead.ecommerceapp.models

data class CartItem(
    val product: Product,
    var quantity: Int,
    val categoryId: String,
    val categoryName: String,
    val vendorId: String,
    val vendorName: String
)
