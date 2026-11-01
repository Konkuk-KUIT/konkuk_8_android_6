package com.kuit.week6.data.auth

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 인증 관련 요청 선언. baseUrl 은 https://dummyjson.com/ (NetworkModule 의 Retrofit)
interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // Authorization 헤더 필수. 없거나 만료면 401 { "message": "Invalid/Expired Token!" }
    @GET("auth/me")
    suspend fun me(): UserDto

    // refreshToken 이 틀리면 403 { "message": "Invalid refresh token" }
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): RefreshResponse
}
