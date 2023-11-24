package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.algolia.search.client.ClientSearch
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.GoogleBooksService
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.GoogleBooksResponse
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.components.ImageLinks
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.AVAILABLE_QUANTITY
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.BOOKS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.DATE
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


private const val TAG = "BooksRepositoryImpl"

class BooksRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val googleBooksService: GoogleBooksService,
    private val algoliaClient: ClientSearch
) : BooksRepository {

    override fun getAllBooks(): Flow<Result<List<Book>>> = callbackFlow {
        val itemsReference = firestore.collection(BOOKS_COLLECTION)
            .orderBy(
                AVAILABLE_QUANTITY,
                Query.Direction.ASCENDING
            ) // Order by "cantidad_disponible" first
            .orderBy(DATE, Query.Direction.DESCENDING) // Then order by "fecha_ingreso"
            .whereGreaterThan(AVAILABLE_QUANTITY, 0)


        val subscription = itemsReference.addSnapshotListener { snapshot, error ->
            if (error != null) {
                launch {
                    trySend(Result.Error(error.message.toString())).isSuccess
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                launch {
                    val booksFirestore = snapshot.toObjects(BookFirestore::class.java)

                    val resultBooks = try {
                        coroutineScope {
                            // Use async to perform Google Books search for each book concurrently
                            val deferredBooks = booksFirestore.map { bookFirestore ->
                                async(Dispatchers.IO) {
                                    val bookGoogle = googleBooksService.searchBooksInGoogleBooks(
                                        listOf(bookFirestore)
                                    )
                                    createBookFromRemoteData(
                                        bookFirestore,
                                        bookGoogle.singleOrNull()
                                    )
                                }
                            }

                            // Await all deferred results
                            deferredBooks.awaitAll()
                        }
                    } catch (e: Exception) {
                        // Handle exceptions that might occur during the asynchronous processing
                        Log.e(TAG, "Error processing books", e)
                        emptyList()
                    }

                    Log.d(TAG, "size ${resultBooks.size}")
                    trySend(Result.Success(resultBooks)).isSuccess
                }
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun reserveBook(
        isbn: String,
        userEmail: String,
        quantity: Int
    ): Result<ReservationResult> {
        return try {
            val result = suspendCoroutine { continuation ->
                val booksCollection = firestore.collection(BOOKS_COLLECTION)
                val book = booksCollection.document(isbn)

                // Retrieve the current quantity before updating
                book.get().addOnSuccessListener { documentSnapshot ->
                    val currentQuantityLong = documentSnapshot.getLong(AVAILABLE_QUANTITY) ?: 0
                    val currentQuantity = currentQuantityLong.toInt()

                    if (currentQuantity <= 0) {
                        Log.d(TAG, "CURRENT Q LESS OR EQUAL THAN 0 $currentQuantity")
                        // If current quantity is less than 0, return an error immediately
                        continuation.resume(Result.Error("Alguien fue más rápido! No hay disponibilidad..."))
                    } else {
                        Log.d(TAG, "CURRENT Q greater THAN 0 $currentQuantity")
                        // Update the available quantity
                        book.update(AVAILABLE_QUANTITY, currentQuantity - 1)
                            .addOnSuccessListener {
                                Log.d(TAG, "update ok")
                                // Call continuation.resume with the Result.Success
                                continuation.resume(
                                    Result.Success(
                                        ReservationResult(
                                            userEmail,
                                            isbn
                                        )
                                    )
                                )
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "error en update $it")
                                // Call continuation.resume with the Result.Error
                                continuation.resume(Result.Error("Error reserving book: ${it.message}"))
                            }
                    }
                }
            }

            Log.d(TAG, "RESULT FINAL $result")

            result
        } catch (e: Exception) {
            Result.Error("Error reserving book: ${e.message}")
        }
    }

    private suspend fun getBooksFromFirestoreByTitle(title: String): List<BookFirestore> {
        val index = algoliaClient.initIndex(IndexName("libros"))
        var libros: List<BookFirestore> = emptyList()

        try {
            val response = index.search(Query(title))
            libros = response.hits.deserialize(BookFirestore.serializer())
            Log.d(TAG, "mi response $response")
            Log.d(TAG, "mis libros $libros")
        } catch (e: Exception) {
            Log.e(TAG, "Error during Algolia search $e")
        }

        return libros
    }

    override suspend fun getBooksByTitle(title: String): Result<List<Book>> {
        return try {
            val books = mutableListOf<Book>()

            val booksFirestore = withContext(Dispatchers.IO) {
                getBooksFromFirestoreByTitle(title)
                //getBooksFromFirestoreByTitle2(title)
            }

            Log.d(TAG, "booksFirestore $booksFirestore")
            val booksGoogle = googleBooksService.searchBooksInGoogleBooks(booksFirestore)

            if (booksFirestore.size != booksGoogle.size) {
                throw IllegalArgumentException("Input lists must have the same size") //Revisar
                //En general siempre van a ser iguales la lista de firestore y gbooks porque el isbn es uno
                //Pero contemplar un caso donde esta excepción sea un problema, o al menos sea controlada
            }

            for (i in booksFirestore.indices) {
                val bookFirestore = booksFirestore[i]
                val bookGoogle = booksGoogle[i]
                val book = createBookFromRemoteData(bookFirestore, bookGoogle)
                Log.d(TAG, "Viendo mi book final $book")
                books.add(book)
            }
            Result.Success(books) // Return the list of books
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    private fun createBookFromRemoteData(
        bookFirestore: BookFirestore,
        bookGoogle: GoogleBooksResponse?
    ): Book {
        return Book(
            autor = bookFirestore.autor,
            titulo = bookFirestore.titulo,
            isbn = bookFirestore.isbn,
            descripcion = bookGoogle?.items?.getOrNull(0)?.volumeInfo?.description.orEmpty(),
            valoracion = bookGoogle?.items?.getOrNull(0)?.volumeInfo?.averageRating ?: 0.0,
            imageLinks = ImageLinks(bookGoogle?.items?.getOrNull(0)?.volumeInfo?.imageLinks?.smallThumbnail),
            cantidadDisponible = bookFirestore.cantidad_disponible
        )
    }
}
