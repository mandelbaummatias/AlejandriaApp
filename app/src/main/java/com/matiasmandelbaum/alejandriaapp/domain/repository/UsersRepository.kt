package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.user.User

interface UsersRepository {
    suspend fun getUserByEmail(email: String): User

    suspend fun getAllUsers(): Result<List<User>>
}