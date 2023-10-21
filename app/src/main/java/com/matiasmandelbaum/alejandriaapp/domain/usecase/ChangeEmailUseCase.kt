package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import javax.inject.Inject

class ChangeEmailUseCase @Inject
constructor(
    private val authenticationService: AuthenticationService,
    val newEmail: String
) {
    suspend operator fun invoke() = authenticationService.verifyBeforeUpdateEmail(newEmail = newEmail)

}
