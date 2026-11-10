
package com.beverlly.app.ui.checkout

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beverlly.app.data.AuthRepository
import com.beverlly.app.data.CartRepository
import com.beverlly.app.data.OrderRepository
import com.beverlly.app.databinding.ActivityCheckoutBinding
import com.beverlly.app.ui.auth.LoginActivity
import com.beverlly.app.util.OrderEmailService

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure user is authenticated before checking out
        if (!AuthRepository.isLoggedIn()) {
            Toast.makeText(
                this,
                "Please sign in or register to complete your order",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("EXTRA_REQUIRE_AUTH_FOR_CHECKOUT", true)
            startActivity(intent)
            finish()
            return
        }

        // Pre-fill user profile information
        val user = AuthRepository.getCurrentUser()

        if (user != null) {
            if (user.firstName.isNotEmpty()) {
                binding.etFirstName.setText(user.firstName)
            }

            if (user.lastName.isNotEmpty()) {
                binding.etLastName.setText(user.lastName)
            }

            if (user.address.isNotEmpty()) {
                binding.etAddress.setText(user.address)
            }
        }

        // Display order total
        val total = CartRepository.grandTotal()
        binding.tvOrderTotal.text = "R ${total.toInt()}"

        // Display order summary
        val items = CartRepository.cartItems.value ?: emptyList()

        val summary = items.joinToString("\n") {
            "${it.product.name} (${it.size}) ×${it.quantity}  —  R ${(it.product.price * it.quantity).toInt()}"
        }

        binding.tvOrderSummary.text = summary

        // Payment method selection
        binding.rgPayment.setOnCheckedChangeListener { _, checkedId ->
            binding.cardFields.visibility =
                if (checkedId == binding.rbCard.id) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Place order button
        binding.btnPlaceOrder.setOnClickListener {

            val address = binding.etAddress.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val postal = binding.etPostal.text.toString().trim()

            // Validate delivery address
            if (address.isEmpty() || city.isEmpty() || postal.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please complete your delivery address",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val fullAddress = "$address, $city, $postal"

            // Validate selected payment method
            val selectedPaymentId = binding.rgPayment.checkedRadioButtonId

            val paymentMethodStr: String

            if (selectedPaymentId == binding.rbCard.id) {

                paymentMethodStr = "Credit / Debit Card"

                val cardNumber = binding.etCardNumber.text.toString()
                    .replace(" ", "")
                    .trim()

                val expiry = binding.etExpiry.text.toString().trim()
                val cvv = binding.etCvv.text.toString().trim()

                if (cardNumber.length < 13) {
                    Toast.makeText(
                        this,
                        "Please enter a valid card number",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (!expiry.contains("/") || expiry.length < 4) {
                    Toast.makeText(
                        this,
                        "Please enter a valid expiry date (MM/YY)",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (cvv.length < 3) {
                    Toast.makeText(
                        this,
                        "Please enter a valid 3-digit CVV",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

            } else if (selectedPaymentId == binding.rbEFT.id) {

                paymentMethodStr = "EFT Bank Transfer"

            } else {

                paymentMethodStr = "PayFast"
            }

            // Simulated payment processing
            val progress = ProgressDialog(this).apply {
                setMessage("Processing payment via $paymentMethodStr...")
                setCancelable(false)
                show()
            }

            Handler(Looper.getMainLooper()).postDelayed({

                progress.dismiss()

                // Create order
                val order = CartRepository.placeOrder(fullAddress)
                order.status = "Paid"

                // Get authenticated customer details
                val uid = AuthRepository.getRegisteredUserUid()
                val email = AuthRepository.getRegisteredEmail()

                // Save order to Firestore before sending email
                OrderRepository.saveOrder(order, uid) { saved ->

                    if (!saved) {
                        Toast.makeText(
                            this@CheckoutActivity,
                            "Your order could not be saved. Please check your connection. Contact support before attempting payment again.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@saveOrder
                    }

                    // Send actual order confirmation email
                    OrderEmailService.sendOrderConfirmation(order.id) { emailSent, message ->

                        if (emailSent) {
                            Toast.makeText(
                                this@CheckoutActivity,
                                "Order saved successfully! Confirmation email sent to $email",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@CheckoutActivity,
                                "Order saved, but the email was not sent: $message",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        // Open order confirmation screen
                        val intent = Intent(
                            this@CheckoutActivity,
                            OrderConfirmationActivity::class.java
                        )

                        intent.putExtra("ORDER_ID", order.id)
                        intent.putExtra("ORDER_TOTAL", order.total)
                        intent.putExtra("ORDER_EMAIL", email)

                        startActivity(intent)
                        finish()
                    }
                }

            }, 1500)
        }
    }
}
