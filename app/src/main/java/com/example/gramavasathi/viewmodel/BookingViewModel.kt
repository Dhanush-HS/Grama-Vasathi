package com.example.gramavasathi.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.Farmstay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val stayId: String = savedStateHandle.get<String>("stayId") ?: ""

    private val _stay = MutableStateFlow<Farmstay?>(null)
    val stay: StateFlow<Farmstay?> = _stay.asStateFlow()

    private val _checkInDate = MutableStateFlow<LocalDate?>(null)
    val checkInDate: StateFlow<LocalDate?> = _checkInDate.asStateFlow()

    private val _checkOutDate = MutableStateFlow<LocalDate?>(null)
    val checkOutDate: StateFlow<LocalDate?> = _checkOutDate.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    val totalCost: StateFlow<Int> = combine(
        _checkInDate, _checkOutDate, _stay
    ) { checkIn, checkOut, stay ->
        if (checkIn != null && checkOut != null && stay != null) {
            val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
            (nights * stay.pricePerNight).toInt() + 500
        } else 0
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val numberOfNights: StateFlow<Int> = combine(
        _checkInDate, _checkOutDate
    ) { checkIn, checkOut ->
        if (checkIn != null && checkOut != null) {
            ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
        } else 0
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        if (stayId.isNotEmpty()) {
            loadStay(stayId)
        }
    }

    fun loadStay(stayId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("stays").document(stayId).get().await()
                _stay.value = doc.toObject(Farmstay::class.java)?.copy(id = doc.id)
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error("Failed to load stay: ${e.message}")
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        val checkIn = _checkInDate.value
        val checkOut = _checkOutDate.value

        if (checkIn == null) {
            _checkInDate.value = date
        } else if (checkOut == null) {
            if (date.isAfter(checkIn)) {
                _checkOutDate.value = date
            } else {
                // If selected date is before checkIn, reset checkIn
                _checkInDate.value = date
                _checkOutDate.value = null
            }
        } else {
            // Third tap resets
            _checkInDate.value = date
            _checkOutDate.value = null
        }
    }

    fun confirmBooking() {
        val checkIn = _checkInDate.value ?: return
        val checkOut = _checkOutDate.value ?: return
        val currentStay = _stay.value ?: return
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            _bookingState.value = BookingState.Error("Please sign in to book a stay")
            return
        }

        viewModelScope.launch {
            _bookingState.value = BookingState.Loading
            try {
                val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
                val roomCost = (nights * currentStay.pricePerNight).toInt()
                val total = roomCost + 500

                // Generate booked dates list
                val allBookedDates = mutableListOf<String>()
                var currentDate = checkIn
                while (!currentDate.isAfter(checkOut)) { // Includes checkout date as unavailable
                    allBookedDates.add(currentDate.toString())
                    currentDate = currentDate.plusDays(1)
                }

                // 1. Create booking
                val bookingData = hashMapOf(
                    "stayId" to (currentStay.id.ifBlank { stayId }),
                    "stayName" to currentStay.name,
                    "stayImageUrl" to currentStay.imageUrl,
                    "stayLocation" to currentStay.location,
                    "guestUid" to currentUser.uid,
                    "guestName" to (currentUser.displayName ?: ""),
                    "guestEmail" to (currentUser.email ?: ""),
                    "hostUid" to currentStay.hostUid,
                    "hostName" to currentStay.hostName,
                    "checkIn" to checkIn.toString(),
                    "checkOut" to checkOut.toString(),
                    "nights" to nights,
                    "pricePerNight" to currentStay.pricePerNight.toInt(),
                    "roomCost" to roomCost,
                    "activitiesCost" to 500,
                    "totalCost" to total,
                    "status" to "confirmed",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                firestore.collection("bookings").add(bookingData).await()

                // 2. Update stay unavailableDates
                firestore.collection("stays")
                    .document(currentStay.id.ifBlank { stayId })
                    .update("unavailableDates", FieldValue.arrayUnion(*allBookedDates.toTypedArray()))
                    .await()

                // 3. Update guest totalStays
                firestore.collection("guests")
                    .document(currentUser.uid)
                    .update("totalStays", FieldValue.increment(1))
                    .await()

                _bookingState.value = BookingState.Success
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.message ?: "Failed to save booking")
            }
        }
    }
}

sealed class BookingState {
    object Idle : BookingState()
    object Loading : BookingState()
    object Success : BookingState()
    data class Error(val message: String) : BookingState()
}
