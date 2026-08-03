package com.example.bervelly.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bervelly.R
import com.example.bervelly.data.CartRepository
import com.example.bervelly.databinding.FragmentWishlistBinding
import com.example.bervelly.ui.catalogue.ProductAdapter

import android.content.Intent
import com.example.bervelly.model.Product
import com.example.bervelly.ui.detail.ProductDetailActivity

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }
        binding.rvWishlist.layoutManager = GridLayoutManager(context, 2)
        binding.rvWishlist.adapter = adapter

        CartRepository.wishlistItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.rvWishlist.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.rvWishlist.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
                adapter.updateProducts(items)
            }
        }

        binding.btnShopNow.setOnClickListener {
            activity?.let {
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.catalogueFragment
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
