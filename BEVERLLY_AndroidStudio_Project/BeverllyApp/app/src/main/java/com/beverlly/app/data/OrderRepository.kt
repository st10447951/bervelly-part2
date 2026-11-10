package com.beverlly.app.data

import com.beverlly.app.model.CartItem
import com.beverlly.app.model.Order
import com.google.firebase.firestore.FirebaseFirestore

object OrderRepository {

    private fun getFirestore(): FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (_: Exception) {
        null
    }

    fun saveOrder(order: Order, userId: String, onComplete: (Boolean) -> Unit) {
        val db = getFirestore()
        if (db == null || userId.isBlank() || userId == "GUEST" || userId.startsWith("LOCAL")) {
            onComplete(false)
            return
        }

        val itemDocuments = order.items.map { item ->
            hashMapOf(
                "productId" to item.product.id,
                "productName" to item.product.name,
                "category" to item.product.category,
                "unitPrice" to item.product.price,
                "quantity" to item.quantity,
                "size" to item.size,
                "lineTotal" to item.product.price * item.quantity,
                "imageResId" to item.product.imageResId
            )
        }

        val orderData = hashMapOf<String, Any>(
            "id" to order.id,
            "date" to order.date,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "total" to order.total,
            "status" to order.status,
            "deliveryAddress" to order.deliveryAddress,
            "paymentMethod" to order.paymentMethod,
            "userId" to userId,
            "customerId" to userId,
            "itemsCount" to order.items.size,
            "items" to itemDocuments,
            "itemsSummary" to order.items.joinToString(", ") {
                "${it.product.name} (${it.size}) x${it.quantity}"
            }
        )

        // Save the full order and all line items to the main orders collection.
        db.collection("orders").document(order.id).set(orderData)
            .addOnSuccessListener {
                // Keep a customer-scoped copy for efficient "My Orders" queries.
                db.collection("users").document(userId)
                    .collection("user_orders").document(order.id)
                    .set(orderData)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun fetchUserOrders(userId: String, onResult: (List<Order>) -> Unit) {
        val db = getFirestore()
        if (db == null || userId.isBlank() || userId.startsWith("LOCAL")) {
            onResult(emptyList())
            return
        }

        db.collection("users").document(userId).collection("user_orders")
            .get()
            .addOnSuccessListener { snapshot ->
                val orders = snapshot.documents.mapNotNull { doc ->
                    val itemMaps = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                    val items = itemMaps.map { data ->
                        val productId = (data["productId"] as? Number)?.toInt() ?: 0
                        val localProduct = ProductRepository.getById(productId)
                        val product = (localProduct ?: com.beverlly.app.model.Product()).copy(
                            id = productId,
                            name = data["productName"] as? String ?: localProduct?.name.orEmpty(),
                            category = data["category"] as? String ?: localProduct?.category.orEmpty(),
                            price = (data["unitPrice"] as? Number)?.toDouble() ?: 0.0,
                            imageResId = (data["imageResId"] as? Number)?.toInt() ?: localProduct?.imageResId ?: 0
                        )
                        CartItem(
                            product = product,
                            quantity = (data["quantity"] as? Number)?.toInt() ?: 1,
                            size = data["size"] as? String ?: ""
                        )
                    }
                    Order(
                        id = doc.getString("id") ?: doc.id,
                        date = doc.getString("date") ?: "",
                        items = items,
                        total = doc.getDouble("total") ?: 0.0,
                        status = doc.getString("status") ?: "Processing",
                        deliveryAddress = doc.getString("deliveryAddress") ?: "",
                        paymentMethod = doc.getString("paymentMethod") ?: "Card",
                        userId = userId
                    )
                }.sortedByDescending { it.date }
                onResult(orders)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
