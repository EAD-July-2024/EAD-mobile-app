package com.ead.ecommerceapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import com.google.android.material.slider.RangeSlider
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding

class ProductListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var productList: List<Product> = listOf()
    private var filteredProductList: List<Product> = listOf() // Filtered product list
    private lateinit var binding: ActivityProductListBinding // ViewBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var toggle: ActionBarDrawerToggle
    private var categories: MutableList<String> = mutableListOf("All")  // Start with "All" category
    private var selectedVendor: String? = null
    private var minPrice: Int = 0
    private var maxPrice: Int = Int.MAX_VALUE
    private var selectedRating: Int? = null  // Store the selected star rating

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
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener(this)

        // Dynamically update navigation drawer items based on login status
        updateNavigationMenu()

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate menu with the search icon and filter icon
        menuInflater.inflate(R.menu.app_bar_menu, menu)

        // Set up the SearchView
        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search Products"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProductsBySearch(newText ?: "")
                return true
            }
        })

        // Reset filters when search is closed
        searchView.setOnCloseListener {
            searchView.onActionViewCollapsed()  // Collapse the SearchView
            productAdapter = ProductAdapter(this, productList)
            binding.recyclerViewProducts.adapter = productAdapter
            true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_filter -> {
                showFilterDialog()  // Show filter dialog when the filter button is clicked
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Show filter options for vendor, price range, and rating
    private fun showFilterDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Filter Options")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        // Vendor selection
        val vendorText = TextView(this)
        vendorText.setPadding(16, 16, 16, 16)
        vendorText.text = "Select Vendor"
        layout.addView(vendorText)

        val vendorList = productList.map { it.vendorName }.distinct().sorted()
        val scrollView = ScrollView(this)
        val vendorLayout = LinearLayout(this)
        vendorLayout.orientation = LinearLayout.VERTICAL

        for (vendor in vendorList) {
            val vendorOption = TextView(this)
            vendorOption.text = vendor
            vendorOption.setPadding(32, 16, 16, 16)
            vendorOption.setOnClickListener {
                selectedVendor = vendor
                Toast.makeText(this, "Selected Vendor: $vendor", Toast.LENGTH_SHORT).show()
            }
            vendorLayout.addView(vendorOption)
        }

        scrollView.addView(vendorLayout)
        layout.addView(scrollView)

        // Price Range selection using RangeSlider
        val priceText = TextView(this)
        priceText.setPadding(16, 16, 16, 16)
        priceText.text = "Select Price Range"
        layout.addView(priceText)

        val rangeSlider = RangeSlider(this)
        rangeSlider.valueFrom = 0f
        rangeSlider.valueTo = 10000f
        rangeSlider.values = listOf(0f, 10000f)
        rangeSlider.stepSize = 100f

        rangeSlider.addOnChangeListener { slider, _, _ ->
            minPrice = slider.values[0].toInt()
            maxPrice = slider.values[1].toInt()
        }

        layout.addView(rangeSlider)

        // Rating selection using RatingBar
        val ratingText = TextView(this)
        ratingText.setPadding(16, 16, 16, 16)
        ratingText.text = "Select Minimum Rating"
        layout.addView(ratingText)

        val ratingBar = RatingBar(this)
        ratingBar.setPadding(10, 16, 0, 16)
        ratingBar.numStars = 5
        ratingBar.stepSize = 1.0f  // Allows selecting whole stars
        ratingBar.rating = 0f  // Default to 0 stars
        ratingBar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layout.addView(ratingBar)

        // Listen for rating change
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            selectedRating = rating.toInt()
            Toast.makeText(this, "Selected Rating: $selectedRating Stars", Toast.LENGTH_SHORT).show()
        }

        dialogBuilder.setView(layout)

        dialogBuilder.setPositiveButton("Apply") { _, _ ->
            filterProductsByVendorPriceAndRating()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.show()
    }

    // Function to filter products by vendor and price range
    private fun filterProductsByVendorAndPrice() {
        filteredProductList = productList.filter { product ->
            val isWithinPriceRange = product.price.toInt() in minPrice..maxPrice
            val isVendorMatched = selectedVendor?.let { product.vendorName == it } ?: true
            isWithinPriceRange && isVendorMatched
        }
        productAdapter = ProductAdapter(this, filteredProductList)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    // Function to filter products by search query
    private fun filterProductsBySearch(query: String) {
        val searchedList = filteredProductList.filter { it.name.contains(query, ignoreCase = true) }
        productAdapter = ProductAdapter(this, searchedList)
        binding.recyclerViewProducts.adapter = productAdapter
    }

    // Function to dynamically update the navigation menu
    private fun updateNavigationMenu() {
        val menu = binding.navView.menu
        val isLoggedIn = sessionManager.isLoggedIn()

        // Show or hide orders and logout menu items based on login status
        menu.findItem(R.id.nav_orders).isVisible = isLoggedIn
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
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

    // Function to filter products by vendor, price, and rating
    private fun filterProductsByVendorPriceAndRating() {
        filteredProductList = productList.filter { product ->
            val isWithinPriceRange = product.price.toInt() in minPrice..maxPrice
            val isVendorMatched = selectedVendor?.let { product.vendorName == it } ?: true
            val isRatingMatched = product.rating in (selectedRating?.toDouble() ?: 0.0)..(selectedRating?.toDouble()?: 4.1 + 0.9)
            isWithinPriceRange && isVendorMatched && isRatingMatched
        }
        productAdapter = ProductAdapter(this, filteredProductList)
        binding.recyclerViewProducts.adapter = productAdapter
    }

}
