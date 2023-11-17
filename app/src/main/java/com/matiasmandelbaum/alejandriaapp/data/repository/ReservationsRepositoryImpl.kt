package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.DAY_MONTH_YEAR
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.END_DATE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.ISBN
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.PENDING_STATUS
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.RESERVATIONS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.START_DATE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.STATUS
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.USER_EMAIL
import com.matiasmandelbaum.alejandriaapp.domain.model.reservation.Reservation
import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository

import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val TAG = "ReservationsRepositoryImpl"

class ReservationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    ReservationsRepository {

    private val db = firestore
    private val reservationsCollection = db.collection(RESERVATIONS_COLLECTION)

    override suspend fun createReservation(isbn: String, userEmail: String): Result<Unit> =
        suspendCoroutine { continuation ->
            // Create a reservation document
            val reservationsCollection = db.collection(RESERVATIONS_COLLECTION)

            // Get the current date
            val currentDate = LocalDate.now()

            val fechaInicio = currentDate
            val fechaFin = fechaInicio.plusMonths(1)

            val formattedFechaFin = fechaFin.format(DateTimeFormatter.ofPattern(DAY_MONTH_YEAR))

            val reservationData = hashMapOf(
                STATUS to PENDING_STATUS,
                // START_DATE to formattedFechaInicio,
                START_DATE to FieldValue.serverTimestamp(),
                END_DATE to formattedFechaFin,
                ISBN to isbn,
                USER_EMAIL to userEmail
            )

            reservationsCollection.add(reservationData)
                .addOnSuccessListener {
                    Log.d(TAG, "Reservation added successfully")
                    continuation.resume(Result.Success(Unit))
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Reservation failed! $e")
                    continuation.resume(Result.Error("Reservation failed: ${e.message}"))
                }
        }

    override suspend fun getReservationByUserEmail(email: String): Reservation {
        TODO("Not yet implemented")
    }

    override suspend fun getAllReservations(): Result<List<Reservation>> {
        TODO("Not yet implemented")
    }

    override suspend fun createReservation(reservation: Reservation): Boolean {
        return try {
            reservationsCollection.add(reservation).await()
            true // reserva creada
        } catch (e: Exception) {
            Log.e(TAG, "error al crear la reserva: ${e.message}")
            false // error al crear la reserva
        }
    }


}
