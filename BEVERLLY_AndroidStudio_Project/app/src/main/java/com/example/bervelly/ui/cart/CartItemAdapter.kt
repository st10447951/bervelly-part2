package com.example.bervelly.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bervelly.R
import com.example.bervelly.model.CartItem
import com.google.android.material.button.MaterialButton

class CartItemAdapter(
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    private var items: MutableList<CartItem> = mutableListOf()

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.ivProduct)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvSizeQty: TextView = view.findViewById(R.id.tvSizeQty)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnRemove: MaterialButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.ivProduct.setImageResource(item.product.imageResId)
        holder.tvName.text = item.product.name
        holder.tvSizeQty.text = "Size: ${item.size}  ·  Qty: ${item.quantity}"
        holder.tvPrice.text = "R ${(item.product.price * item.quantity).toInt()}"
        holder.btnRemove.setOnClickListener { onRemove(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: MutableList<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
