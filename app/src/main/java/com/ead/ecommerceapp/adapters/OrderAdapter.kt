package com.ead.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ead.ecommerceapp.databinding.ItemOrderBinding
import com.ead.ecommerceapp.models.Order

class OrderAdapter(
    private val context: Context,
    private val orders: List<Order>,
    private val onItemClicked: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.orderIdText.text = "Order ID: ${order.orderId}"
        holder.binding.orderTotalPriceText.text = "Total: Rs.${order.totalPrice}0"
        holder.binding.orderStatusText.text = "Status: ${order.status}"

        holder.itemView.setOnClickListener {
            onItemClicked(order)  // Handle click event
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
