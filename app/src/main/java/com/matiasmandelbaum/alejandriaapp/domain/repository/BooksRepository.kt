package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
interface BooksRepository {
    suspend fun getBooksByTitle(title: String): Result<List<Book>>

    suspend fun getAllBooks(): Result<List<Book>>

}