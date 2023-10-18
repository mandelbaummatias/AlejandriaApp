package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    suspend operator fun invoke() = authenticationService.sendVerificationEmail()
}