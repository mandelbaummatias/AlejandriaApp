package com.matiasmandelbaum.alejandriaapp.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class AddSubscriptionIdToSubscriptionUserUseCaseTestProfile {

    private lateinit var usersRepository: UsersRepository
    private lateinit var addSubscriptionIdToUserUseCase: AddSubscriptionIdToUserUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        usersRepository = mockk()
        addSubscriptionIdToUserUseCase = AddSubscriptionIdToUserUseCase(usersRepository)
    }

    @Test
    fun `when adding subscription ID to user is successful`() = runBlocking {
        // Given
        val subscriptionId = "testSubscriptionId"
        val userId = "testUserId"

        coEvery { usersRepository.addSubscriptionId(subscriptionId, userId) } returns true

        // When
        val result = addSubscriptionIdToUserUseCase(subscriptionId, userId)

        // Then
        assertEquals(true, result)
    }

    @Test
    fun `when adding subscription ID to user fails`() = runBlocking {
        // Given
        val subscriptionId = "testSubscriptionId"
        val userId = "testUserId"

        coEvery { usersRepository.addSubscriptionId(subscriptionId, userId) } returns false

        // When
        val result = addSubscriptionIdToUserUseCase(subscriptionId, userId)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `when adding subscription ID to user throws FirebaseAuthUserCollisionException`() = runBlocking {
        // Given
        val subscriptionId = "testSubscriptionId"
        val userId = "testUserId"

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        val exception = mockk<FirebaseAuthUserCollisionException>()
        coEvery { usersRepository.addSubscriptionId(subscriptionId, userId) } throws exception

        // When
        val result = addSubscriptionIdToUserUseCase(subscriptionId, userId)

        // Then
        assertEquals(false, result)
    }
}