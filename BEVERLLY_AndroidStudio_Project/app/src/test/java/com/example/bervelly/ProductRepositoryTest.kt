package com.example.bervelly

import com.example.bervelly.data.ProductRepository
import org.junit.Assert.*
import org.junit.Test

class ProductRepositoryTest {

    @Test
    fun testProductsNotEmpty() {
        assertFalse(ProductRepository.products.isEmpty())
    }

    @Test
    fun testGetByIdValid() {
        val product = ProductRepository.getById(1)
        assertNotNull(product)
        assertEquals(1, product?.id)
        assertEquals("BEVERLLY Classic Tee — White", product?.name)
    }

    @Test
    fun testGetByIdInvalid() {
        val product = ProductRepository.getById(9999)
        assertNull(product)
    }

    @Test
    fun testGetByCategoryAll() {
        val list = ProductRepository.getByCategory("All")
        assertEquals(ProductRepository.products.size, list.size)
    }

    @Test
    fun testGetByCategoryCasual() {
        val list = ProductRepository.getByCategory("Casual")
        assertTrue(list.isNotEmpty())
        for (item in list) {
            assertEquals("Casual", item.category)
        }
    }

    @Test
    fun testGetByCategoryStreetwear() {
        val list = ProductRepository.getByCategory("Streetwear")
        assertTrue(list.isNotEmpty())
        for (item in list) {
            assertEquals("Streetwear", item.category)
        }
    }

    @Test
    fun testSearch() {
        val results = ProductRepository.search("tee")
        assertTrue(results.isNotEmpty())
        for (item in results) {
            val matchesName = item.name.contains("tee", ignoreCase = true)
            val matchesCategory = item.category.contains("tee", ignoreCase = true)
            assertTrue(matchesName || matchesCategory)
        }
    }

    @Test
    fun testSearchCaseInsensitive() {
        val results = ProductRepository.search("HOODIE")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun testCategoriesList() {
        val categories = ProductRepository.categories
        assertTrue(categories.contains("All"))
        assertTrue(categories.contains("Casual"))
        assertTrue(categories.contains("Streetwear"))
    }
}
