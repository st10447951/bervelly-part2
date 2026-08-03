package com.beverlly.app.model

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val sizes: List<String>,
    val stock: Int,
    val isNew: Boolean,
    val imageResId: Int  // drawable resource ID for real product images
)

data class CartItem(
    val product: Product,
    var quantity: Int,
    val size: String
)

data class Order(
    val id: String,
    val date: String,
    val items: List<CartItem>,
    val total: Double,
    var status: String,
    val deliveryAddress: String = ""
)

data class Customer(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val address: String
)
