package com.kuit.week6.data.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

// 요청이 나가기 직전에 한 번 호출된다. 여기서 헤더를 붙이면 모든 요청에 붙는다.
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Interceptor 는 suspend 가 아니다. 이 자리에서만 runBlocking 을 허용한다 (OkHttp 스레드).
        val token = runBlocking { tokenStore.accessToken.first() }

        // [TODO 2] token 이 null 이 아니면 "Authorization: Bearer <token>" 헤더를 붙인 새 요청으로 proceed.
        //          null 이면 원래 요청 그대로 proceed. (로그인 · refresh 요청은 토큰 없이 나간다)
        val request = chain.request()
        if (token == null || request.url.encodedPath.endsWith("auth/refresh")) {
            return chain.proceed(request)
        }
        val newRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}
