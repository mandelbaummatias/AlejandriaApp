package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import javax.inject.Inject

class GetAllBooksUseCase @Inject constructor(private val booksRepository: BooksRepository) {
    operator fun invoke() = booksRepository.getAllBooks()
}