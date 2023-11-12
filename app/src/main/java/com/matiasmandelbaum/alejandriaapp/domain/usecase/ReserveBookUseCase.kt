package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import javax.inject.Inject

class ReserveBookUseCase @Inject constructor(private val booksRepository: BooksRepository) {
    suspend operator fun invoke(isbn: String, userEmail: String, quantity: Int): Result<ReservationResult> {
        return try {
            booksRepository.reserveBook(isbn, userEmail, quantity)
            Result.Success(ReservationResult(userEmail, isbn))
        } catch (e: Exception) {
            Result.Error("Error reserving book: ${e.message}")
        }
    }
}