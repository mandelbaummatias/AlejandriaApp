package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyEmailBeforeUpdateUseCase @Inject constructor(private val authenticationService: AuthenticationService) {

    operator fun invoke(newEmail: String): Flow<Boolean> = authenticationService.verifiedAccount2(newEmail)

}
