package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.model.userinput.UserDataInput
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import javax.inject.Inject

class ChangeUserProfileUseCase @Inject constructor(private val usersRepository: UsersRepository) {
    suspend operator fun invoke(
        userDataInput: UserDataInput
    ) = usersRepository.updateUserProfile(userDataInput.name, userDataInput.lastName, userDataInput.email!!, userDataInput.birthDate)
}