package com.kuit.week6.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuit.week6.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

//미션
@HiltViewModel
class SplashViewModel @Inject constructor(
    repo: AuthRepository,
) : ViewModel() {

    // null = 아직 모름, true = 로그인됨, false = 로그인 안 됨
    val isLoggedIn: StateFlow<Boolean?> = repo.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}