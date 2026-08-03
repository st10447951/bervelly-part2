package com.beverlly.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beverlly.app.data.CartRepository
import com.beverlly.app.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val total = CartRepository.grandTotal()
        binding.tvOrderTotal.text = "R ${total.toInt()}"

        // Pre-fill order summary
        val items = CartRepository.cartItems.value ?: emptyList()
        val summary = items.joinToString("\n") {
            "${it.product.name} (${it.size}) ×${it.quantity}  —  R ${(it.product.price * it.quantity).toInt()}"
        }
        binding.tvOrderSummary.text = summary

        // Payment method selection
        binding.rgPayment.setOnCheckedChangeListener { _, checkedId ->
            binding.cardFields.visibility =
                if (checkedId == binding.rbCard.id) android.view.View.VISIBLE
                else android.view.View.GONE
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnPlaceOrder.setOnClickListener {
            val address = binding.etAddress.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter a delivery address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val order = CartRepository.placeOrder(address)
            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("ORDER_ID", order.id)
            intent.putExtra("ORDER_TOTAL", order.total)
            startActivity(intent)
            finish()
        }
    }
}
