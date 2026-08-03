package com.example.bervelly.data

import com.example.bervelly.model.CartItem
import com.example.bervelly.model.Order
import com.example.bervelly.model.Product
import androidx.lifecycle.MutableLiveData

object CartRepository {

    val cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val wishlistItems = MutableLiveData<MutableList<Product>>(mutableListOf())
    val orders = MutableLiveData<MutableList<Order>>(mutableListOf(
        Order(
            id = "BEV-2026-0210",
            date = "28 May 2026",
            items = listOf(
                CartItem(ProductRepository.products[2], 1, "M")
            ),
            total = 450.0,
            status = "Delivered"
        ),
        Order(
            id = "BEV-2026-0178",
            date = "15 May 2026",
            items = listOf(
                CartItem(ProductRepository.products[3], 1, "L"),
                CartItem(ProductRepository.products[0], 1, "M")
            ),
            total = 1249.0,
            status = "Delivered"
        )
    ))

    fun addToCart(product: Product, size: String, quantity: Int = 1) {
        val current = cartItems.value ?: mutableListOf()
        val existing = current.find { it.product.id == product.id && it.size == size }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            current.add(CartItem(product, quantity, size))
        }
        cartItems.value = current
    }

    fun removeFromCart(item: CartItem) {
        val current = cartItems.value ?: mutableListOf()
        current.remove(item)
        cartItems.value = current
    }

    fun clearCart() {
        cartItems.value = mutableListOf()
    }

    fun cartTotal(): Double =
        cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0

    fun cartCount(): Int =
        cartItems.value?.sumOf { it.quantity } ?: 0

    fun deliveryFee(): Double = if (cartTotal() >= 500) 0.0 else 80.0

    fun grandTotal(): Double = cartTotal() + deliveryFee()

    fun toggleWishlist(product: Product) {
        val current = wishlistItems.value ?: mutableListOf()
        if (current.any { it.id == product.id }) {
            current.removeAll { it.id == product.id }
        } else {
            current.add(product)
        }
        wishlistItems.value = current
    }

    fun isInWishlist(productId: Int): Boolean =
        wishlistItems.value?.any { it.id == productId } ?: false

    fun placeOrder(deliveryAddress: String): Order {
        val items = cartItems.value?.toList() ?: emptyList()
        val orderId = "BEV-" + (1000..9999).random()
        val order = Order(
            id = orderId,
            date = "02 Jun 2026",
            items = items,
            total = grandTotal(),
            status = "Processing",
            deliveryAddress = deliveryAddress
        )
        val current = orders.value ?: mutableListOf()
        current.add(0, order)
        orders.value = current
        clearCart()
        return order
    }
}
