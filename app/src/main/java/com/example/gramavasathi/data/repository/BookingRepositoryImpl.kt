package com.example.gramavasathi.data.repository

import com.example.gramavasathi.domain.model.Booking
import com.example.gramavasathi.domain.repository.BookingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor() : BookingRepository {

    override fun getUnavailableDates(farmstayId: String): Flow<List<Long>> = flow {
        // Simulate fetching blocked dates from Firestore
        // For demonstration, let's block tomorrow and the day after tomorrow
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = cal.timeInMillis
        
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val dayAfterTomorrow = cal.timeInMillis
        
        emit(listOf(tomorrow, dayAfterTomorrow))
    }

    override suspend fun createBooking(booking: Booking): Boolean {
        // Simulate network delay for writing to Firestore
        delay(1500)
        return true // Success
    }
}
