package com.beverlly.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beverlly.app.MainActivity
import com.beverlly.app.databinding.ActivityOrderConfirmationBinding

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("ORDER_ID") ?: "BEV-0001"
        val total = intent.getDoubleExtra("ORDER_TOTAL", 0.0)

        binding.tvOrderRef.text = "Order #$orderId"
        binding.tvTotalPaid.text = "R ${total.toInt()}"

        binding.btnTrackOrder.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("NAVIGATE_TO", "orders")
            startActivity(intent)
            finishAffinity()
        }

        binding.btnContinueShopping.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}
