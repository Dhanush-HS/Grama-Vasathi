package com.example.gramavasathi.domain.repository

import com.example.gramavasathi.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    
    // Add abstract functions for sign in, sign out, etc.
}
