package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import com.matiasmandelbaum.alejandriaapp.data.signin.response.LoginResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    suspend operator fun invoke(email: String, password: String): LoginResult =
        authenticationService.login(email, password)
}