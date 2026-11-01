package com.kuit.week6.data.auth

import com.kuit.week6.domain.auth.User
import kotlinx.serialization.Serializable

// DummyJSON 인증 API 응답 모양 그대로. https://dummyjson.com/docs/auth
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val expiresInMins: Int = 1,      // 실습용: 1분 뒤 만료 → 401 → 재발급 흐름을 바로 본다
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
    val expiresInMins: Int = 1,
)

@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String? = null,
)

fun UserDto.toUser(): User = User(
    id = id,
    username = username,
    email = email,
    name = "$firstName $lastName",
    imageUrl = image,
)
