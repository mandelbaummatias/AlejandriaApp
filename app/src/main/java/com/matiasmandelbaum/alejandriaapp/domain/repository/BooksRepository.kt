package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun getBooksByTitle(title: String): Result<List<Book>>
    fun getAllBooks(): Flow<Result<List<Book>>>
    suspend fun reserveBook(
        isbn: String,
        userEmail: String,
        quantity: Int
    ): Result<ReservationResult>


}