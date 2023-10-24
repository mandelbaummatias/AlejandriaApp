package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "BooksDetailViewModel"

@HiltViewModel
class BooksDetailViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _book = MutableLiveData<Book>()
    // val book : LiveData<Book> = _book

    val book: Book = savedStateHandle["book"]!!

    fun reserveBook() {
        if (book.cantidadDisponible > 0) {
            // Decrease the available quantity by one
           // book.cantidad_disponible--

            // Update the Firestore document
            val db = FirebaseFirestore.getInstance()
            val booksCollection = db.collection("libros")

            booksCollection
                .whereEqualTo("isbn_13", book.isbn)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() > 0) {
                        val documentSnapshot = documents.documents[0]
                        val bookReference = documentSnapshot.reference
                        bookReference.update("cantidad_disponible", book.cantidadDisponible - 1)

                        // Create a reservation document
                        val reservationsCollection = db.collection("reservas_libros")
                        val reservationData = hashMapOf(
                            "estado" to "A retirar",
                            "fecha_fin" to null, // Initialize as null
                            "fecha_inicio" to null, // Initialize as null
                            "fecha_reserva" to FieldValue.serverTimestamp(),
                            "isbn_13" to book.isbn,
                            "mail_usuario" to null
                        )

                        reservationsCollection.add(reservationData)
                            .addOnSuccessListener {
                                Log.d(TAG,"  // Reservation added successfully")
                                // Reservation added successfully
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "error in reservation $e")
                                // Handle errors if the reservation document creation fails.
                            }
                    } else {
                        Log.d(TAG, "isbn not found")
                        // Handle the case when the book with the given ISBN is not found.
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "query or update failed $e")
                    // Handle errors if the query or update fails.
                }

            // Notify observers of the change in the book's availability
            _book.value = book
        } else {
            Log.d(TAG, "no books to reserve (devolver mensaje")
            // Handle the case when there are no available books to reserve
        }
    }

    fun onCreate() {
        Log.d(TAG, "init!")
        Log.d(TAG, "mi libro $book")
    }

}


