package com.ead.ecommerceapp.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ead.ecommerceapp.databinding.ActivityProductDetailBinding
import com.ead.ecommerceapp.models.Product

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

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

            // Load image with Glide
            Glide.with(this).load(it.imageUrl).into(binding.productImageDetail)
        }
    }
}