package com.beverlly.app.data

import com.beverlly.app.R
import com.beverlly.app.model.Product

object ProductRepository {

    val products = listOf(
        Product(
            id = 1,
            name = "BEVERLLY Classic Tee — White",
            category = "Casual",
            price = 350.0,
            description = "Oversized white tee with the iconic BEVERLLY South Africa logo and double monogram detail on the hem. 100% heavyweight cotton. Shot to perfection.",
            sizes = listOf("XS", "S", "M", "L", "XL"),
            stock = 12,
            isNew = true,
            imageResId = R.drawable.product_tee_white
        ),
        Product(
            id = 2,
            name = "BEVERLLY Classic Tee — Black",
            category = "Casual",
            price = 350.0,
            description = "Oversized black tee with BEVERLLY South Africa logo and double monogram detail on the hem. Worn against the Pretoria skyline — iconic.",
            sizes = listOf("XS", "S", "M", "L", "XL"),
            stock = 10,
            isNew = true,
            imageResId = R.drawable.product_tee_black
        ),
        Product(
            id = 3,
            name = "Snake Tee — Black/Red",
            category = "Streetwear",
            price = 450.0,
            description = "Statement oversized tee with bold red BEVERLLY lettering and graphic serpent print. A BEVERLLY signature piece — the one that defines the brand.",
            sizes = listOf("XS", "S", "M", "L", "XL", "XXL"),
            stock = 8,
            isNew = true,
            imageResId = R.drawable.product_tee_snake
        ),
        Product(
            id = 4,
            name = "Snake Hoodie — Black/Red",
            category = "Streetwear",
            price = 899.0,
            description = "Heavyweight oversized hoodie with red BEVERLLY South Africa lettering and serpent graphic on the back. Brushed fleece interior.",
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            stock = 15,
            isNew = false,
            imageResId = R.drawable.product_hoodie_red
        ),
        Product(
            id = 5,
            name = "Wrap Logo Hoodie — Black",
            category = "Streetwear",
            price = 899.0,
            description = "Oversized black hoodie with the BEVERLLY wordmark wrapping across the back and sleeves in white. Bold, unmissable, effortlessly street.",
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            stock = 18,
            isNew = false,
            imageResId = R.drawable.product_hoodie_wrap
        ),
        Product(
            id = 6,
            name = "Snake Hoodie — Black/White",
            category = "Streetwear",
            price = 899.0,
            description = "The original BEVERLLY snake hoodie in classic black and white. White lettering, serpent graphic. The piece that started it all.",
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            stock = 7,
            isNew = false,
            imageResId = R.drawable.product_hoodie_bw
        )
    )

    fun getById(id: Int): Product? = products.find { it.id == id }

    fun getByCategory(category: String): List<Product> =
        if (category == "All") products else products.filter { it.category == category }

    fun search(query: String): List<Product> =
        products.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true)
        }

    val categories = listOf("All", "Casual", "Streetwear")
}
