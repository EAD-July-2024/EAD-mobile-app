package com.ead.ecommerceapp.activities

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ead.ecommerceapp.R
import com.ead.ecommerceapp.adapters.ProductAdapter
import com.ead.ecommerceapp.databinding.ActivityProductListBinding
import com.ead.ecommerceapp.models.Product
import com.ead.ecommerceapp.repositories.ProductRepository

class ProductListActivity : AppCompatActivity() {

    private lateinit var productAdapter: ProductAdapter
    private var productList: List<Product> = listOf()
    private lateinit var binding: ActivityProductListBinding // ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch products and set up RecyclerView
        ProductRepository.getProducts { products ->
            products?.let {
                productList = it
                productAdapter = ProductAdapter(this, productList)
                binding.recyclerViewProducts.layoutManager = GridLayoutManager(this, 2)
                binding.recyclerViewProducts.adapter = productAdapter
            }
        }

        // Search functionality
        binding.productSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = productList.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                productAdapter = ProductAdapter(this@ProductListActivity, filteredList)
                binding.recyclerViewProducts.adapter = productAdapter
                return true
            }
        })
    }
}