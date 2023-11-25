package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class GetUserByEmailUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(email: String): Result<UserProfile> =
        usersRepository.getUserByEmail(email)
}