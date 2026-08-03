package com.beverlly.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.beverlly.app.R
import com.beverlly.app.data.ProductRepository
import com.beverlly.app.databinding.FragmentHomeBinding
import com.beverlly.app.model.Product
import com.beverlly.app.ui.catalogue.ProductAdapter
import com.beverlly.app.ui.detail.ProductDetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // New arrivals (horizontal scroll)
        val newArrivals = ProductRepository.products.filter { it.isNew }
        val newAdapter = ProductAdapter(
            newArrivals,
            R.layout.item_product_card
        ) { product -> openDetail(product) }
        binding.rvNewArrivals.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNewArrivals.adapter = newAdapter

        // Trending (horizontal scroll)
        val trending = ProductRepository.products.filter { !it.isNew }
        val trendAdapter = ProductAdapter(
            trending,
            R.layout.item_product_card
        ) { product -> openDetail(product) }
        binding.rvTrending.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrending.adapter = trendAdapter

        binding.btnShopNow.setOnClickListener {
            // Navigate to catalogue tab
            activity?.let {
                val bottomNav = it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
                bottomNav.selectedItemId = R.id.catalogueFragment
            }
        }

        binding.tvSeeAll.setOnClickListener {
            activity?.let {
                val bottomNav = it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
                bottomNav.selectedItemId = R.id.catalogueFragment
            }
        }
    }

    private fun openDetail(product: Product) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
