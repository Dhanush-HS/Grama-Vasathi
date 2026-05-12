package com.example.gramavasathi.domain.model

import java.util.Date

data class Booking(
    val id: String = "",
    val stayId: String = "",
    val guestUid: String = "",
    val stayName: String = "",
    val stayImageUrl: String = "",
    val stayLocation: String = "",
    val guestName: String = "",
    val guestEmail: String = "",
    val hostUid: String = "",
    val hostName: String = "",
    val checkIn: String = "",
    val checkOut: String = "",
    val nights: Int = 0,
    val pricePerNight: Int = 0,
    val roomCost: Int = 0,
    val activitiesCost: Int = 500,
    val totalCost: Int = 0,
    val status: String = "confirmed",
    val createdAt: Date? = null
)
