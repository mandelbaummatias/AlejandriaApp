package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository
import javax.inject.Inject

class ChangeUserEmailInReservesUseCase @Inject constructor(private val reservationsRepository: ReservationsRepository) {

    suspend operator fun invoke(isbn: String, userEmail: String) =
        reservationsRepository.changeUserEmailInReserve(isbn, userEmail)
}