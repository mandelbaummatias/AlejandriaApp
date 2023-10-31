package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import javax.inject.Inject

class GetBooksByTitleUseCase @Inject constructor(private val booksRepository: BooksRepository) {
    suspend operator fun invoke(title: String): Result<List<Book>> {
        return booksRepository.getBooksByTitle(title)
    }
}