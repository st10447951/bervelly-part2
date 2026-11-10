package com.beverlly.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.beverlly.app.MainActivity
import com.beverlly.app.R
import com.beverlly.app.data.AuthRepository
import com.beverlly.app.databinding.ActivityLoginBinding
import com.beverlly.app.ui.checkout.CheckoutActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isCheckoutFlow = false

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken ?: throw IllegalStateException("Google did not return an ID token. Check your OAuth configuration.")
            val email = account?.email ?: ""
            val givenName = account?.givenName ?: "Google"
            val familyName = account?.familyName ?: "User"

            AuthRepository.firebaseAuthWithGoogle(
                idToken = idToken,
                email = email,
                givenName = givenName,
                familyName = familyName,
                onSuccess = { user ->
                    Toast.makeText(this, "Signed in as ${user.email} 👋", Toast.LENGTH_SHORT).show()
                    onAuthSuccess()
                },
                onError = { err ->
                    Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
                }
            )
        } catch (e: ApiException) {
            val errorMsg = when (e.statusCode) {
                10 -> "Google Sign-In developer error (10). Please verify SHA-1 key is added in Firebase Console."
                7 -> "Network error during Google Sign-In. Check your connection."
                12500 -> "Google Sign-In failed (12500). Please update Google Play Services."
                12501 -> null // User cancelled sign-in prompt explicitly
                else -> e.localizedMessage ?: "Google sign-in failed (code: ${e.statusCode})"
            }
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.localizedMessage ?: "Google sign-in failed.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isCheckoutFlow = intent.getBooleanExtra("EXTRA_REQUIRE_AUTH_FOR_CHECKOUT", false)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AuthRepository.signInWithEmail(
                email = email,
                pass = password,
                onSuccess = { profile ->
                    Toast.makeText(this, "Welcome back, ${profile.firstName}! 👋", Toast.LENGTH_SHORT).show()
                    onAuthSuccess()
                },
                onError = { err ->
                    Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnGoogleSso.setOnClickListener {
            try {
                val webClientId = getString(R.string.default_web_client_id)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(this, gso)
                googleSignInLauncher.launch(client.signInIntent)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    e.localizedMessage ?: "Google sign-in could not be started.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("EXTRA_REQUIRE_AUTH_FOR_CHECKOUT", isCheckoutFlow)
            startActivity(intent)
        }
    }

    private fun onAuthSuccess() {
        val email = AuthRepository.getCurrentUser()?.email ?: binding.etEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            com.beverlly.app.util.EmailNotificationHelper.sendLoginNotificationEmail(this, email)
        }
        if (isCheckoutFlow) {
            startActivity(Intent(this, CheckoutActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
