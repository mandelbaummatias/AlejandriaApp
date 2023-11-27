package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.algolia.search.client.ClientSearch
import com.algolia.search.helper.deserialize
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.GoogleBooksService
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.GoogleBooksResponse
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.components.ImageLinks
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.AUTHOR
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.AVAILABLE_QUANTITY
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.BOOKS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.DATE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.TITLE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.ISBN
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.RESERVATIONS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.START_DATE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.STATUS
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.USER_EMAIL
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.model.reserve.Reserves
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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
            )
            .orderBy(DATE, Query.Direction.DESCENDING)
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

                            deferredBooks.awaitAll()
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                    trySend(Result.Success(resultBooks)).isSuccess
                }
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getUserReservedBooks(userEmail: String): Result<List<Reserves>> {
        return try {
            val bookReserveCollection = firestore.collection(RESERVATIONS_COLLECTION)
                .whereEqualTo(USER_EMAIL, userEmail)
                .get()
                .await()

            val resultBooks = mutableListOf<Reserves>()

            for (document in bookReserveCollection) {
                val isbn = document.getString(ISBN)
                if (isbn != null) {
                    val book = getBookDetails(isbn, document)
                    resultBooks.add(book)
                }
            }

            Result.Success(resultBooks)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    private suspend fun getBookDetails(isbn: String, reserveDocument: DocumentSnapshot): Reserves {
        return try {
            val bookCollection = firestore.collection(BOOKS_COLLECTION)

            val bookQuerySnapshot = bookCollection.whereEqualTo(ISBN, isbn).get().await()

            for (bookDocument in bookQuerySnapshot) {
                val title = bookDocument.getString(TITLE) ?: ""
                val author = bookDocument.getString(AUTHOR) ?: ""
                val dateReserve = reserveDocument.getTimestamp(START_DATE) ?: Timestamp(Date())
                val status = reserveDocument.getString(STATUS) ?: ""

                return Reserves(isbn, title, author, dateReserve, status)
            }
            Reserves(isbn, "", "", Timestamp(Date()), "")
        } catch (e: Exception) {
            Reserves(isbn, "", "", Timestamp(Date()), "")
        }
    }

    override suspend fun reserveBook(
        isbn: String,
        userEmail: String,
    ): Result<ReservationResult> {
        return try {
            val book = fetchBook(isbn) ?: return Result.Error("Libro no encontrado")

            val currentQuantity = checkAvailability(book)

            if (currentQuantity <= 0) {
                return Result.Error("Alguien fue más rápido! No hay disponibilidad...")
            }

            updateQuantity(book, currentQuantity - 1)

            Result.Success(ReservationResult(userEmail, isbn))
        } catch (e: Exception) {
            Result.Error("Error reserving book")
        }
    }

    private suspend fun fetchBook(isbn: String): DocumentSnapshot? {
        return suspendCoroutine { continuation ->
            val booksCollection = firestore.collection(BOOKS_COLLECTION)
            val book = booksCollection.document(isbn)

            book.get().addOnSuccessListener { documentSnapshot ->
                continuation.resume(documentSnapshot)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    private fun checkAvailability(book: DocumentSnapshot?): Int {
        val currentQuantityLong = book?.getLong(AVAILABLE_QUANTITY) ?: 0
        return currentQuantityLong.toInt()
    }

    private suspend fun updateQuantity(book: DocumentSnapshot?, newQuantity: Int) {
        book?.let {
            suspendCoroutine { continuation ->
                it.reference.update(AVAILABLE_QUANTITY, newQuantity)
                    .addOnSuccessListener {
                        continuation.resume(Unit)
                    }.addOnFailureListener {
                        continuation.resumeWithException(Exception("Error al reservar"))
                    }
            }
        }
    }

    private suspend fun getBooksFromFirestoreByTitle(title: String): List<BookFirestore> {
        val index = algoliaClient.initIndex(IndexName(BOOKS_COLLECTION))
        var libros: List<BookFirestore> = emptyList()

        try {
            val response = index.search(Query(title))
            libros = response.hits.deserialize(BookFirestore.serializer())
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
            }

            val booksGoogle = googleBooksService.searchBooksInGoogleBooks(booksFirestore)

            if (booksFirestore.size != booksGoogle.size) {
                throw IllegalArgumentException("Input lists must have the same size")
            }

            for (i in booksFirestore.indices) {
                val bookFirestore = booksFirestore[i]
                val bookGoogle = booksGoogle[i]
                val book = createBookFromRemoteData(bookFirestore, bookGoogle)
                books.add(book)
            }
            Result.Success(books)
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
