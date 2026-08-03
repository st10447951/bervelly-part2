package com.example.bervelly.data

import com.example.bervelly.R
import com.example.bervelly.model.Product

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
            imageResId = R.drawable.product_tee_white,
            galleryImages = listOf(R.drawable.hero_lifestyle, R.drawable.product_tee_white)
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
            imageResId = R.drawable.product_tee_black,
            galleryImages = listOf(R.drawable.hero_lifestyle2, R.drawable.product_tee_black)
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
            imageResId = R.drawable.product_tee_snake,
            galleryImages = listOf(R.drawable.product_tee_snake, R.drawable.hero_lifestyle)
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
            imageResId = R.drawable.product_hoodie_red,
            galleryImages = listOf(R.drawable.product_hoodie_red, R.drawable.hero_lifestyle2)
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
            imageResId = R.drawable.product_hoodie_wrap,
            galleryImages = listOf(R.drawable.product_hoodie_wrap, R.drawable.hero_lifestyle)
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
            imageResId = R.drawable.product_hoodie_bw,
            galleryImages = listOf(R.drawable.product_hoodie_bw, R.drawable.hero_lifestyle2)
        ),
        Product(
            id = 7,
            name = "BEVERLLY Graphic Tee — Signature",
            category = "Streetwear",
            price = 420.0,
            description = "New season signature graphic tee featuring the latest BEVERLLY artwork. Premium cotton blend for ultimate comfort and style.",
            sizes = listOf("XS", "S", "M", "L", "XL"),
            stock = 20,
            isNew = true,
            imageResId = R.drawable.product_new_tee_1,
            galleryImages = listOf(R.drawable.product_new_tee_1, R.drawable.hero_lifestyle)
        ),
        Product(
            id = 8,
            name = "BEVERLLY Minimal Tee — Grey",
            category = "Casual",
            price = 380.0,
            description = "A clean, minimal approach to the classic BEVERLLY tee. Subtle branding on the chest, perfect for everyday wear.",
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            stock = 25,
            isNew = true,
            imageResId = R.drawable.product_new_tee_2,
            galleryImages = listOf(R.drawable.product_new_tee_2, R.drawable.hero_lifestyle2)
        ),
        Product(
            id = 9,
            name = "BEVERLLY Heritage Hoodie — Black",
            category = "Streetwear",
            price = 950.0,
            description = "Part of our Heritage collection, this hoodie features embroidered BEVERLLY South Africa detailing. High-density fleece.",
            sizes = listOf("M", "L", "XL", "XXL"),
            stock = 12,
            isNew = true,
            imageResId = R.drawable.product_new_hoodie_1,
            galleryImages = listOf(R.drawable.product_new_hoodie_1, R.drawable.hero_lifestyle)
        ),
        Product(
            id = 10,
            name = "BEVERLLY Street Hoodie — Charcoal",
            category = "Streetwear",
            price = 950.0,
            description = "Charcoal grey street hoodie with oversized fit and dropped shoulders. Bold BEVERLLY typography on the sleeves.",
            sizes = listOf("S", "M", "L", "XL"),
            stock = 14,
            isNew = true,
            imageResId = R.drawable.product_new_hoodie_2,
            galleryImages = listOf(R.drawable.product_new_hoodie_2, R.drawable.hero_lifestyle2)
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
