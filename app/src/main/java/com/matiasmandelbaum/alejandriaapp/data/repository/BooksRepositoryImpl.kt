package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.model.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.GoogleBooksResponse
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components.ImageLinks
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.GoogleBooksService
import com.matiasmandelbaum.alejandriaapp.data.util.FirebaseConstants
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


private const val TAG = "BooksRepositoryImpl"
class BooksRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val googleBooksService: GoogleBooksService
) : BooksRepository {
    override suspend fun getBooksByTitle(title: String): Result<List<Book>> {
        return try {
            val books = mutableListOf<Book>()

            val booksFirestore = withContext(Dispatchers.IO) {
                getBooksFromFirestoreByTitle(title)
            }
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

    override suspend fun getAllBooks(): Result<List<Book>> {
        return try {
            val books = mutableListOf<Book>()

            val booksFirestore = withContext(Dispatchers.IO) {
                getAllBooksFromFirestore()
            }

            Log.d(TAG, "libros firestore getAll : $booksFirestore")

            val booksGoogle = googleBooksService.searchBooksInGoogleBooks(booksFirestore)

            Log.d(TAG, "libros google getAll : $booksFirestore")

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

    private suspend fun getBooksFromFirestoreByTitle(title: String): List<BookFirestore> {
        val querySnapshot =
            firestore.collection(FirebaseConstants.BOOKS_COLLECTION)
                .orderBy("titulo") //Ver para no hardcodearlo
                .startAt(title)
                .endAt("$title~")
                .get()
                .await()
        return querySnapshot.toObjects(BookFirestore::class.java)
    }

    private suspend fun getAllBooksFromFirestore(): List<BookFirestore> {
        val querySnapshot =
            firestore.collection(FirebaseConstants.BOOKS_COLLECTION)
                .orderBy("titulo") //Ver para no hardcodearlo
                .get()
                .await()
        return querySnapshot.toObjects(BookFirestore::class.java)
    }

    private fun createBookFromRemoteData(
        bookFirestore: BookFirestore,
        bookGoogle: GoogleBooksResponse
    ): Book {
        return Book(
            autor = bookFirestore.autor,
            titulo = bookFirestore.titulo,
            isbn = bookFirestore.isbn_13,
            descripcion = bookGoogle.items[0].volumeInfo.description, //Siempre es uno, al ser un ISBN identificador único
            valoracion = bookGoogle.items[0].volumeInfo.averageRating,
            imageLinks = ImageLinks(bookGoogle.items[0].volumeInfo.imageLinks?.smallThumbnail),
            cantidadDisponible = bookFirestore.cantidad_disponible
        )
    }


}