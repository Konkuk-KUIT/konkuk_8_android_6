package com.kuit.week6.presentation.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface LoginIntent {
    data class Submit(val username: String, val password: String) : LoginIntent
}
