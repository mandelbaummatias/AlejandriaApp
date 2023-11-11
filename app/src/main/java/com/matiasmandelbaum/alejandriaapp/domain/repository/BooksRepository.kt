package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
interface BooksRepository {
    suspend fun getBooksByTitle(title: String): Result<List<Book>>

    suspend fun getAllBooks(): Result<List<Book>>

    suspend fun reserveBook(isbn: String, userEmail: String): Boolean

    suspend fun updateBook(book: Book): Result<Boolean>

    suspend fun reserveBook2(isbn: String, userEmail: String,cant: Int): Result<ReservationResult>

    //suspend fun reserveBook2(isbn: String, userEmail: String,cant: Int): Result<Boolean>

}