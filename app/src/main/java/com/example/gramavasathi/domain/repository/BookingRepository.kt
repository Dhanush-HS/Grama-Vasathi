package com.example.gramavasathi.domain.repository

import com.example.gramavasathi.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getUnavailableDates(farmstayId: String): Flow<List<Long>>
    suspend fun createBooking(booking: Booking): Boolean
}
