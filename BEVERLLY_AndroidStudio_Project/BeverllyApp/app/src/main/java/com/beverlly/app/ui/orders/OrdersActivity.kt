package com.beverlly.app.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beverlly.app.R
import com.beverlly.app.data.CartRepository
import com.beverlly.app.databinding.ActivityOrdersBinding
import com.beverlly.app.model.Order

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val orders = CartRepository.orders.value ?: emptyList()
        val adapter = OrderAdapter(orders)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }
}

class OrderAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRef: TextView = view.findViewById(R.id.tvOrderRef)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvItems: TextView = view.findViewById(R.id.tvItems)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvRef.text = "Order #${order.id}"
        holder.tvDate.text = order.date
        holder.tvItems.text = order.items.joinToString(", ") {
            "${it.product.name} ×${it.quantity}"
        }
        holder.tvTotal.text = "R ${order.total.toInt()}"
        holder.tvStatus.text = order.status
        holder.tvStatus.setBackgroundResource(
            when (order.status) {
                "Delivered" -> R.drawable.bg_status_delivered
                "Processing" -> R.drawable.bg_status_processing
                "Shipped" -> R.drawable.bg_status_shipped
                else -> R.drawable.bg_status_pending
            }
        )
    }

    override fun getItemCount() = orders.size
}
