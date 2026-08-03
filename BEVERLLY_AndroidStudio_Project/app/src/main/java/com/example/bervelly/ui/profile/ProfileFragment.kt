package com.example.bervelly.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bervelly.R
import com.example.bervelly.data.session.SessionManager
import com.example.bervelly.databinding.FragmentProfileBinding
import com.example.bervelly.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // Set user info
        binding.tvUserName.text = sessionManager.getFirstName() ?: "User"
        binding.tvUserEmail.text = sessionManager.getEmail() ?: "email@example.com"
        val initials = (sessionManager.getFirstName()?.take(2) ?: "TD").uppercase()
        binding.tvInitials.text = initials

        binding.menuOrders.setOnClickListener {
            // startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }
        binding.menuWishlist.setOnClickListener {
            activity?.let {
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.wishlistFragment
            }
        }
        binding.menuEditProfile.setOnClickListener {
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        }
        binding.menuPassword.setOnClickListener {
            Toast.makeText(context, "Password change email sent", Toast.LENGTH_SHORT).show()
        }
        binding.menuSignOut.setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
