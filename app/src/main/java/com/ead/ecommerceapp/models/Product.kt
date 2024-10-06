package com.ead.ecommerceapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val categoryId: String,
    val categoryName: String,
    val price: String,  // Change to String to match the API response
    val imageUrls: List<String>,  // List of image URLs
    val quantity: String,
    val vendorId: String,
    val vendorName: String
) : Parcelable
