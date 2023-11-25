package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class ChangeImageForUserUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(newImage: String) = usersRepository.changeImageForUser(newImage)
}