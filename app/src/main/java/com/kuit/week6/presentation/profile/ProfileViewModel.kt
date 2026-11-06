package com.kuit.week6.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuit.week6.domain.auth.BreakAccessTokenUseCase
import com.kuit.week6.domain.auth.GetMeUseCase
import com.kuit.week6.domain.auth.LogoutUseCase
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
class ProfileViewModel @Inject constructor(
    private val getMe: GetMeUseCase,
    private val logout: LogoutUseCase,
    private val breakAccessToken: BreakAccessTokenUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> load()
            ProfileIntent.BreakToken -> viewModelScope.launch {
                breakAccessToken()
                _state.update { it.copy(lastEvent = "Access 토큰을 'broken' 으로 바꿨다. 새로고침하면 401 → 재발급 → 재시도가 일어나야 한다") }
            }
            ProfileIntent.Logout -> viewModelScope.launch { logout() }
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val user = getMe()
            _state.update { it.copy(isLoading = false, user = user, lastEvent = "GET /auth/me 200") }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            // Authenticator 가 재발급에 실패했을 때만 여기로 온다 (refresh 도 만료)
            val msg = if (e.code() == 401) "로그인이 만료되었습니다. 다시 로그인하세요" else "서버 오류 (${e.code()})"
            _state.update { it.copy(isLoading = false, error = msg, lastEvent = "GET /auth/me ${e.code()}") }
        } catch (e: IOException) {
            _state.update { it.copy(isLoading = false, error = "인터넷 연결을 확인하세요") }
        }
    }
}
