package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class GetAllBooksUseCaseTest {

    @RelaxedMockK
    private lateinit var booksRepository: BooksRepository

    lateinit var getAllBooksUseCase: GetAllBooksUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getAllBooksUseCase = GetAllBooksUseCase(booksRepository)
    }

    @Test
    fun `when the api returns something then get values from api`() = runBlocking {
        // Given
        val myList = listOf(
            Book(
                "Autor Aleatorio",
                "Título Aleatorio",
                "ISBN Aleatorio",
                "Descripción Aleatoria",
                4.5,
                null,
                10
            )
        )
        coEvery { booksRepository.getAllBooks() } returns Result.Success(myList)

        // When
        val response = getAllBooksUseCase()

        // Then
        assert(response is Result.Success)
        val books = (response as Result.Success).data
        assertEquals(myList, books)
    }

    @Test
    fun `when the API returns nothing then get an empty list`() = runBlocking {
        //Given
        coEvery { booksRepository.getAllBooks() } returns Result.Success(emptyList())

        // When
        val response = getAllBooksUseCase()

        // Then
        assert(response is Result.Success)
        val books = (response as Result.Success).data
        assertTrue(books.isEmpty())
    }


    @Test
    fun `when the API call throws an exception, return a Result Error with the expected exception message`() = runBlocking {
        val expectedExceptionMessage = "An error occurred"
        coEvery { booksRepository.getAllBooks() } returns Result.Error(expectedExceptionMessage)

        // When
        val response = getAllBooksUseCase()

        // Then
        assert(response is Result.Error)
        val errorMessage = (response as Result.Error).message
        assertEquals(expectedExceptionMessage, errorMessage)
    }

    @Test
    fun `when the API call is in a loading state, return Result Loading`() = runBlocking {
        // Given: Simulate a loading state returned from the repository
        coEvery { booksRepository.getAllBooks() } returns Result.Loading

        // When
        val response = getAllBooksUseCase()

        // Then
        assert(response is Result.Loading)
    }
}