package com.example.gramavasathi.domain.model

data class User(
    val uid: String,
    val email: String,
    val name: String = "",
    val profilePictureUrl: String = ""
)
