package com.beverlly.app.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beverlly.app.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

object AuthRepository {

    private var firebaseAuth: FirebaseAuth? = null
    private val _currentUserProfile = MutableLiveData<UserProfile?>()
    val currentUserProfile: LiveData<UserProfile?> get() = _currentUserProfile

    private var localUser: UserProfile? = null

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth?.currentUser?.let { fbUser ->
                    val profile = UserProfile(
                        uid = fbUser.uid,
                        firstName = fbUser.displayName?.split(" ")?.firstOrNull() ?: "",
                        lastName = fbUser.displayName?.split(" ")?.drop(1)?.joinToString(" ") ?: "",
                        email = fbUser.email ?: "",
                        authProvider = if (fbUser.providerData.any { it.providerId == "google.com" }) "Google" else "Email"
                    )
                    localUser = profile
                    _currentUserProfile.postValue(profile)
                    // Fetch full profile from Firestore
                    UserRepository.fetchUserProfile(fbUser.uid) { remoteProfile ->
                        if (remoteProfile != null) {
                            localUser = remoteProfile
                            _currentUserProfile.postValue(remoteProfile)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth?.currentUser != null || localUser != null
    }

    fun getCurrentUser(): UserProfile? {
        return localUser
    }

    fun getRegisteredEmail(): String {
        val email = localUser?.email?.ifEmpty { null }
            ?: firebaseAuth?.currentUser?.email
        return email ?: ""
    }

    fun getRegisteredUserUid(): String {
        return localUser?.uid?.ifEmpty { null }
            ?: firebaseAuth?.currentUser?.uid
            ?: "GUEST"
    }

    fun setCurrentUser(profile: UserProfile) {
        localUser = profile
        _currentUserProfile.postValue(profile)
    }

    fun isEmailVerified(): Boolean {
        return firebaseAuth?.currentUser?.isEmailVerified ?: (localUser != null)
    }

    fun sendEmailVerification(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = firebaseAuth?.currentUser
        if (user != null) {
            user.sendEmailVerification()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to send verification email") }
        } else {
            onSuccess()
        }
    }

    fun signInWithEmail(
        email: String,
        pass: String,
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = firebaseAuth
        if (auth != null) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val user = result.user
                    val uid = user?.uid ?: ""
                    UserRepository.fetchUserProfile(uid) { remoteProfile ->
                        val profile = remoteProfile ?: UserProfile(
                            uid = uid,
                            email = email,
                            authProvider = "Email"
                        )
                        localUser = profile
                        _currentUserProfile.postValue(profile)
                        onSuccess(profile)
                    }
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Authentication failed")
                }
        } else {
            onError("Firebase Authentication is not available. Check your Firebase configuration and try again.")
        }
    }

    fun signUpWithEmail(
        profile: UserProfile,
        pass: String,
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = firebaseAuth
        if (auth != null) {
            auth.createUserWithEmailAndPassword(profile.email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: ""
                    val updatedProfile = profile.copy(uid = uid, authProvider = "Email")
                    // Send Email Verification automatically on sign up
                    result.user?.sendEmailVerification()
                    UserRepository.saveUserProfile(updatedProfile) {
                        localUser = updatedProfile
                        _currentUserProfile.postValue(updatedProfile)
                        onSuccess(updatedProfile)
                    }
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Registration failed")
                }
        } else {
            onError("Firebase Authentication is not available. Check your Firebase configuration and try again.")
        }
    }

    fun firebaseAuthWithGoogle(
        idToken: String,
        email: String? = null,
        givenName: String? = null,
        familyName: String? = null,
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = firebaseAuth
        if (auth != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val fbUser = result.user
                    val uid = fbUser?.uid ?: ""
                    val nameParts = fbUser?.displayName?.split(" ") ?: emptyList()
                    val fName = givenName?.ifEmpty { null } ?: nameParts.firstOrNull() ?: "Google"
                    val lName = familyName?.ifEmpty { null } ?: if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else "User"
                    val userEmail = email?.ifEmpty { null } ?: fbUser?.email ?: ""

                    UserRepository.fetchUserProfile(uid) { existingProfile ->
                        val finalProfile = existingProfile?.copy(
                            email = userEmail,
                            firstName = existingProfile.firstName.ifEmpty { fName },
                            lastName = existingProfile.lastName.ifEmpty { lName }
                        ) ?: UserProfile(
                            uid = uid,
                            firstName = fName,
                            lastName = lName,
                            email = userEmail,
                            authProvider = "Google"
                        )
                        UserRepository.saveUserProfile(finalProfile) {}
                        localUser = finalProfile
                        _currentUserProfile.postValue(finalProfile)
                        onSuccess(finalProfile)
                    }
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "Google sign-in failed")
                }
        } else {
            onError("Firebase Authentication is not available. Check your Firebase configuration and try again.")
        }
    }

    fun updateUserProfile(
        updatedProfile: UserProfile,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        localUser = updatedProfile
        _currentUserProfile.postValue(updatedProfile)
        UserRepository.saveUserProfile(updatedProfile) {
            onSuccess()
        }
    }

    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = firebaseAuth?.currentUser
        if (user != null) {
            user.updatePassword(newPassword)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to update password") }
        } else {
            onError("You must be signed in with Firebase to update your password.")
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val auth = firebaseAuth
        if (auth != null) {
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.localizedMessage ?: "Password reset failed") }
        } else {
            onError("Firebase Authentication is not available. Check your Firebase configuration and try again.")
        }
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        localUser = null
        _currentUserProfile.postValue(null)
    }
}
