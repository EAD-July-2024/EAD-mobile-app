package com.ead.ecommerceapp.repositories

import android.os.Handler
import android.os.Looper
import com.ead.ecommerceapp.models.Product
import org.json.JSONArray

object ProductRepository {
    fun getProducts(callback: (List<Product>?) -> Unit) {
        HttpRepository.getRequest("product") { response ->
            response?.let {
                val productList = mutableListOf<Product>()
                val jsonArray = JSONArray(it)

                for (i in 0 until jsonArray.length()) {
                    val jsonProduct = jsonArray.getJSONObject(i)

                    val imageUrls = mutableListOf<String>()
                    val jsonImageUrls = jsonProduct.getJSONArray("imageUrls")
                    for (j in 0 until jsonImageUrls.length()) {
                        imageUrls.add(jsonImageUrls.getString(j))
                    }

                    productList.add(
                        Product(
                            id = jsonProduct.getString("productId"),
                            name = jsonProduct.getString("name"),
                            description = jsonProduct.getString("description"),
                            price = jsonProduct.getString("price"),
                            imageUrls = imageUrls,
                            categoryName = jsonProduct.getString("categoryName"),
                            categoryId = jsonProduct.getString("categoryId"),
                            vendorName = jsonProduct.getString("vendorName"),
                            vendorId = jsonProduct.getString("vendorId"),
                            quantity = jsonProduct.getString("quantity"),
                            rating = jsonProduct.getDouble("vendorRating")
                        )
                    )
                }

                // Run the callback on the main/UI thread
                Handler(Looper.getMainLooper()).post {
                    callback(productList)
                }
            } ?: callback(null)
        }
    }
}
