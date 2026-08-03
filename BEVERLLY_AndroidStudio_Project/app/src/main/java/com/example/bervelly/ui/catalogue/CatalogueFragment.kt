package com.example.bervelly.ui.catalogue

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bervelly.R
import com.example.bervelly.data.ProductRepository
import com.example.bervelly.databinding.FragmentCatalogueBinding

import android.content.Intent
import com.example.bervelly.model.Product
import com.example.bervelly.ui.detail.ProductDetailActivity

class CatalogueFragment : Fragment() {

    private var _binding: FragmentCatalogueBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private var currentCategory = "All"
    private var currentQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter(ProductRepository.products) { product -> openDetail(product) }
        binding.rvProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = adapter

        // Search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentQuery = s.toString()
                filterProducts()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Category chips
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            currentCategory = when {
                checkedIds.contains(R.id.chipCasual) -> "Casual"
                checkedIds.contains(R.id.chipStreet) -> "Streetwear"
                else -> "All"
            }
            filterProducts()
        }

        filterProducts()
    }

    private fun filterProducts() {
        var filtered = ProductRepository.getByCategory(currentCategory)
        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(currentQuery, ignoreCase = true) ||
                it.category.contains(currentQuery, ignoreCase = true)
            }
        }
        adapter.updateProducts(filtered)
        binding.tvCount.text = "${filtered.size} product${if (filtered.size != 1) "s" else ""}"
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
