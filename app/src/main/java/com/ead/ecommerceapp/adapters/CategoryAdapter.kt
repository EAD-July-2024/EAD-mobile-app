package com.ead.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ead.ecommerceapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val context: Context,
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit // Callback for when a category is selected
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategory = "All"

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryName.text = category

        // Highlight selected category
        holder.binding.categoryName.isSelected = category == selectedCategory

        holder.itemView.setOnClickListener {
            selectedCategory = category
            notifyDataSetChanged() // Refresh category selection
            onCategorySelected(category) // Pass the selected category
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
