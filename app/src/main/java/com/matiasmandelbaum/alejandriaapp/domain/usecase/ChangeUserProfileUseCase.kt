package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class ChangeUserProfileUseCase @Inject constructor(private val usersRepository: UsersRepository) {

    suspend operator fun invoke(
        name: String,
        lastName: String,
        email: String
    ) = usersRepository.updateUserProfile(name, lastName, email)
}