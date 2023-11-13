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

class GetBooksByTitleUseCaseTest {
    @RelaxedMockK
    private lateinit var booksRepository: BooksRepository

    lateinit var getBooksByTitleUseCase: GetBooksByTitleUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getBooksByTitleUseCase = GetBooksByTitleUseCase(booksRepository)
    }

    @Test
    fun `when the API returns books by title, return a list of books`() = runBlocking {
        // Given
        val title = "Sample Title"
        val bookList = listOf(
            Book(
                "Author 1",
                title,
                "ISBN-1",
                "Description 1",
                4.5,
                null,
                10
            ),
            Book(
                "Author 2",
                title,
                "ISBN-2",
                "Description 2",
                4.0,
                null,
                15
            )
        )
        coEvery { booksRepository.getBooksByTitle(title) } returns Result.Success(bookList)

        // When
        val response = getBooksByTitleUseCase(title)

        // Then
        assert(response is Result.Success)
        val books = (response as Result.Success).data
        assertEquals(bookList, books)
    }

    @Test
    fun `when the API returns an empty list, return an empty list of books`() = runBlocking {
        // Given
        val title = "Non-Existent Title"
        coEvery { booksRepository.getBooksByTitle(title) } returns Result.Success(emptyList())

        // When
        val response = getBooksByTitleUseCase(title)

        // Then
        assert(response is Result.Success)
        val books = (response as Result.Success).data
        assertTrue(books.isEmpty())
    }

    @Test
    fun `when the API call throws an exception, return a Result Error with the exception message`() =
        runBlocking {
            // Given
            val title = "Sample Title"
            val expectedExceptionMessage = "An error occurred"
            coEvery { booksRepository.getBooksByTitle(title) } returns Result.Error(
                expectedExceptionMessage
            )

            // When
            val response = getBooksByTitleUseCase(title)

            // Then
            assert(response is Result.Error)
            val errorMessage = (response as Result.Error).message
            assertEquals(expectedExceptionMessage, errorMessage)
        }
}