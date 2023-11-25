package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
class GetSubscriptionUserByIdUseCaseTestProfile {

    @RelaxedMockK
    private lateinit var usersRepository: UsersRepository

    lateinit var getUserByIdUseCase: GetUserByIdUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getUserByIdUseCase = GetUserByIdUseCase(usersRepository)
    }

    @Test
    fun `when repository returns user then get user by id`() = runBlocking {
        // Given
        val userId = "user123"
        val subscriptionUser = com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser("123", false)
        coEvery { usersRepository.getUserById(userId) } returns Result.Success(subscriptionUser)

        // When
        val result = getUserByIdUseCase(userId)

        // Then
        assert(result is Result.Success)
        val returnedUser = (result as Result.Success).data
        assertEquals(subscriptionUser, returnedUser)
    }

    @Test
    fun `when repository returns an error then return an error`() = runBlocking {
        // Given
        val userId = "user123"
        val error = "User not found"
        coEvery { usersRepository.getUserById(userId) } returns Result.Error(error)

        // When
        val result = getUserByIdUseCase(userId)

        // Then
        assert(result is Result.Error)
        val returnedError = (result as Result.Error).message
        assertEquals(error, returnedError)
    }
}
