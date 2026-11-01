package com.kuit.week6.data.auth

import com.kuit.week6.domain.auth.AuthRepository
import com.kuit.week6.domain.auth.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// 토큰은 여기(Data)까지만 온다. ViewModel · 화면은 토큰을 모른다.
class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val tokenStore: TokenStore,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenStore.accessToken.map { it != null }

    override suspend fun login(username: String, password: String) {
        val res = service.login(LoginRequest(username, password))
        tokenStore.save(res.accessToken, res.refreshToken)
    }

    // 헤더는 AuthInterceptor 가 붙인다. 여기서는 토큰을 만지지 않는다.
    override suspend fun me(): User = service.me().toUser()

    override suspend fun logout() = tokenStore.clear()

    override suspend fun breakAccessToken() {
        val refresh = tokenStore.refreshToken.first() ?: return
        tokenStore.save(access = "broken", refresh = refresh)
    }
}
