package com.kuit.week6.domain.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>                 // 토큰 유무. 미션 B 에서 사용
    suspend fun login(username: String, password: String)
    suspend fun me(): User
    suspend fun logout()
    suspend fun breakAccessToken()                // 실습 확인용: Access 토큰을 일부러 망가뜨린다
}
