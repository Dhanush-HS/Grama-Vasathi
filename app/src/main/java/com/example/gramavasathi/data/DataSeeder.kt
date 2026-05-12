package com.example.gramavasathi.data

import com.example.gramavasathi.domain.model.Farmstay
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DataSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun seedIfEmpty() {
        val snapshot = firestore.collection("stays")
            .get().await()
        if (snapshot.isEmpty) {
            val batch = firestore.batch()
            getSeedData().forEach { stay ->
                val ref = firestore.collection("stays").document()
                batch.set(ref, stay)
            }
            batch.commit().await()
        }
    }

    private fun getSeedData(): List<Farmstay> {
        return listOf(
            Farmstay(
                name = "Nandini Family Farmstay",
                location = "Coorg, Karnataka",
                district = "Kodagu",
                distanceKm = 42,
                pricePerNight = 1200.0,
                price = 1200.0,
                rating = 4.8,
                starRating = 4.8,
                verified = true,
                isVerified = true,
                hostName = "Nandini Krishnamurthy",
                hostUid = "host_001",
                activities = listOf("Cow Milking", "Local Cooking", "Birdwatching", "Organic Farming"),
                amenities = listOf("Western Toilet", "Safe Water", "Clean Linen", "WiFi"),
                unavailableDates = listOf("2026-05-14", "2026-05-21"),
                imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
                imageGalleryUrls = listOf(
                    "https://images.unsplash.com/photo-1582719508461-905c673771fd?w=600",
                    "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600",
                    "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600"
                ),
                description = "Authentic coffee estate stay in Coorg."
            ),
            Farmstay(
                name = "Reddy Mane Agri-Home",
                location = "Hampi, Karnataka",
                district = "Vijayanagara",
                distanceKm = 8,
                pricePerNight = 950.0,
                price = 950.0,
                rating = 4.6,
                starRating = 4.6,
                verified = true,
                isVerified = true,
                hostName = "Ramesh Reddy",
                hostUid = "host_002",
                activities = listOf("Field Plowing", "Birdwatching", "Trekking"),
                amenities = listOf("Western Toilet", "Safe Water", "Clean Linen"),
                unavailableDates = listOf("2026-05-18", "2026-05-19"),
                imageUrl = "https://images.unsplash.com/photo-1510798831971-661eb04b3739?w=800",
                imageGalleryUrls = listOf(
                    "https://images.unsplash.com/photo-1502673530728-f79b254cabef?w=600",
                    "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600",
                    "https://images.unsplash.com/photo-1523217582564-09d865d498bd?w=600"
                ),
                description = "Stay beside the ancient Hampi ruins."
            ),
            Farmstay(
                name = "Gowda Lake Farmhouse",
                location = "Chikmagalur, Karnataka",
                district = "Chikmagalur",
                distanceKm = 12,
                pricePerNight = 1450.0,
                price = 1450.0,
                rating = 4.9,
                starRating = 4.9,
                verified = true,
                isVerified = true,
                hostName = "Sunitha Gowda",
                hostUid = "host_003",
                activities = listOf("Birdwatching", "Coffee Picking", "Trekking", "Organic Farming"),
                amenities = listOf("Western Toilet", "Safe Water", "Clean Linen", "Hot Water", "WiFi"),
                unavailableDates = listOf("2026-05-10", "2026-05-25"),
                imageUrl = "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800",
                imageGalleryUrls = listOf(
                    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600",
                    "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600",
                    "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=600"
                ),
                description = "Premium coffee estate in Western Ghats."
            ),
            Farmstay(
                name = "Patil Heritage Homestay",
                location = "Dharwad, Karnataka",
                district = "Dharwad",
                distanceKm = 5,
                pricePerNight = 800.0,
                price = 800.0,
                rating = 4.4,
                starRating = 4.4,
                verified = true,
                isVerified = true,
                hostName = "Mallikarjun Patil",
                hostUid = "host_004",
                activities = listOf("Pottery", "Local Cooking", "Field Plowing"),
                amenities = listOf("Western Toilet", "Safe Water", "Clean Linen"),
                unavailableDates = listOf("2026-05-16", "2026-05-17"),
                imageUrl = "https://images.unsplash.com/photo-1518602164578-cd0074062767?w=800",
                imageGalleryUrls = listOf(
                    "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600",
                    "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=600",
                    "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=600"
                ),
                description = "Heritage home in North Karnataka."
            )
        )
    }
}
