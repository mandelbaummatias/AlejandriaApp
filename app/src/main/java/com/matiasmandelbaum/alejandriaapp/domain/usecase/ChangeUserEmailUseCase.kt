package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class ChangeUserEmailUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(
        newEmail: String,
        previousEmail: String,
        pass: String
    ) = usersRepository.updateUserEmail(newEmail, previousEmail, pass)
}
