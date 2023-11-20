package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.user.User
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile


interface UsersRepository {

    suspend fun getUserById(userId: String): Result<com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User>
    suspend fun getUserByEmail(email: String): Result<UserProfile>

    suspend fun getAllUsers(): Result<List<User>>

    suspend fun updateUserReservationState(userEmail: String): Result<Unit>

    suspend fun updateUserProfile(name: String, lastName: String, userEmail: String): Result<Unit>

    suspend fun updateUserEmail(newEmail:String, previousEmail: String, pass: String): Result<Unit>

    suspend fun addSubscriptionId(subscriptionId: String, userId: String): Boolean


}