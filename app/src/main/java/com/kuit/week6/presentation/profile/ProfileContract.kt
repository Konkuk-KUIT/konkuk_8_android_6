package com.kuit.week6.presentation.profile

import com.kuit.week6.domain.auth.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val loggedOut: Boolean = false,     // true 가 되면 App 이 뒤로 이동
    val lastEvent: String? = null,      // 실습 확인용 로그 한 줄
)

sealed interface ProfileIntent {
    data object Load : ProfileIntent
    data object BreakToken : ProfileIntent   // Access 토큰을 망가뜨린 뒤 Load → 401 → Authenticator 동작 확인
    data object Logout : ProfileIntent
}
