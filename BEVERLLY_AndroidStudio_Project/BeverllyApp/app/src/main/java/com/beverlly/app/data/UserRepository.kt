package com.beverlly.app.data

import com.beverlly.app.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun saveUserProfile(profile: UserProfile, onComplete: () -> Unit) {
        val db = getFirestore()
        if (db != null && profile.uid.isNotEmpty() && !profile.uid.startsWith("LOCAL")) {
            val userMap = hashMapOf(
                "uid" to profile.uid,
                "firstName" to profile.firstName,
                "lastName" to profile.lastName,
                "email" to profile.email,
                "phone" to profile.phone,
                "address" to profile.address,
                "authProvider" to profile.authProvider
            )
            db.collection("users").document(profile.uid)
                .set(userMap)
                .addOnSuccessListener { onComplete() }
                .addOnFailureListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun fetchUserProfile(uid: String, onResult: (UserProfile?) -> Unit) {
        val db = getFirestore()
        if (db != null && uid.isNotEmpty() && !uid.startsWith("LOCAL")) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val profile = UserProfile(
                            uid = snapshot.getString("uid") ?: uid,
                            firstName = snapshot.getString("firstName") ?: "",
                            lastName = snapshot.getString("lastName") ?: "",
                            email = snapshot.getString("email") ?: "",
                            phone = snapshot.getString("phone") ?: "",
                            address = snapshot.getString("address") ?: "",
                            authProvider = snapshot.getString("authProvider") ?: "Email"
                        )
                        onResult(profile)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            onResult(null)
        }
    }
}
