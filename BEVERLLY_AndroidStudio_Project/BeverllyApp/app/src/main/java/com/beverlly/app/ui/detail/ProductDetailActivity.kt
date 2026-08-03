package com.beverlly.app.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beverlly.app.R
import com.beverlly.app.data.CartRepository
import com.beverlly.app.data.ProductRepository
import com.beverlly.app.databinding.ActivityProductDetailBinding
import com.beverlly.app.model.Product
import com.google.android.material.chip.Chip

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var product: Product
    private var selectedSize = ""
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        product = ProductRepository.getById(productId) ?: run { finish(); return }

        setupUI()
    }

    private fun setupUI() {
        binding.ivProduct.setImageResource(product.imageResId)
        binding.tvCategory.text = product.category.uppercase()
        binding.tvName.text = product.name
        binding.tvPrice.text = "R ${product.price.toInt()}"
        binding.tvDescription.text = product.description
        binding.tvStock.text = "✓ In Stock (${product.stock} available)"

        // Size chips
        product.sizes.forEach { size ->
            val chip = Chip(this).apply {
                text = size
                isCheckable = true
                setChipBackgroundColorResource(R.color.bev_white)
                setTextColor(getColor(R.color.bev_text))
            }
            binding.cgSizes.addView(chip)
        }
        // Select first size by default
        if (product.sizes.isNotEmpty()) {
            selectedSize = product.sizes[0]
            (binding.cgSizes.getChildAt(0) as? Chip)?.isChecked = true
        }
        binding.cgSizes.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                selectedSize = chip.text.toString()
            }
        }

        // Quantity controls
        updateQtyDisplay()
        binding.btnMinus.setOnClickListener {
            if (quantity > 1) { quantity--; updateQtyDisplay() }
        }
        binding.btnPlus.setOnClickListener {
            if (quantity < product.stock) { quantity++; updateQtyDisplay() }
        }

        // Add to cart
        binding.btnAddToCart.setOnClickListener {
            if (selectedSize.isEmpty()) {
                Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CartRepository.addToCart(product, selectedSize, quantity)
            Toast.makeText(this, "${product.name} added to cart ✓", Toast.LENGTH_SHORT).show()
        }

        // Wishlist
        updateWishlistBtn()
        binding.btnWishlist.setOnClickListener {
            CartRepository.toggleWishlist(product)
            updateWishlistBtn()
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.ivWishlist.setOnClickListener {
            CartRepository.toggleWishlist(product)
            updateWishlistBtn()
        }
    }

    private fun updateQtyDisplay() {
        binding.tvQty.text = quantity.toString()
        val total = product.price * quantity
        binding.btnAddToCart.text = "Add to Cart — R ${total.toInt()}"
    }

    private fun updateWishlistBtn() {
        if (CartRepository.isInWishlist(product.id)) {
            binding.btnWishlist.text = "❤️  Saved to Wishlist"
            binding.ivWishlist.setImageResource(R.drawable.ic_heart_filled)
        } else {
            binding.btnWishlist.text = "🤍  Save to Wishlist"
            binding.ivWishlist.setImageResource(R.drawable.ic_heart_outline)
        }
    }
}
