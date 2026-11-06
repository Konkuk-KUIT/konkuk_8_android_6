package com.kuit.week6

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.kuit.week6.presentation.products.ProductsScreen
import com.kuit.week6.presentation.products.ProductsViewModel
import com.kuit.week6.presentation.splash.SplashViewModel
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object Login : NavKey
@Serializable data object Profile : NavKey
@Serializable data object Products : NavKey

@Composable
fun App() {
    val backStack = rememberNavBackStack(Splash)

    // 로그인 상태가 바뀔 때마다 이동 (시작, 로그인 성공, 로그아웃 전부 여기서 처리)
    val authVm: SplashViewModel = hiltViewModel()
    val loggedIn by authVm.isLoggedIn.collectAsStateWithLifecycle()
    LaunchedEffect(loggedIn) {
        when (loggedIn) {
            true -> { backStack.clear(); backStack.add(Profile) }
            false -> { backStack.clear(); backStack.add(Login) }
            null -> Unit
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Splash> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            entry<Login> {
                val vm: LoginViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                LoginScreen(state = state, onIntent = vm::onIntent)
            }

            entry<Products> {
                val vm: ProductsViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                ProductsScreen(state = state, onIntent = vm::onIntent)
            }

            entry<Profile> {
                val vm: ProfileViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                ProfileScreen(
                    state = state,
                    onIntent = vm::onIntent,
                    onOpenProducts = { backStack.add(Products) },
                )
            }
        }
    )
}