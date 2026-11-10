package com.beverlly.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beverlly.app.MainActivity
import com.beverlly.app.data.AuthRepository
import com.beverlly.app.databinding.ActivityRegisterBinding
import com.beverlly.app.model.UserProfile
import com.beverlly.app.ui.checkout.CheckoutActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isCheckoutFlow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isCheckoutFlow = intent.getBooleanExtra("EXTRA_REQUIRE_AUTH_FOR_CHECKOUT", false)

        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userProfile = UserProfile(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                address = address,
                authProvider = "Email"
            )

            AuthRepository.signUpWithEmail(
                profile = userProfile,
                pass = password,
                onSuccess = {
                    Toast.makeText(this, "Account created! Welcome to BEVERLLY 🎉", Toast.LENGTH_SHORT).show()
                    if (isCheckoutFlow) {
                        startActivity(Intent(this, CheckoutActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                },
                onError = { err ->
                    Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvLogin.setOnClickListener { finish() }
    }
}
