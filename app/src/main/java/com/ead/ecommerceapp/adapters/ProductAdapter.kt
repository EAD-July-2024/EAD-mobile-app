package com.ead.ecommerceapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ead.ecommerceapp.activities.ProductDetailActivity
import com.ead.ecommerceapp.databinding.ItemProductBinding
import com.ead.ecommerceapp.models.Product

class ProductAdapter(private val context: Context, private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = "Rs.${product.price}"

        // Load the first image from the imageUrls list
        if (product.imageUrls.isNotEmpty()) {
            Glide.with(context).load(product.imageUrls[0]).into(holder.binding.productImage)
        }

        // Click listener to navigate to ProductDetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("product", product)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}
