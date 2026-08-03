package com.example.bervelly.ui.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bervelly.R
import com.example.bervelly.data.CartRepository
import com.example.bervelly.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val layoutId: Int = R.layout.item_product_grid,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.ivProduct)
        val ivWishlist: ImageView = view.findViewById(R.id.ivWishlist)
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvNewBadge: TextView = view.findViewById(R.id.tvNewBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.tvName.text = product.name
        holder.tvCategory.text = product.category
        holder.tvPrice.text = "R ${product.price.toInt()}"
        holder.tvNewBadge.visibility = if (product.isNew) View.VISIBLE else View.GONE
        holder.ivProduct.setImageResource(product.imageResId)

        // Wishlist toggle
        updateWishlistIcon(holder.ivWishlist, product.id)
        holder.ivWishlist.setOnClickListener {
            CartRepository.toggleWishlist(product)
            updateWishlistIcon(holder.ivWishlist, product.id)
        }

        holder.itemView.setOnClickListener { onProductClick(product) }
    }

    private fun updateWishlistIcon(iv: ImageView, productId: Int) {
        if (CartRepository.isInWishlist(productId)) {
            iv.setImageResource(R.drawable.ic_heart_filled)
        } else {
            iv.setImageResource(R.drawable.ic_heart_outline)
        }
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
