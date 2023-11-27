package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        return try {
            val accountCreated =
                authenticationService.createAccount(userSignIn.email, userSignIn.password)
            if (accountCreated != null) {
                userService.createUserTable(userSignIn, accountCreated.user!!.uid)
                true
            } else {
                false
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            false
        }
    }
}