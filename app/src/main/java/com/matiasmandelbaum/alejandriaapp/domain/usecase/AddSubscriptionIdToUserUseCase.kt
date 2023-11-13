package com.matiasmandelbaum.alejandriaapp.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

private const val TAG = "AddSubscriptionIdToUserUseCase"

//class AddSubscriptionIdToUserUseCase @Inject constructor(
//    private val userService: UserService
//) {
//    suspend operator fun invoke(subscriptionId: String, userId: String): Result<Boolean> {
//        return try {
//            userService.addSubsciptionId(subscriptionId, userId)
//            Result.Success(true)
//        } catch (e: FirebaseAuthUserCollisionException) {
//            Log.d(TAG, "Exception $e")
//            Result.Error(e.message.toString())
//        }
//    }
//}

class AddSubscriptionIdToUserUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(subscriptionId: String, userId: String): Boolean {
        return try {
            usersRepository.addSubscriptionId(subscriptionId, userId)
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.d(TAG, "Exception $e")
            // You can log the exception or handle it as needed
            false
        }
    }
}