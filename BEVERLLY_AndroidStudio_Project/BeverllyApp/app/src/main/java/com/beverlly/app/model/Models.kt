package com.beverlly.app.model

data class Product(
    val id: Int = 0,
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val sizes: List<String> = emptyList(),
    val stock: Int = 0,
    val isNew: Boolean = false,
    val imageResId: Int = 0  // drawable resource ID for real product images
)

data class CartItem(
    val product: Product = Product(),
    var quantity: Int = 1,
    val size: String = ""
)

data class Order(
    val id: String = "",
    val date: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    var status: String = "Pending",
    val deliveryAddress: String = "",
    val paymentMethod: String = "Card",
    val userId: String = ""
)

data class Customer(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = ""
)

data class UserProfile(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val authProvider: String = "Email" // "Email" or "Google"
)
