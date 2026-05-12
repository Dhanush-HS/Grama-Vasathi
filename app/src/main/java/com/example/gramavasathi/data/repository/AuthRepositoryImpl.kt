package com.example.gramavasathi.data.repository

import com.example.gramavasathi.domain.model.User
import com.example.gramavasathi.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<User?> = flow {
        // Simple stub implementation
        val user = firebaseAuth.currentUser
        if (user != null) {
            emit(User(uid = user.uid, email = user.email ?: ""))
        } else {
            emit(null)
        }
    }
}
