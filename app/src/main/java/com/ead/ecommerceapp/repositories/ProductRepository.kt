package com.ead.ecommerceapp.repositories

import com.ead.ecommerceapp.models.Product
import org.json.JSONArray

object ProductRepository {
    fun getProducts(callback: (List<Product>?) -> Unit) {
        HttpRepository.getRequest("products") { response ->
            response?.let {
                val productList = mutableListOf<Product>()
                val jsonArray = JSONArray(it)

                for (i in 0 until jsonArray.length()) {
                    val jsonProduct = jsonArray.getJSONObject(i)
                    productList.add(
                        Product(
                            id = jsonProduct.getString("id"),
                            name = jsonProduct.getString("name"),
                            description = jsonProduct.getString("description"),
                            price = jsonProduct.getDouble("price"),
                            imageUrl = jsonProduct.getString("imageUrl")
                        )
                    )
                }
                callback(productList)
            } ?: callback(null)
        }
    }
}