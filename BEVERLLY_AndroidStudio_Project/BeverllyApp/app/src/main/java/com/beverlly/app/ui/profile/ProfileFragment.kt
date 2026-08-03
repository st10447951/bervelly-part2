package com.beverlly.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.beverlly.app.data.CartRepository
import com.beverlly.app.databinding.FragmentProfileBinding
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

        binding.menuOrders.setOnClickListener {
            startActivity(Intent(requireContext(), OrdersActivity::class.java))
        }
        binding.menuWishlist.setOnClickListener {
            activity?.let {
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    com.beverlly.app.R.id.bottomNav
                ).selectedItemId = com.beverlly.app.R.id.wishlistFragment
            }
        }
        binding.menuEditProfile.setOnClickListener {
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        }
        binding.menuPassword.setOnClickListener {
            Toast.makeText(context, "Password change email sent", Toast.LENGTH_SHORT).show()
        }
        binding.menuSignOut.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
