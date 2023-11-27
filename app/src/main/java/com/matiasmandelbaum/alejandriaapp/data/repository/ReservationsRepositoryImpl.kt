package com.matiasmandelbaum.alejandriaapp.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.END_DATE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.ISBN
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.PENDING_STATUS
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.RESERVATIONS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.START_DATE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.STATUS
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.USER_EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.DAY_MONTH_YEAR
import com.matiasmandelbaum.alejandriaapp.domain.model.reservation.Reservation
import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReservationsRepositoryImpl @Inject constructor(firestore: FirebaseFirestore) :
    ReservationsRepository {

    private val db = firestore
    private val reservationsCollection = db.collection(RESERVATIONS_COLLECTION)

    override suspend fun createReservation(isbn: String, userEmail: String): Result<Unit> =
        suspendCoroutine { continuation ->
            val reservationsCollection = db.collection(RESERVATIONS_COLLECTION)

            val currentDate = LocalDate.now()

            val fechaInicio = currentDate
            val fechaFin = fechaInicio.plusMonths(1)

            val formattedFechaFin = fechaFin.format(DateTimeFormatter.ofPattern(DAY_MONTH_YEAR))

            val reservationData = hashMapOf(
                STATUS to PENDING_STATUS,
                START_DATE to FieldValue.serverTimestamp(),
                END_DATE to formattedFechaFin,
                ISBN to isbn,
                USER_EMAIL to userEmail
            )

            reservationsCollection.add(reservationData)
                .addOnSuccessListener {
                    continuation.resume(Result.Success(Unit))
                }.addOnFailureListener { e ->
                    continuation.resume(Result.Error("Reservation failed: ${e.message}"))
                }
        }

    override suspend fun changeUserEmailInReserve(newEmail: String, oldEmail: String) {
        reservationsCollection.whereEqualTo(USER_EMAIL, oldEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val reserveDocument = document.reference
                        reserveDocument.update(USER_EMAIL, newEmail)
                    }
                }
            }
    }


    override suspend fun createReservation(reservation: Reservation): Boolean {
        return try {
            reservationsCollection.add(reservation).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
