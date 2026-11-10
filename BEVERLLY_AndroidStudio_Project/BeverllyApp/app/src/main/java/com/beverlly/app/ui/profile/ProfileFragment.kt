package com.beverlly.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.beverlly.app.R
import com.beverlly.app.data.AuthRepository
import com.beverlly.app.databinding.FragmentProfileBinding
import com.beverlly.app.model.UserProfile
import com.beverlly.app.ui.auth.LoginActivity
import com.beverlly.app.ui.orders.OrdersActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AuthRepository.currentUserProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                val fullName = "${profile.firstName} ${profile.lastName}".trim().ifEmpty { "Beverlly Member" }
                val firstInitial = profile.firstName.take(1).uppercase()
                val lastInitial = profile.lastName.take(1).uppercase()
                val initials = if (firstInitial.isNotEmpty()) "$firstInitial$lastInitial" else "BV"

                binding.tvAvatarInitials.text = initials
                binding.tvUserName.text = fullName
                binding.tvUserEmail.text = profile.email.ifEmpty { "No email registered" }

                val verifiedText = if (AuthRepository.isEmailVerified()) " • Email Verified ✓" else " • Verify Email ✉️"
                binding.tvMemberSince.text = "Signed in via ${profile.authProvider}$verifiedText"
                binding.tvSignOutLabel.text = "Sign Out"
            } else {
                binding.tvAvatarInitials.text = "👋"
                binding.tvUserName.text = "Guest User"
                binding.tvUserEmail.text = "Sign in to access orders and saved items"
                binding.tvMemberSince.text = "BEVERLLY Guest"
                binding.tvSignOutLabel.text = "Sign In / Register"
            }
        }

        binding.tvMemberSince.setOnClickListener {
            if (AuthRepository.isLoggedIn() && !AuthRepository.isEmailVerified()) {
                AuthRepository.sendEmailVerification(
                    onSuccess = {
                        Toast.makeText(context, "Verification email sent to ${AuthRepository.getCurrentUser()?.email} ✉️", Toast.LENGTH_LONG).show()
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.menuOrders.setOnClickListener {
            if (AuthRepository.isLoggedIn()) {
                startActivity(Intent(requireContext(), OrdersActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Please sign in to view your orders", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        binding.menuWishlist.setOnClickListener {
            activity?.let {
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.wishlistFragment
            }
        }

        binding.menuEditProfile.setOnClickListener {
            if (AuthRepository.isLoggedIn()) {
                showEditProfileDialog()
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        binding.menuPassword.setOnClickListener {
            if (AuthRepository.isLoggedIn()) {
                showChangePasswordDialog()
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        binding.menuSignOut.setOnClickListener {
            if (AuthRepository.isLoggedIn()) {
                AuthRepository.signOut()
                Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }
    }

    private fun showEditProfileDialog() {
        val currentUser = AuthRepository.getCurrentUser() ?: return
        val context = requireContext()

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val etFirstName = EditText(context).apply {
            hint = "First Name"
            setText(currentUser.firstName)
        }
        val etLastName = EditText(context).apply {
            hint = "Last Name"
            setText(currentUser.lastName)
        }
        val etPhone = EditText(context).apply {
            hint = "Phone Number"
            setText(currentUser.phone)
        }
        val etAddress = EditText(context).apply {
            hint = "Delivery Address"
            setText(currentUser.address)
        }

        layout.addView(etFirstName)
        layout.addView(etLastName)
        layout.addView(etPhone)
        layout.addView(etAddress)

        AlertDialog.Builder(context)
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { dialog, _ ->
                val updated = currentUser.copy(
                    firstName = etFirstName.text.toString().trim(),
                    lastName = etLastName.text.toString().trim(),
                    phone = etPhone.text.toString().trim(),
                    address = etAddress.text.toString().trim()
                )
                AuthRepository.updateUserProfile(
                    updated,
                    onSuccess = {
                        Toast.makeText(context, "Profile updated successfully! ✓", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val currentUser = AuthRepository.getCurrentUser() ?: return
        val context = requireContext()

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }

        val etNewPassword = EditText(context).apply {
            hint = "New Password (min 6 characters)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etConfirmPassword = EditText(context).apply {
            hint = "Confirm New Password"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(etNewPassword)
        layout.addView(etConfirmPassword)

        AlertDialog.Builder(context)
            .setTitle("Change Password")
            .setView(layout)
            .setPositiveButton("Update Password") { dialog, _ ->
                val newPass = etNewPassword.text.toString().trim()
                val confirmPass = etConfirmPassword.text.toString().trim()

                if (newPass.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (newPass != confirmPass) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                AuthRepository.updatePassword(
                    newPass,
                    onSuccess = {
                        Toast.makeText(context, "Password updated successfully! ✓", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNeutralButton("Send Reset Email") { _, _ ->
                AuthRepository.resetPassword(
                    currentUser.email,
                    onSuccess = {
                        Toast.makeText(context, "Password reset email sent to ${currentUser.email} ✉️", Toast.LENGTH_LONG).show()
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
