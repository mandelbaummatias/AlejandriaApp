package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser
import javax.inject.Inject


private const val TAG = "UsersRepositoryImpl"

class UsersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userService: UserService
) : UsersRepository {

    override suspend fun addSubscriptionId(subscriptionId: String, userId: String): Boolean {
        return try {
            userService.addSubscriptionId(subscriptionId, userId)
            true
        } catch (e: Exception) {
            Log.d(TAG, "failure on update MP ${e.message}")
            false
        }
    }

    override suspend fun changeImageForUser(newImage: String): Result<Unit> =
        userService.changeImageForUser(newImage)


    override suspend fun updateUserReservationState(userEmail: String): Result<Unit> =
        userService.updateUserReservationState(userEmail)

    override suspend fun updateUserProfile(
        name: String,
        lastName: String,
        userEmail: String,
        birthDate: String
    ): Result<Unit> = userService.updateUserProfile(name, lastName, userEmail, birthDate)


    override suspend fun updateUserEmail(
        newEmail: String,
        previousEmail: String,
        pass: String
    ) = userService.updateUserEmail(newEmail, previousEmail, pass)


    override suspend fun getUserById(userId: String): Result<SubscriptionUser> {
        return userService.getUserById(userId)

    }

    override suspend fun getUserByEmail(email: String): Result<UserProfile> =
        userService.getUserByEmail(email)

}
