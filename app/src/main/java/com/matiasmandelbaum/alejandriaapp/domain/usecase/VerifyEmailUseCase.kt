package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    operator fun invoke(): Flow<Boolean> = authenticationService.verifiedAccount

}