package com.example.gramavasathi.domain.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Farmstay(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val district: String = "",
    val distanceKm: Int = 0,
    val pricePerNight: Double = 0.0,
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val starRating: Double = 0.0,
    
    @get:PropertyName("isVerified")
    val isVerified: Boolean = false,
    
    @get:PropertyName("verified")
    val verified: Boolean = false,
    
    val hostUid: String = "",
    val hostName: String = "",
    val activities: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val unavailableDates: List<String> = emptyList(),
    val description: String = "",
    val imageUrl: String = "",
    /** Extra photos for the stay detail / gallery (spec: high-quality gallery per farm-stay). */
    val imageGalleryUrls: List<String> = emptyList(),
    val createdAt: Date? = null
)
