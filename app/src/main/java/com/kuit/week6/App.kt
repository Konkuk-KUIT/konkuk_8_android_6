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
import com.kuit.week6.presentation.products.ProductsScreen
import com.kuit.week6.presentation.products.ProductsViewModel
import com.kuit.week6.presentation.profile.ProfileScreen
import com.kuit.week6.presentation.profile.ProfileViewModel
import com.kuit.week6.presentation.splash.SplashScreen
import com.kuit.week6.presentation.splash.SplashViewModel
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object Login : NavKey
@Serializable data object Profile : NavKey
@Serializable data object Products : NavKey

@Composable
fun App() {
    val backStack = rememberNavBackStack(Splash)

    val splashViewModel: SplashViewModel = hiltViewModel()
    val isLoggedIn by splashViewModel.isLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn) {
        val root = when (isLoggedIn) {
            true -> Profile
            false -> Login
            null -> return@LaunchedEffect
        }
        if (backStack.firstOrNull() != root) {
            backStack.clear()
            backStack.add(root)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Splash> {
                SplashScreen()
            }

            entry<Login> {
                val vm: LoginViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                LoginScreen(state = state, onIntent = vm::onIntent)
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

            entry<Products> {
                val vm: ProductsViewModel = hiltViewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                ProductsScreen(
                    state = state,
                    onIntent = vm::onIntent,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        }
    )
}
