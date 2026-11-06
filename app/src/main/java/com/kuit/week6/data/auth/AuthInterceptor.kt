package com.kuit.week6.data.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenStore.accessToken.first() }

        // [정답 2] 토큰이 있을 때만 헤더를 얹는다. Request 는 불변이라 newBuilder 로 새로 만든다.
        val request = if (token == null) chain.request()
        else chain.request().newBuilder().header("Authorization", "Bearer $token").build()
        return chain.proceed(request)
    }
}
