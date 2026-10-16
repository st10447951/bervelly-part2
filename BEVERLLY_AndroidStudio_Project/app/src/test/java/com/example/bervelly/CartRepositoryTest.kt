package com.example.bervelly

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bervelly.data.CartRepository
import com.example.bervelly.data.ProductRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CartRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        CartRepository.clearCart()
        CartRepository.wishlistItems.value = mutableListOf()
    }

    @After
    fun tearDown() {
        CartRepository.clearCart()
        CartRepository.wishlistItems.value = mutableListOf()
    }

    @Test
    fun testAddToCartNewItem() {
        val product = ProductRepository.products[0] // price 350.0
        CartRepository.addToCart(product, "M", 1)

        assertEquals(1, CartRepository.cartCount())
        assertEquals(350.0, CartRepository.cartTotal(), 0.001)
        val items = CartRepository.cartItems.value
        assertNotNull(items)
        assertEquals(1, items?.size)
        assertEquals(product, items?.get(0)?.product)
        assertEquals("M", items?.get(0)?.size)
        assertEquals(1, items?.get(0)?.quantity)
    }

    @Test
    fun testAddToCartExistingItemSameSize() {
        val product = ProductRepository.products[0]
        CartRepository.addToCart(product, "M", 1)
        CartRepository.addToCart(product, "M", 2)

        assertEquals(3, CartRepository.cartCount())
        assertEquals(350.0 * 3, CartRepository.cartTotal(), 0.001)
        val items = CartRepository.cartItems.value
        assertEquals(1, items?.size)
        assertEquals(3, items?.get(0)?.quantity)
    }

    @Test
    fun testAddToCartDifferentSize() {
        val product = ProductRepository.products[0]
        CartRepository.addToCart(product, "M", 1)
        CartRepository.addToCart(product, "L", 2)

        assertEquals(3, CartRepository.cartCount())
        val items = CartRepository.cartItems.value
        assertEquals(2, items?.size)
    }

    @Test
    fun testRemoveFromCart() {
        val product = ProductRepository.products[0]
        CartRepository.addToCart(product, "M", 1)
        val item = CartRepository.cartItems.value?.get(0)
        assertNotNull(item)

        if (item != null) {
            CartRepository.removeFromCart(item)
        }
        assertEquals(0, CartRepository.cartCount())
        assertEquals(0.0, CartRepository.cartTotal(), 0.001)
    }

    @Test
    fun testDeliveryFeeCalculation() {
        // Below 500 fee is 80.0
        val cheapProduct = ProductRepository.products[0] // 350.0
        CartRepository.addToCart(cheapProduct, "M", 1)
        assertEquals(350.0, CartRepository.cartTotal(), 0.001)
        assertEquals(80.0, CartRepository.deliveryFee(), 0.001)
        assertEquals(350.0 + 80.0, CartRepository.grandTotal(), 0.001)

        // 500 or above fee is 0.0
        CartRepository.clearCart()
        val expensiveProduct = ProductRepository.products[2] // 450.0
        CartRepository.addToCart(expensiveProduct, "M", 2) // 900.0
        assertEquals(900.0, CartRepository.cartTotal(), 0.001)
        assertEquals(0.0, CartRepository.deliveryFee(), 0.001)
        assertEquals(900.0, CartRepository.grandTotal(), 0.001)
    }

    @Test
    fun testWishlistToggle() {
        val product = ProductRepository.products[0]
        assertFalse(CartRepository.isInWishlist(product.id))

        CartRepository.toggleWishlist(product)
        assertTrue(CartRepository.isInWishlist(product.id))

        CartRepository.toggleWishlist(product)
        assertFalse(CartRepository.isInWishlist(product.id))
    }

    @Test
    fun testPlaceOrder() {
        val product = ProductRepository.products[0]
        CartRepository.addToCart(product, "M", 1)
        assertEquals(1, CartRepository.cartCount())

        val order = CartRepository.placeOrder("123 Test Street, Pretoria")
        assertNotNull(order)
        assertTrue(order.id.startsWith("BEV-"))
        assertEquals("123 Test Street, Pretoria", order.deliveryAddress)
        assertEquals("Processing", order.status)
        assertEquals(0, CartRepository.cartCount()) // Cart should be cleared after order
        
        val orders = CartRepository.orders.value
        assertNotNull(orders)
        assertTrue(orders?.contains(order) == true)
    }
}
