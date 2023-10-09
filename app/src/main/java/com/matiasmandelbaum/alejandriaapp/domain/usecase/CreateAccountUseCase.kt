package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        val accountCreated =
            authenticationService.createAccount(userSignIn.email, userSignIn.password) != null
        return if (accountCreated) {
            true
            userService.createUserTable(userSignIn)
        } else {
            false
        }
    }
}