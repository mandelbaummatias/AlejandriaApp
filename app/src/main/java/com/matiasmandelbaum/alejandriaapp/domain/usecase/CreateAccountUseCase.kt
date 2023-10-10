
package com.matiasmandelbaum.alejandriaapp.domain.usecase

import android.util.Log
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.AuthenticationService
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import javax.inject.Inject


private const val TAG = "CreateAccountUseCase"
class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {

    suspend operator fun invoke(userSignIn: UserSignIn): Boolean {
        try {
            val accountCreated =
                authenticationService.createAccount(userSignIn.email, userSignIn.password) != null
            return if (accountCreated) {
                true
                userService.createUserTable(userSignIn)
            } else {
                false
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.d(TAG, "Exception $e")
            // Handle the exception here
            // For example, you can show an error message to the user
            // indicating that the email is already in use.
            return false
        }
    }
}