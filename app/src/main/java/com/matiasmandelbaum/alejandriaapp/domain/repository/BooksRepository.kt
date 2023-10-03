package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.Book

interface BooksRepository {
    suspend fun getBooksByTitle(title: String): Result<List<Book>>

}