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

class UpdateSubscriptionUserReservationStateUseCaseTestProfile {

    @RelaxedMockK
    private lateinit var usersRepository: UsersRepository

    lateinit var updateUserReservationStateUseCase: UpdateUserReservationStateUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        updateUserReservationStateUseCase = UpdateUserReservationStateUseCase(usersRepository)
    }

    @Test
    fun `when updateUserReservationState is successful`() = runBlocking {
        // Given
        val userEmail = "test@example.com"
        coEvery { usersRepository.updateUserReservationState(userEmail) } returns Result.Success(Unit)

        // When
        val response = updateUserReservationStateUseCase(userEmail)

        // Then
        assert(response is Result.Success)
        assertEquals(Unit, (response as Result.Success).data)
    }

    @Test
    fun `when updateUserReservationState fails`() = runBlocking {
        // Given
        val userEmail = "test@example.com"
        coEvery { usersRepository.updateUserReservationState(userEmail) } returns Result.Error("Update failed")

        // When
        val response = updateUserReservationStateUseCase(userEmail)

        // Then
        assert(response is Result.Error)
        assertEquals("Update failed", (response as Result.Error).message)
    }
}
