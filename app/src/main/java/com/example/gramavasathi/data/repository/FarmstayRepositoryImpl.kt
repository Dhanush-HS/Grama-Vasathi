package com.example.gramavasathi.data.repository

import android.util.Log
import com.example.gramavasathi.domain.model.Farmstay
import com.example.gramavasathi.domain.repository.FarmstayRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FarmstayRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmstayRepository {

    private val staysCollection = firestore.collection("stays")

    private fun fallbackImageUrlForStayName(name: String): String {
        return when (name.trim()) {
            "Nandini Family Farmstay" ->
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800"
            "Reddy Mane Agri-Home" ->
                "https://images.unsplash.com/photo-1510798831971-661eb04b3739?w=800"
            "Gowda Lake Farmhouse" ->
                "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800"
            "Patil Heritage Homestay" ->
                "https://images.unsplash.com/photo-1518602164578-cd0074062767?w=800"
            else -> ""
        }
    }

    private fun fallbackGalleryUrlsForStayName(name: String): List<String> {
        return when (name.trim()) {
            "Nandini Family Farmstay" -> listOf(
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?w=600",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600"
            )
            "Reddy Mane Agri-Home" -> listOf(
                "https://images.unsplash.com/photo-1502673530728-f79b254cabef?w=600",
                "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600",
                "https://images.unsplash.com/photo-1523217582564-09d865d498bd?w=600"
            )
            "Gowda Lake Farmhouse" -> listOf(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600",
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600",
                "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=600"
            )
            "Patil Heritage Homestay" -> listOf(
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600",
                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=600",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=600"
            )
            else -> emptyList()
        }
    }

    private fun enrichStayFromFirestore(
        doc: DocumentSnapshot,
        stay: Farmstay
    ): Farmstay {
        val fbUrl = fallbackImageUrlForStayName(stay.name)
        val fbGallery = fallbackGalleryUrlsForStayName(stay.name)
        val updates = mutableMapOf<String, Any>()
        var out = stay.copy(id = doc.id)
        if (fbUrl.isNotBlank() && out.imageUrl != fbUrl) {
            updates["imageUrl"] = fbUrl
            out = out.copy(imageUrl = fbUrl)
        }
        if (fbGallery.isNotEmpty() && out.imageGalleryUrls.isEmpty()) {
            updates["imageGalleryUrls"] = fbGallery
            out = out.copy(imageGalleryUrls = fbGallery)
        }
        if (updates.isNotEmpty()) {
            doc.reference.update(updates)
        }
        return out
    }

    init {
        // Run seed check on initialization
        seedDatabaseIfEmpty()
    }

    private fun seedDatabaseIfEmpty() {
        staysCollection.limit(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d("FarmstayRepository", "Collection is empty, seeding initial data...")
                val batch = firestore.batch()

                val seedData = listOf(
                    "stay_nandini_coorg" to Farmstay(
                        name = "Nandini Family Farmstay",
                        location = "Coorg, Karnataka",
                        district = "Kodagu",
                        distanceKm = 42,
                        pricePerNight = 1200.0,
                        rating = 4.8,
                        isVerified = true,
                        hostName = "Nandini Krishnamurthy",
                        activities = listOf("Cow Milking", "Local Cooking", "Birdwatching", "Organic Farming"),
                        amenities = listOf("Western Toilet", "Safe Water", "Clean Linen", "WiFi"),
                        unavailableDates = listOf("2026-05-14", "2026-05-21"),
                        imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
                        imageGalleryUrls = listOf(
                            "https://images.unsplash.com/photo-1582719508461-905c673771fd?w=600",
                            "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600",
                            "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600"
                        ),
                        description = "Authentic coffee estate stay in the heart of Coorg."
                    ),
                    "stay_reddy_hampi" to Farmstay(
                        name = "Reddy Mane Agri-Home",
                        location = "Hampi, Karnataka",
                        district = "Vijayanagara",
                        distanceKm = 8,
                        pricePerNight = 950.0,
                        rating = 4.6,
                        isVerified = true,
                        hostName = "Ramesh Reddy",
                        activities = listOf("Field Plowing", "Birdwatching", "Trekking"),
                        amenities = listOf("Western Toilet", "Safe Water", "Clean Linen"),
                        unavailableDates = listOf("2026-05-18", "2026-05-19"),
                        imageUrl = "https://images.unsplash.com/photo-1510798831971-661eb04b3739?w=800",
                        imageGalleryUrls = listOf(
                            "https://images.unsplash.com/photo-1502673530728-f79b254cabef?w=600",
                            "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600",
                            "https://images.unsplash.com/photo-1523217582564-09d865d498bd?w=600"
                        ),
                        description = "Stay beside the ancient Hampi ruins on a working farm."
                    ),
                    "stay_gowda_chikmagalur" to Farmstay(
                        name = "Gowda Lake Farmhouse",
                        location = "Chikmagalur, Karnataka",
                        district = "Chikmagalur",
                        distanceKm = 12,
                        pricePerNight = 1450.0,
                        rating = 4.9,
                        isVerified = true,
                        hostName = "Sunitha Gowda",
                        activities = listOf("Birdwatching", "Coffee Picking", "Trekking", "Organic Farming"),
                        amenities = listOf("Western Toilet", "Safe Water", "Clean Linen", "Hot Water", "WiFi"),
                        unavailableDates = listOf("2026-05-10", "2026-05-25"),
                        imageUrl = "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800",
                        imageGalleryUrls = listOf(
                            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600",
                            "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=600",
                            "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=600"
                        ),
                        description = "Premium coffee estate homestay in the Western Ghats."
                    ),
                    "stay_patil_dharwad" to Farmstay(
                        name = "Patil Heritage Homestay",
                        location = "Dharwad, Karnataka",
                        district = "Dharwad",
                        distanceKm = 5,
                        pricePerNight = 800.0,
                        rating = 4.4,
                        isVerified = true,
                        hostName = "Mallikarjun Patil",
                        activities = listOf("Pottery", "Local Cooking", "Field Plowing"),
                        amenities = listOf("Western Toilet", "Safe Water", "Clean Linen"),
                        unavailableDates = listOf("2026-05-16", "2026-05-17"),
                        imageUrl = "https://images.unsplash.com/photo-1518602164578-cd0074062767?w=800",
                        imageGalleryUrls = listOf(
                            "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600",
                            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=600",
                            "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=600"
                        ),
                        description = "A heritage home in North Karnataka. Learn traditional pottery."
                    )
                )

                seedData.forEach { (docId, stay) ->
                    val docRef = staysCollection.document(docId)
                    val stayWithId = stay.copy(id = docId)
                    batch.set(docRef, stayWithId)
                }

                batch.commit().addOnSuccessListener {
                    Log.d("FarmstayRepository", "Seeding complete!")
                }.addOnFailureListener { e ->
                    Log.e("FarmstayRepository", "Seeding failed", e)
                }
            }
        }
    }

    override fun getFarmstays(filter: String): Flow<List<Farmstay>> = 
        callbackFlow {
            val query = if (filter == "All") {
                firestore.collection("stays")
            } else {
                firestore.collection("stays")
                    .whereArrayContains("activities", filter)
            }
            
            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val farmstays = snapshot?.documents?.mapNotNull { doc ->
                    val stay = doc.toObject(Farmstay::class.java)?.copy(id = doc.id) ?: return@mapNotNull null
                    enrichStayFromFirestore(doc, stay)
                } ?: emptyList()
                trySend(farmstays)
            }
            awaitClose { listener.remove() }
        }

    override fun getFarmstayById(id: String): Flow<Farmstay?> = callbackFlow {
        val docRef = staysCollection.document(id)
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val stay = snapshot.toObject(Farmstay::class.java)
                if (stay != null) {
                    trySend(enrichStayFromFirestore(snapshot, stay.copy(id = snapshot.id)))
                } else {
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }

    override fun searchFarmstays(query: String, district: String?): Flow<List<Farmstay>> = flow {
        // Note: Firestore doesn't support full-text search directly.
        // For a production app, Algolia or similar is recommended.
        // Here we fetch all and filter locally for simplicity since data is small.
        val snapshot = staysCollection.get().await()
        val farmstays = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Farmstay::class.java)?.copy(id = doc.id)
        }

        val filtered = farmstays.filter { stay ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                stay.name.contains(query, ignoreCase = true) ||
                stay.location.contains(query, ignoreCase = true) ||
                stay.activities.any { it.contains(query, ignoreCase = true) }
            }

            val matchesDistrict = if (district.isNullOrBlank() || district == "All") {
                true
            } else {
                stay.location.contains(district, ignoreCase = true)
            }

            matchesQuery && matchesDistrict
        }
        emit(filtered)
    }
}
