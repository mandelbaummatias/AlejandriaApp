package com.matiasmandelbaum.alejandriaapp.domain.usecase

import com.google.firebase.Timestamp
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.model.reservation.Reservation
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.ZoneOffset

class BookReservationUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
    private val reservationRepository: ReservationsRepository,
    private val userRepository: UsersRepository
) {
     suspend fun reserveBook(title: String, email: String): Boolean {
        val bookResult: Result<List<Book>> = booksRepository.getBooksByTitle(title)
        if (bookResult is Result.Success) {
            val booksList = bookResult.data
            if (booksList.isNotEmpty()) {
                val firstBook = booksList[0]

                if (firstBook.cantidadDisponible > 0) {
                    // Decrementar la cantidad disponible del libro
                    val newQuantity = firstBook.cantidadDisponible - 1
                    val updatedBook = firstBook.copy(cantidadDisponible = newQuantity)
                    booksRepository.updateBook(updatedBook)

                    // Obtener el usuario por email
                    val user = userRepository.getUserByEmail(email)
                    if (user != null) {

                        val tiempoActual = System.currentTimeMillis()

                        val fechaActual = LocalDateTime.ofEpochSecond(tiempoActual / 1000, 0, ZoneOffset.UTC)
                        val fechaDespuesUnMes = fechaActual.plusMonths(1)

                        val timestampFirebaseActual = Timestamp(tiempoActual, 0)

                        val reservation = Reservation(
                            isbn_13 = firstBook.isbn,
                            mail_usuario = user.email,
                            fecha_fin = fechaDespuesUnMes.toString(),
                            fecha_inicio = fechaActual.toString(),
                            fecha_reserva = timestampFirebaseActual,
                            estado = "A retirar",
                        )

                        // guardamos la reserva
                        reservationRepository.createReservation(reservation)
                        return true // reserva exitosa
                    } else {
                        // no se encontró el usuario
                        return false
                    }
                } else {
                    // no hay stock
                    return false
                }
            }
        }
        return false // otro tipo de error
    }
}
