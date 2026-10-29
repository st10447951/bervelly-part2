package com.example.bervelly.model

data class Product(
    val id: Int = 0,
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val sizes: List<String> = emptyList(),
    val stock: Int = 0,
    val isNew: Boolean = false,
    val imageResId: Int = 0,
    val galleryImages: List<Int> = emptyList()
)

data class CartItem(
    val product: Product = Product(),
    var quantity: Int = 0,
    val size: String = ""
)

data class Order(
    val id: String = "",
    val date: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    var status: String = "",
    val deliveryAddress: String = ""
)

data class Customer(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = ""
)
