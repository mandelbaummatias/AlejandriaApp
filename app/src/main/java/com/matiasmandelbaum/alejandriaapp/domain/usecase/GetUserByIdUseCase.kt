package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(userId: String): Result<SubscriptionUser> {
        return usersRepository.getUserById(userId)
    }

}