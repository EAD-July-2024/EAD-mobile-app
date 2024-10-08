package com.ead.ecommerceapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ead.ecommerceapp.models.CartItem
import com.ead.ecommerceapp.models.Product

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cart_db"
        private const val DATABASE_VERSION = 2  // Incremented version

        // Table and columns
        private const val TABLE_CART = "cart"
        private const val COLUMN_PRODUCT_ID = "product_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_CATEGORY_ID = "categoryId"
        private const val COLUMN_CATEGORY_NAME = "categoryName"
        private const val COLUMN_VENDOR_ID = "vendorId"
        private const val COLUMN_VENDOR_NAME = "vendorName"
        private const val COLUMN_IMAGE_URL = "image_url"  // New column for image URL
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createCartTable = """
            CREATE TABLE $TABLE_CART (
                $COLUMN_PRODUCT_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_PRICE TEXT,
                $COLUMN_QUANTITY INTEGER,
                $COLUMN_CATEGORY_ID TEXT,
                $COLUMN_CATEGORY_NAME TEXT,
                $COLUMN_VENDOR_ID TEXT,
                $COLUMN_VENDOR_NAME TEXT,
                $COLUMN_IMAGE_URL TEXT  
            )
        """.trimIndent()
        db?.execSQL(createCartTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add the new column if upgrading from version 1 to 2
            db?.execSQL("ALTER TABLE $TABLE_CART ADD COLUMN $COLUMN_IMAGE_URL TEXT")
        }
    }

    // Insert cart item into the database
    fun insertCartItem(cartItem: CartItem) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_ID, cartItem.product.id)
            put(COLUMN_NAME, cartItem.product.name)
            put(COLUMN_PRICE, cartItem.product.price)
            put(COLUMN_QUANTITY, cartItem.quantity)
            put(COLUMN_CATEGORY_ID, cartItem.categoryId)
            put(COLUMN_CATEGORY_NAME, cartItem.categoryName)
            put(COLUMN_VENDOR_ID, cartItem.vendorId)
            put(COLUMN_VENDOR_NAME, cartItem.vendorName)
            put(COLUMN_IMAGE_URL, cartItem.product.imageUrls.firstOrNull())  // Store the first image URL
        }
        db.insert(TABLE_CART, null, values)
    }

    // Update existing cart item (if the quantity or other fields are changed)
    fun updateCartItem(cartItem: CartItem) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUANTITY, cartItem.quantity)  // Only update quantity
            put(COLUMN_IMAGE_URL, cartItem.product.imageUrls.firstOrNull())  // Update image URL if needed
        }
        db.update(TABLE_CART, values, "$COLUMN_PRODUCT_ID = ?", arrayOf(cartItem.product.id))
    }

    // Retrieve all cart items from the database
    fun getCartItems(): List<CartItem> {
        val db = readableDatabase
        val cursor = db.query(TABLE_CART, null, null, null, null, null, null)
        val cartItems = mutableListOf<CartItem>()

        while (cursor.moveToNext()) {
            val product = Product(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                description = "",  // Not storing description
                price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                quantity = "",
                imageUrls = listOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))),  // Retrieve image URL
                categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                categoryId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                vendorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENDOR_NAME)),
                vendorId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENDOR_ID)),
            )
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            val categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME))
            val categoryId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
            val vendorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENDOR_NAME))
            val vendorId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENDOR_ID))
            cartItems.add(CartItem(product, quantity, categoryId, categoryName, vendorId, vendorName))
        }

        cursor.close()
        return cartItems
    }

    // Remove an item from the cart
    fun removeCartItem(productId: String) {
        val db = writableDatabase
        db.delete(TABLE_CART, "$COLUMN_PRODUCT_ID = ?", arrayOf(productId))
    }

    // Clear the cart and update the items
    fun updateCartItems(cartItems: List<CartItem>) {
        val db = writableDatabase
        db.delete(TABLE_CART, null, null)  // Clear current cart data
        cartItems.forEach { insertCartItem(it) }
    }
}
