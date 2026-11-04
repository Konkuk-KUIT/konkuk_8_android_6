package com.kuit.week6.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuit.week6.domain.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Submit -> submit(intent.username, intent.password)
        }
    }

    private fun submit(username: String, password: String) = viewModelScope.launch {
        if (username.isBlank() || password.isBlank()) return@launch
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            login(username, password)
            _state.update { it.copy(isLoading = false) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            // DummyJSON: 틀린 계정 → 400 { "message": "Invalid credentials" }
            _state.update { it.copy(isLoading = false, error = if (e.code() == 400) "아이디 또는 비밀번호가 틀렸습니다" else "서버 오류 (${e.code()})") }
        } catch (e: IOException) {
            _state.update { it.copy(isLoading = false, error = "인터넷 연결을 확인하세요") }
        }
    }
}
