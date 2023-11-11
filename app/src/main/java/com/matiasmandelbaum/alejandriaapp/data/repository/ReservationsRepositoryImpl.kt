package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
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
    private val reservationsCollection = db.collection("reservas_libros")
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

    override suspend fun createReservation(isbn: String, userEmail: String): Result<Unit> = suspendCoroutine { continuation ->
        // Create a reservation document
        val reservationsCollection = db.collection("reservas_libros")

        // Get the current date
        val currentDate = LocalDate.now()

        // Format the current date as a string in the desired format (28/10/2023)
        val formattedCurrentDate = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // Calculate the fecha_fin by adding a month to fecha_inicio
        val fechaInicio = currentDate
        val fechaFin = fechaInicio.plusMonths(1)

        // Format the fecha_fin as a string in the desired format
        val formattedFechaInicio = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val formattedFechaFin = fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // Create the reservationData hashmap with the formatted dates
        val reservationData = hashMapOf(
            "estado" to "A retirar",
            "fecha_inicio" to formattedFechaInicio,
            "fecha_fin" to formattedFechaFin,
            "fecha_reserva" to FieldValue.serverTimestamp(),
            "isbn_13" to isbn,
            "mail_usuario" to userEmail
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
}
