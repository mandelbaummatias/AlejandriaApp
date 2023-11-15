package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CreateReservationUseCaseTest {

    @RelaxedMockK
    private lateinit var reservationsRepository: ReservationsRepository

    lateinit var createReservationUseCase: CreateReservationUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        createReservationUseCase = CreateReservationUseCase(reservationsRepository)
    }

    @Test
    fun `when createReservation is successful`() = runBlocking {
        // Given
        val isbn = "testIsbn"
        val userEmail = "test@example.com"
        coEvery {
            reservationsRepository.createReservation(
                isbn,
                userEmail
            )
        } returns Result.Success(Unit)

        // When
        val response = createReservationUseCase(isbn, userEmail)

        // Then
        assert(response is Result.Success)
        assertEquals(Unit, (response as Result.Success).data)
    }

    @Test
    fun `when createReservation fails`() = runBlocking {
        // Given
        val isbn = "testIsbn"
        val userEmail = "test@example.com"
        coEvery {
            reservationsRepository.createReservation(
                isbn,
                userEmail
            )
        } returns Result.Error("Create reservation failed")

        // When
        val response = createReservationUseCase(isbn, userEmail)

        // Then
        assert(response is Result.Error)
        assertEquals("Create reservation failed", (response as Result.Error).message)
    }
}
