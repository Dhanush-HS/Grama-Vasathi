package com.example.gramavasathi.domain.model

import java.util.Date

data class Guest(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val homeCity: String = "",
    val savedStayIds: List<String> = emptyList(),
    val totalStays: Int = 0,
    val createdAt: Date? = null
)
