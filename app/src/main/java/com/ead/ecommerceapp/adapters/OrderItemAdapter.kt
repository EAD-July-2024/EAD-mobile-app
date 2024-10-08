package com.ead.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ead.ecommerceapp.databinding.ItemOrderItemBinding
import com.ead.ecommerceapp.models.OrderItem

class OrderItemAdapter(private val context: Context, private val orderItems: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(val binding: ItemOrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        holder.binding.productNameText.text = orderItem.productName
        holder.binding.productQuantityText.text = "Quantity: ${orderItem.quantity}"
        holder.binding.productPriceText.text = "Price: $${orderItem.price}"

        // Load image using Glide
        Glide.with(context)
            .load(orderItem.imageUrl)
            .into(holder.binding.productImageView)  // New ImageView for product image
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }
}
