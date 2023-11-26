package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(email: String) = usersRepository.sendPasswordResetEmail(email)
}