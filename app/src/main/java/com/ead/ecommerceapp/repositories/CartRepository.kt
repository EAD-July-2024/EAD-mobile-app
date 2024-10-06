package com.ead.ecommerceapp.repositories

import android.content.Context
import com.ead.ecommerceapp.models.CartItem
import com.ead.ecommerceapp.utils.DatabaseHelper

object CartRepository {

    // Add an item to the cart (check if it already exists first)
    fun addToCart(cartItem: CartItem, context: Context) {
        val dbHelper = DatabaseHelper(context)
        val existingCartItems = dbHelper.getCartItems()

        // Check if the product is already in the cart
        val existingItem = existingCartItems.find { it.product.id == cartItem.product.id }

        if (existingItem != null) {
            // If the item exists, update the quantity
            existingItem.quantity += cartItem.quantity
            dbHelper.updateCartItem(existingItem)
        } else {
            // If it's a new item, insert it
            dbHelper.insertCartItem(cartItem)
        }
    }

    // Get all cart items from the SQLite database
    fun getCartItems(context: Context): List<CartItem> {
        val dbHelper = DatabaseHelper(context)
        return dbHelper.getCartItems()
    }

    // Update cart items in the SQLite database
    fun updateCart(cartItems: List<CartItem>, context: Context) {
        val dbHelper = DatabaseHelper(context)
        dbHelper.updateCartItems(cartItems)
    }

    // Remove item from the cart
    fun removeCartItem(cartItem: CartItem, context: Context) {
        val dbHelper = DatabaseHelper(context)
        dbHelper.removeCartItem(cartItem.product.id)
    }

    // Calculate total price of the items in the cart
    fun calculateTotalPrice(context: Context): Double {
        val cartItems = getCartItems(context)
        return cartItems.sumOf { it.product.price.toDouble() * it.quantity }
    }
}
