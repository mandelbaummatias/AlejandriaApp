package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.model.reserve.Reserves
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun getBooksByTitle(title: String): Result<List<Book>>
    suspend fun getUserReservedBooks(userEmail: String): Result<List<Reserves>>
    fun getAllBooks(): Flow<Result<List<Book>>>
    suspend fun reserveBook(
        isbn: String,
        userEmail: String
    ): Result<ReservationResult>


}