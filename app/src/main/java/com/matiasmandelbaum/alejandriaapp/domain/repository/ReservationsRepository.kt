package com.matiasmandelbaum.alejandriaapp.domain.repository

import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.reservation.Reservation

interface ReservationsRepository {

    suspend fun getReservationByUserEmail(email: String): Reservation

    suspend fun getAllReservations(): Result<List<Reservation>>

    suspend fun createReservation(reservation: Reservation): Boolean

    suspend fun createReservation(isbn:String, userEmail:String): Result<Unit>

}