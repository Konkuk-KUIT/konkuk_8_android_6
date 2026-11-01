package com.kuit.week6.data.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

// OkHttp 가 401 응답을 받았을 때만 호출한다.
// 새 Request 를 돌려주면 그걸로 한 번 더 시도하고, null 을 돌려주면 401 을 그대로 앱에 넘긴다.
class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authService: Provider<AuthService>,   // Provider: OkHttp → Retrofit → AuthService 순환 의존을 끊는다
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // [TODO 3-a] 이미 재시도한 응답이면 포기 (무한 루프 방지)
        //            힌트: response.priorResponse != null → return null

        // [TODO 3-b] refreshToken 을 읽어 authService.get().refresh(...) 호출.
        //            실패(예외)하면 tokenStore.clear() 후 return null  → 앱은 401 을 받고 로그아웃 처리
        //            성공하면 tokenStore.save(새 access, 새 refresh)

        // [TODO 3-c] 원래 요청에 새 토큰 헤더를 얹어 반환
        //            response.request.newBuilder().header("Authorization", "Bearer $new").build()
        return null
    }
}
