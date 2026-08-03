package com.beverlly.app.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beverlly.app.R
import com.beverlly.app.data.CartRepository
import com.beverlly.app.databinding.FragmentCartBinding
import com.beverlly.app.ui.checkout.CheckoutActivity

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartItemAdapter { item ->
            CartRepository.removeFromCart(item)
            refreshCart()
        }
        binding.rvCartItems.adapter = adapter

        CartRepository.cartItems.observe(viewLifecycleOwner) { refreshCart() }

        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(requireContext(), CheckoutActivity::class.java))
        }

        binding.btnShopNow.setOnClickListener {
            activity?.let {
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.catalogueFragment
            }
        }

        refreshCart()
    }

    private fun refreshCart() {
        val items = CartRepository.cartItems.value ?: emptyList()
        if (items.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.layoutCart.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.layoutCart.visibility = View.VISIBLE
            adapter.updateItems(items.toMutableList())
            val sub = CartRepository.cartTotal()
            val del = CartRepository.deliveryFee()
            val total = CartRepository.grandTotal()
            binding.tvSubtotal.text = "R ${sub.toInt()}"
            binding.tvDelivery.text = if (del == 0.0) "FREE" else "R ${del.toInt()}"
            binding.tvTotal.text = "R ${total.toInt()}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
