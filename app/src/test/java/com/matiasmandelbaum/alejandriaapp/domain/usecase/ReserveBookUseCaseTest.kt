package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ReserveBookUseCaseTest {

    @RelaxedMockK
    private lateinit var booksRepository: BooksRepository

    lateinit var reserveBookUseCase: ReserveBookUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        reserveBookUseCase = ReserveBookUseCase(booksRepository)
    }

    @Test
    fun `when reserving a book is successful, return Result Success`() = runBlocking {
        // Given
        val isbn = "ISBN123"
        val userEmail = "user@example.com"
        val quantity = 1

        coEvery { booksRepository.reserveBook(isbn, userEmail) } returns Result.Success(
            ReservationResult(userEmail, isbn)
        )

        // When
        val result = reserveBookUseCase(isbn, userEmail)

        // Then
        assert(result is Result.Success)
        val reservationResult = (result as Result.Success).data
        assertEquals(userEmail, reservationResult.userEmail)
        assertEquals(isbn, reservationResult.bookIsbn)
    }

    @Test
    fun `when reserving a book fails, return Result Error`() = runBlocking {
        // Given
        val isbn = "ISBN123"
        val userEmail = "user@example.com"
        val quantity = 1

        coEvery { booksRepository.reserveBook(isbn, userEmail) } throws Exception("Reservation failed")

        // When
        val result = reserveBookUseCase(isbn, userEmail)

        // Then
        assert(result is Result.Error)
        val errorMessage = (result as Result.Error).message
        assertEquals("Error reserving book: Reservation failed", errorMessage)
    }
}