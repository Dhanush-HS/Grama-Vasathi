package com.example.gramavasathi.data.repository

import com.example.gramavasathi.domain.model.PastStay
import com.example.gramavasathi.domain.model.UserProfile
import com.example.gramavasathi.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {
    override fun getUserProfile(userId: String): Flow<UserProfile> = flow {
        // Return dummy data for the prototype
        val dummyProfile = UserProfile(
            name = "Guest User",
            aggregateRating = 4.8,
            pastStays = listOf(
                PastStay(
                    id = "booking_1",
                    stayName = "Green Valley Homestay",
                    date = "Mar 12, 2026 - Mar 15, 2026",
                    activityBooked = "Pottery",
                    status = "Completed"
                ),
                PastStay(
                    id = "booking_2",
                    stayName = "Sunrise Acres",
                    date = "Jun 01, 2026 - Jun 05, 2026",
                    activityBooked = "Coffee Picking",
                    status = "Upcoming"
                )
            )
        )
        emit(dummyProfile)
    }
}
