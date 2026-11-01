package com.kuit.week6.domain.auth

import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(username: String, password: String) = repo.login(username, password)
}

class GetMeUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(): User = repo.me()
}

class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}

class BreakAccessTokenUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.breakAccessToken()
}
