package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.Booking
import com.example.gramavasathi.domain.model.Guest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    private val _guestProfile = MutableStateFlow<Guest?>(null)
    val guestProfile: StateFlow<Guest?> = _guestProfile.asStateFlow()

    private val _pastBookings = MutableStateFlow<List<Booking>>(emptyList())
    val pastBookings: StateFlow<List<Booking>> = _pastBookings.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _staysCount = MutableStateFlow(0)
    val staysCount: StateFlow<Int> = _staysCount.asStateFlow()

    private val _savedCount = MutableStateFlow(0)
    val savedCount: StateFlow<Int> = _savedCount.asStateFlow()

    private var bookingsListener: ListenerRegistration? = null

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val uid = currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                // Fetch Guest Profile
                val guestDoc = firestore.collection("guests").document(uid).get().await()
                if (guestDoc.exists()) {
                    val guest = guestDoc.toObject(Guest::class.java)
                    _guestProfile.value = guest
                    _savedCount.value = guest?.savedStayIds?.size ?: 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        bookingsListener?.remove()
        bookingsListener = firestore.collection("bookings")
            .whereEqualTo("guestUid", FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val bookings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Booking::class.java)
                        ?.copy(id = doc.id)
                } ?: emptyList()
                _pastBookings.value = bookings
                _staysCount.value = bookings.count { it.status == "confirmed" }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                // Step 1 — Update booking status to cancelled
                firestore.collection("bookings")
                    .document(bookingId)
                    .update("status", "cancelled")
                    .await()

                // Step 2 — Get the booking to find which dates to free up
                val bookingDoc = firestore
                    .collection("bookings")
                    .document(bookingId)
                    .get().await()

                val stayId = bookingDoc.getString("stayId") ?: return@launch
                val checkIn = bookingDoc.getString("checkIn") ?: return@launch
                val checkOut = bookingDoc.getString("checkOut") ?: return@launch

                // Step 3 — Remove dates from stay unavailableDates
                val datesToFree = generateDateRange(checkIn, checkOut)
                firestore.collection("stays")
                    .document(stayId)
                    .update(
                        "unavailableDates",
                        FieldValue.arrayRemove(*datesToFree.toTypedArray())
                    )
                    .await()

                // Step 4 — Decrease guest totalStays
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    firestore.collection("guests")
                        .document(currentUser.uid)
                        .update("totalStays", FieldValue.increment(-1))
                        .await()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to cancel booking: ${e.message}"
            }
        }
    }

    private fun generateDateRange(
        checkIn: String,
        checkOut: String
    ): List<String> {
        return try {
            val start = java.time.LocalDate.parse(checkIn)
            val end = java.time.LocalDate.parse(checkOut)
            val dates = mutableListOf<String>()
            var current = start
            while (!current.isAfter(end)) {
                dates.add(current.toString())
                current = current.plusDays(1)
            }
            dates
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCleared() {
        bookingsListener?.remove()
        bookingsListener = null
        super.onCleared()
    }
}
