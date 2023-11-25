package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class UpdateUserReservationStateUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(userEmail: String): Result<Unit> =
        usersRepository.updateUserReservationState(userEmail)
}