package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import javax.inject.Inject

private const val TAG = "GetUserByIdUseCase"
class GetUserByIdUseCase @Inject constructor(
    private val userService: UserService
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return userService.getUserById(userId)
    }

}