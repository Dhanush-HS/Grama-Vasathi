package com.example.gramavasathi.domain.model

data class UserProfile(
    val name: String,
    val aggregateRating: Double,
    val pastStays: List<PastStay>
)

data class PastStay(
    val id: String,
    val stayName: String,
    val date: String,
    val activityBooked: String,
    val status: String // e.g., "Completed", "Upcoming"
)
