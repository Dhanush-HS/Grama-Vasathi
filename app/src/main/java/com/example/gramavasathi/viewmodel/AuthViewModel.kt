package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.demo.DemoCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState =
        MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> =
        _authState.asStateFlow()

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapSignInException(e))
            }
        }
    }

    /**
     * Sign-in used by the login UI. If the built-in demo email/password are used and Firebase
     * rejects sign-in (usually because the user was never created), registers that account and
     * signs in — so "Fill demo credentials" works on a fresh Firebase project without Console setup.
     */
    fun signInWithEmailAllowingDemoBootstrap(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            try {
                auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
                _authState.value = AuthState.Success
            } catch (signInError: Exception) {
                if (!DemoCredentials.matchesKnownDemo(trimmedEmail, trimmedPassword)) {
                    _authState.value = AuthState.Error(mapSignInException(signInError))
                    return@launch
                }
                try {
                    val result =
                        auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
                    val user = result.user!!
                    val displayName = DemoCredentials.displayNameForDemoEmail(trimmedEmail)
                    val profileUpdates =
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
                    user.updateProfile(profileUpdates).await()
                    createGuestProfile(user, displayName, trimmedEmail)
                    _authState.value = AuthState.Success
                } catch (createError: Exception) {
                    val msg = createError.message ?: ""
                    val alreadyRegistered =
                        msg.contains("email address is already", ignoreCase = true) ||
                            msg.contains("EMAIL_EXISTS", ignoreCase = true)
                    if (alreadyRegistered) {
                        try {
                            auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword).await()
                            _authState.value = AuthState.Success
                        } catch (_: Exception) {
                            _authState.value = AuthState.Error(
                                "Incorrect password for this demo account."
                            )
                        }
                    } else {
                        _authState.value = AuthState.Error(
                            createError.message ?: "Could not create demo account"
                        )
                    }
                }
            }
        }
    }

    private fun mapSignInException(e: Exception): String =
        when {
            e.message?.contains("OPERATION_NOT_ALLOWED") == true ->
                "Enable Email/Password in Firebase Console → Authentication → Sign-in method."
            e.message?.contains("no user record") == true ->
                "No account found with this email"
            e.message?.contains("password is invalid") == true ->
                "Incorrect password"
            e.message?.contains("badly formatted") == true ->
                "Please enter a valid email address"
            e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                "Incorrect email or password"
            else -> e.message ?: "Sign in failed"
        }

    fun signUpWithEmail(
        email: String,
        password: String,
        name: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                if (password.length < 6) {
                    _authState.value = AuthState.Error(
                        "Password must be at least 6 characters"
                    )
                    return@launch
                }
                if (name.isBlank()) {
                    _authState.value = AuthState.Error(
                        "Please enter your full name"
                    )
                    return@launch
                }

                val result = auth
                    .createUserWithEmailAndPassword(
                        email, password
                    ).await()

                val user = result.user!!

                // Set display name in Firebase Auth
                val profileUpdates =
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                user.updateProfile(profileUpdates).await()

                // Create guest document in Firestore
                createGuestProfile(user, name, email)

                _authState.value = AuthState.Success

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains(
                            "email address is already") == true ->
                            "An account with this email already exists"
                        e.message?.contains(
                            "badly formatted") == true ->
                            "Please enter a valid email address"
                        e.message?.contains(
                            "EMAIL_EXISTS") == true ->
                            "This email is already registered"
                        else -> e.message ?: "Sign up failed"
                    }
                )
            }
        }
    }

    private suspend fun createGuestProfile(
        user: FirebaseUser,
        name: String,
        email: String
    ) {
        try {
            val doc = firestore
                .collection("guests")
                .document(user.uid)
                .get().await()

            if (!doc.exists()) {
                val guestData = hashMapOf(
                    "uid" to user.uid,
                    "name" to name,
                    "email" to email,
                    "photoUrl" to "",
                    "homeCity" to "Bengaluru",
                    "savedStayIds" to emptyList<String>(),
                    "totalStays" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("guests")
                    .document(user.uid)
                    .set(guestData).await()
            }
        } catch (e: Exception) {
            // Auth succeeded even if Firestore write fails
            // Profile will be created on next login
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}