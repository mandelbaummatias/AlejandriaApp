package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile
import javax.inject.Inject

class ChangeUserProfileUseCase @Inject constructor(private val usersRepository: UsersRepository) {

//    suspend operator fun invoke(
//        name: String,
//        lastName: String,
//        email: String,
//        birthDate: String
//    ) = usersRepository.updateUserProfile(name, lastName, email, birthDate)

    suspend operator fun invoke(
        userProfile: UserProfile
    ) = usersRepository.updateUserProfile(userProfile.name, userProfile.lastName, userProfile.email!!, userProfile.birthDate)
}