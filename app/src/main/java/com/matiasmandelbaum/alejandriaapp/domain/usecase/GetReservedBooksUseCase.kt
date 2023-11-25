package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import com.matiasmandelbaum.alejandriaapp.ui.booksreserved.Reserves
import javax.inject.Inject

class GetReservedBooksUseCase @Inject constructor(private val booksRepository: BooksRepository) {
    suspend operator fun invoke(userEmail: String): Result<List<Reserves>> =
        booksRepository.getUserReservedBooks(userEmail)
}