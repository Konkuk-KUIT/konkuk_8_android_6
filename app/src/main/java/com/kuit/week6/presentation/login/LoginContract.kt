package com.kuit.week6.presentation.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,       // true 가 되면 App 이 Profile 로 이동
)

sealed interface LoginIntent {
    data class Submit(val username: String, val password: String) : LoginIntent
}
