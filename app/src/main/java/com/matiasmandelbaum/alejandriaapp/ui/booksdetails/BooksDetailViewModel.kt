package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.usecase.FetchSubscriptionUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByIdUseCase
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "BooksDetailViewModel"

@HiltViewModel
class BooksDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchSubscriptionUseCase: FetchSubscriptionUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {
    private val _book = MutableLiveData<Book>()

    private val _subscriptionExists: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionExists: LiveData<Result<Subscription>> = _subscriptionExists

    private val _user: MutableLiveData<Result<User>?> = MutableLiveData()
    val user: MutableLiveData<Result<User>?> = _user

    private val _isEnabledToReserve = MutableLiveData<Boolean>()
    val isEnabledToReserve : LiveData<Boolean> = _isEnabledToReserve


    val book: Book = savedStateHandle["book"]!!

    private val _reservationState = MutableLiveData<ReservationState>()
    val reservationState: LiveData<ReservationState> = _reservationState

    private val _hasReservedBook = MutableLiveData<Boolean>()
    val hasReservedBook: LiveData<Boolean> = _hasReservedBook

    fun reserveBook(userEmail: String) {
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
                        // Get the current date
                        val currentDate = LocalDate.now()

                        // Format the current date as a string in the desired format (28/10/2023)
                        val formattedCurrentDate =
                            currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        // Calculate the fecha_fin by adding a month to fecha_inicio
                        val fechaInicio = currentDate
                        val fechaFin = fechaInicio.plusMonths(1)

                        // Format the fecha_fin as a string in the desired format
                        val formattedFechaInicio =
                            fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        val formattedFechaFin =
                            fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        // Create the reservationData hashmap with the formatted dates
                        val reservationData = hashMapOf(
                            "estado" to "A retirar",
                            "fecha_inicio" to formattedFechaInicio,
                            "fecha_fin" to formattedFechaFin,
                            "fecha_reserva" to FieldValue.serverTimestamp(),
                            "isbn_13" to book.isbn,
                            "mail_usuario" to userEmail
                        )

                        reservationsCollection.add(reservationData)
                            .addOnSuccessListener {
                                Log.d(TAG, "Reservation added successfully")

                                // Reservation added successfully, now update the "users" collection
                                val usersCollection = db.collection("users")
                                usersCollection
                                    .whereEqualTo("email", userEmail)
                                    .get()
                                    .addOnSuccessListener { userDocuments ->
                                        if (userDocuments.size() > 0) {
                                            val userDocument = userDocuments.documents[0]
                                            val userReference = userDocument.reference
                                            userReference.update("reservo_libro", true)
                                            updateReservationState(false)
                                        } else {
                                            Log.d(TAG, "User not found")
                                            // Handle the case when the user with the given email is not found.
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d(TAG, "User query or update failed $e")
                                        // Handle errors if the user query or update fails.
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "Error in reservation $e")
                                // Handle errors if the reservation document creation fails.
                            }
                    } else {
                        Log.d(TAG, "ISBN not found")
                        // Handle the case when the book with the given ISBN is not found.
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Query or update failed $e")
                    // Handle errors if the query or update fails.
                }

            // Notify observers of the change in the book's availability
            _book.value = book
        } else {
            Log.d(TAG, "No books to reserve (devolver mensaje)")
            // Handle the case when there are no available books to reserve
        }
    }

    fun onCreate() {
        Log.d(TAG, "init!")
        Log.d(TAG, "mi libro $book")
    }

    fun fetchSubscription(id: String) {
        Log.d(TAG, "ejecutando fetchSubscription")
        _subscriptionExists.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "subscription fetch")
            val result = fetchSubscriptionUseCase(id)
            Log.d(TAG, "a ver $result")
            _subscriptionExists.postValue(result)

        }
    }

    fun getUserById(userId: String) {
        Log.d(TAG, "getUserById")
        _user.value = Result.Loading
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _user.value = result
        }
    }

    fun updateReservationState(isEnabled: Boolean){
        Log.d(TAG, "updateReservationState $isEnabled")
        _isEnabledToReserve.value = isEnabled
    }

    fun resetUser() {
        Log.d(TAG, "resetUser()")
        _user.value = null
    }


}

data class ReservationState(
    val isSubscriptionAuthorized: Boolean,
    val hasReservedBook: Boolean
)


