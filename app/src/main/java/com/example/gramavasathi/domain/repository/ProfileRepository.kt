package com.example.gramavasathi.domain.repository

import com.example.gramavasathi.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(userId: String): Flow<UserProfile>
}
