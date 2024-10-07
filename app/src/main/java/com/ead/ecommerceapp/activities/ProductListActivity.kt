package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.ecommerceapp.R
import com.ead.ecommerceapp.adapters.CategoryAdapter
import com.ead.ecommerceapp.adapters.ProductAdapter
import com.ead.ecommerceapp.databinding.ActivityProductListBinding
import com.ead.ecommerceapp.models.Product
import com.ead.ecommerceapp.repositories.ProductRepository
import com.ead.ecommerceapp.utils.SessionManager
import com.google.android.material.navigation.NavigationView

class ProductListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var productList: List<Product> = listOf()
    private var filteredProductList: List<Product> = listOf() // Filtered product list
    private lateinit var binding: ActivityProductListBinding // ViewBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var toggle: ActionBarDrawerToggle
    private var categories: MutableList<String> = mutableListOf("All")  // Start with "All" category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Set up the toolbar as the action bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the navigation drawer
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, toolbar, R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set the icon to a three horizontal lines (hamburger icon)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)  // Set your custom icon here
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener(this)

        // Fetch products and set up RecyclerView
        ProductRepository.getProducts { products ->
            products?.let {
                productList = it
                filteredProductList = it // Initialize with all products

                // Extract distinct categories from the products
                categories.addAll(productList.map { product -> product.categoryName }.distinct())

                // Set up category RecyclerView
                setupCategoryRecyclerView()

                // Set up product RecyclerView
                productAdapter = ProductAdapter(this, filteredProductList)
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
                filterProductsBySearch(newText ?: "")
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate menu and update the navigation drawer dynamically
        // menuInflater.inflate(R.menu.nav_menu, menu)
        updateNavigationMenuItems()
        return true
    }

    // Update menu items based on login state
    private fun updateNavigationMenuItems() {
        val menu = binding.navView.menu
        val logoutItem = menu.findItem(R.id.nav_logout)
        val logoutItem2 = menu.findItem(R.id.nav_orders)

        // Show or hide logout based on login status
        if (sessionManager.isLoggedIn()) {
            logoutItem.isVisible = true
        } else {
            logoutItem.isVisible = false
        }

        // Show or hide order
        if (sessionManager.isLoggedIn()) {
            logoutItem2.isVisible = true
        } else {
            logoutItem2.isVisible = false
        }
    }

    // Function to set up the category RecyclerView
    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter(this, categories) { selectedCategory ->
            filterProductsByCategory(selectedCategory)
        }
        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter
    }

    // Function to filter products by selected category
    private fun filterProductsByCategory(category: String) {
        filteredProductList = if (category == "All") {
            productList
        } else {
            productList.filter { it.categoryName == category }
        }
        productAdapter = ProductAdapter(this, filteredProductList)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    // Function to filter products based on search query
    private fun filterProductsBySearch(query: String) {
        val searchedList = filteredProductList.filter { it.name.contains(query, ignoreCase = true) }
        productAdapter = ProductAdapter(this, searchedList)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_orders -> {
                startActivity(Intent(this, OrderListActivity::class.java))  // Navigate to order list
            }
            R.id.nav_account -> {
                if (sessionManager.isLoggedIn()) {
                    startActivity(Intent(this, AccountActivity::class.java))  // Navigate to account page
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))  // Navigate to login page
                }
            }
            R.id.nav_cart -> {
                startActivity(Intent(this, CartActivity::class.java))  // Navigate to cart
            }
            R.id.nav_logout -> {
                sessionManager.logout()
                startActivity(Intent(this, LoginActivity::class.java))  // Logout and navigate to login page
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
