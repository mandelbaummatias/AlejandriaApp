package com.matiasmandelbaum.alejandriaapp.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import javax.inject.Inject

private const val TAG = "GetUserBySubscriptionIdUseCase"

class GetUserBySubscriptionIdUseCase @Inject constructor(
    private val userService: UserService
) {

    //    suspend operator fun invoke(subscriptionId: String, userId: String): Result<Boolean> {
//        return try {
//            val coso = userService.getUserBySubscriptionId(subscriptionId, userId)
//            Result.Success(coso)
//        } catch (e: FirebaseAuthUserCollisionException) {
//            Log.d(TAG, "Exception $e")
//            // Handle the exception here
//            // For example, you can show an error message to the user
//            // indicating that the email is already in use.
//            Result.Error(e.message!!)
//        }
//    }
    suspend operator fun invoke(subscriptionId: String, userId: String): Boolean {
        return try {
            val coso = userService.getUserBySubscriptionId(subscriptionId, userId)
            coso
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.d(TAG, "Exception $e")
            // Handle the exception here
            // For example, you can show an error message to the user
            // indicating that the email is already in use.
            false
        }
    }

}