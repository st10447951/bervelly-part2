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
import com.beverlly.app.data.AuthRepository
import com.beverlly.app.data.CartRepository
import com.beverlly.app.data.OrderRepository
import com.beverlly.app.databinding.ActivityOrdersBinding
import com.beverlly.app.model.Order

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding
    private val ordersList = mutableListOf<Order>()
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        adapter = OrderAdapter(ordersList)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter

        val user = AuthRepository.getCurrentUser()
        if (user != null && user.uid.isNotEmpty()) {
            OrderRepository.fetchUserOrders(user.uid) { remoteOrders ->
                ordersList.clear()
                ordersList.addAll(remoteOrders)
                adapter.notifyDataSetChanged()
            }
        }
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
        holder.tvDate.text = order.date.ifEmpty { "Recent Order" }
        val itemsSummary = if (order.items.isNotEmpty()) {
            order.items.joinToString("\\n") {
                "${it.product.name} • Size ${it.size} • Qty ${it.quantity} • R ${(it.product.price * it.quantity).toInt()}"
            }
        } else {
            "No item details were returned for this order."
        }
        holder.tvItems.text = itemsSummary
        holder.tvTotal.text = "R ${order.total.toInt()}"
        holder.tvStatus.text = order.status
        holder.tvStatus.setBackgroundResource(
            when (order.status) {
                "Delivered" -> R.drawable.bg_status_delivered
                "Processing", "Paid" -> R.drawable.bg_status_processing
                "Shipped" -> R.drawable.bg_status_shipped
                else -> R.drawable.bg_status_pending
            }
        )
    }

    override fun getItemCount() = orders.size
}
