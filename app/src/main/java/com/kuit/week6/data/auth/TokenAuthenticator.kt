package com.kuit.week6.data.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authService: Provider<AuthService>,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // [정답 3-a] 재시도한 요청이 또 401 이면 포기. 없으면 무한 루프.
        if (response.priorResponse != null) return null

        // [정답 3-b] refresh 요청. Authenticator 도 suspend 가 아니라 runBlocking (OkHttp 스레드).
        val refresh = runBlocking { tokenStore.refreshToken.first() } ?: return null
        val new = runBlocking {
            try { authService.get().refresh(RefreshRequest(refresh)) }
            catch (e: Exception) { null }          // 403 Invalid refresh token, 네트워크 오류 → null
        }
        if (new == null) {
            runBlocking { tokenStore.clear() }      // 둘 다 만료 → 로그아웃 상태. 앱은 401 을 받는다
            return null
        }
        runBlocking { tokenStore.save(new.accessToken, new.refreshToken) }

        // [정답 3-c] 원래 요청을 새 토큰으로 한 번 더
        return response.request.newBuilder()
            .header("Authorization", "Bearer ${new.accessToken}")
            .build()
    }
}
