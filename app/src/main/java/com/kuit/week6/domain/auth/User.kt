package com.kuit.week6.domain.auth

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val name: String,
    val imageUrl: String? = null,
)
