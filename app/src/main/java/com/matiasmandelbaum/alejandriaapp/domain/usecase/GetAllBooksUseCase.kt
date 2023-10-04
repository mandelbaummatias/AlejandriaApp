package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import javax.inject.Inject

class GetAllBooksUseCase @Inject constructor(private val booksRepository: BooksRepository) {
    suspend operator fun invoke(): Result<List<Book>> {
        return booksRepository.getAllBooks()
    }
}