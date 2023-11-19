package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.User
import javax.inject.Inject

class GetUserByEmailUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(email: String): Result<User> = usersRepository.getUserByEmail(email)
}