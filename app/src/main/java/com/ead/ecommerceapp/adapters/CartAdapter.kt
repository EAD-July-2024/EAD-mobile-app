package com.ead.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ead.ecommerceapp.databinding.ItemCartBinding
import com.ead.ecommerceapp.models.CartItem
import com.ead.ecommerceapp.repositories.CartRepository

class CartAdapter(private val context: Context, private val cartItems: MutableList<CartItem>, private val updateTotalPrice: () -> Unit) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var onItemRemovedListener: ((List<CartItem>) -> Unit)? = null

    fun setOnItemRemovedListener(listener: (List<CartItem>) -> Unit) {
        onItemRemovedListener = listener
    }

    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.binding.productName.text = cartItem.product.name
        holder.binding.productPrice.text = "$${cartItem.product.price}"
        holder.binding.quantityText.text = cartItem.quantity.toString()

        // Add quantity
        holder.binding.addQuantityButton.setOnClickListener {
            cartItem.quantity++
            holder.binding.quantityText.text = cartItem.quantity.toString()
            CartRepository.updateCart(cartItems, context)  // Update cart in DB
            updateTotalPrice()  // Update total price instantly
        }

        // Remove quantity
        holder.binding.removeQuantityButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                holder.binding.quantityText.text = cartItem.quantity.toString()
                CartRepository.updateCart(cartItems, context)  // Update cart in DB
                updateTotalPrice()  // Update total price instantly
            }
        }

        // Remove item from cart
        holder.binding.removeItemButton.setOnClickListener {
            cartItems.removeAt(position)
            CartRepository.removeCartItem(cartItem, context)  // Remove from DB
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            onItemRemovedListener?.invoke(cartItems)  // Notify listener
            updateTotalPrice()  // Update total price after removing the item
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
