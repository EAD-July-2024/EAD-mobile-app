package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.ecommerceapp.adapters.ImageAdapter
import com.ead.ecommerceapp.databinding.ActivityProductDetailBinding
import com.ead.ecommerceapp.models.CartItem
import com.ead.ecommerceapp.models.Product
import com.ead.ecommerceapp.repositories.CartRepository
import com.google.android.material.snackbar.Snackbar

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var currentQuantity: Int = 1  // Default quantity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get product data from intent with backward compatibility
        val product: Product? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("product")
        }

        product?.let {
            binding.productNameDetail.text = it.name
            binding.productDescriptionDetail.text = it.description
            binding.productPriceDetail.text = "$${it.price}"
            binding.quantityText.text = currentQuantity.toString()
            binding.vendorName.text = it.vendorName

            // Set up RecyclerView for images
            binding.productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val imageAdapter = ImageAdapter(this, it.imageUrls)
            binding.productImagesRecyclerView.adapter = imageAdapter

            // Add quantity
            binding.addQuantityButton.setOnClickListener {
                currentQuantity++
                binding.quantityText.text = currentQuantity.toString()
            }

            // Remove quantity (ensure quantity is never below 1)
            binding.removeQuantityButton.setOnClickListener {
                if (currentQuantity > 1) {
                    currentQuantity--
                    binding.quantityText.text = currentQuantity.toString()
                }
            }

            // Add product to cart
            binding.addToCartButton.setOnClickListener {
                val cartItem = CartItem(product, currentQuantity, "", "", "", "")  // Use `product`
                CartRepository.addToCart(cartItem, this)
                Snackbar.make(binding.root, "${product.name} added to cart", Snackbar.LENGTH_LONG).show()
            }

            // Floating Action Button to navigate to Cart Activity
            binding.viewCartFab.setOnClickListener {
                // Open CartActivity
                startActivity(Intent(this, CartActivity::class.java))
            }

            // Navigate to Vendor Detail Page
            binding.vendorName.setOnClickListener {
                val intent = Intent(this, VendorDetailActivity::class.java)
                intent.putExtra("vendorId", product.vendorId)
                startActivity(intent)
            }
        }
    }
}
