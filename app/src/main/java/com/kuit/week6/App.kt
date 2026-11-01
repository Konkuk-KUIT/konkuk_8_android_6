package com.kuit.week6

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.kuit.week6.presentation.login.LoginScreen
import com.kuit.week6.presentation.login.LoginViewModel
import com.kuit.week6.presentation.profile.ProfileScreen
import com.kuit.week6.presentation.profile.ProfileViewModel
import kotlinx.serialization.Serializable

@Serializable data object Login : NavKey
@Serializable data object Profile : NavKey

// 화면 2개. Login → (성공) → Profile. 토큰은 어느 화면도 모른다.
@Composable
fun App() {
    val backStack = rememberNavBackStack(Login)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Login> {
                val vm: LoginViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                // 로그인 성공 → Login 을 스택에서 빼고 Profile 로 (뒤로가기 시 로그인 화면이 다시 안 나오게)
                LaunchedEffect(state.success) {
                    if (state.success) { backStack.removeLastOrNull(); backStack.add(Profile) }
                }
                LoginScreen(state = state, onIntent = vm::onIntent)
            }

            entry<Profile> {
                val vm: ProfileViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                // 로그아웃 → Profile 을 빼고 Login 으로
                LaunchedEffect(state.loggedOut) {
                    if (state.loggedOut) { backStack.removeLastOrNull(); backStack.add(Login) }
                }
                ProfileScreen(state = state, onIntent = vm::onIntent)
            }
        }
    )
}
