package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
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

    private val client = ClientSearch(
        applicationID = ApplicationID(""),
        apiKey = APIKey("")
    )
    private val indexName = IndexName("libros")
    private lateinit var index : Index

    override suspend fun getBooksByTitle(title: String): Result<List<Book>> {
        return try {
            val books = mutableListOf<Book>()

            val booksFirestore = withContext(Dispatchers.IO) {
                getBooksFromFirestoreByTitle(title)
                //getBooksFromFirestoreByTitle2(title)
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

    override suspend fun reserveBook(isbn: String, userEmail: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateBook(book: Book): Result<Boolean> {
        TODO("Not yet implemented")
    }

//    private suspend fun getBooksFromFirestoreByTitle(title: String): List<BookFirestore> {
//        val querySnapshot =
//            firestore.collection(FirebaseConstants.BOOKS_COLLECTION)
//                .orderBy("titulo") //Ver para no hardcodearlo
//                .startAt(title)
//                .endAt("$title~")
//                .get()
//                .await()
//        return querySnapshot.toObjects(BookFirestore::class.java)
//    }

    private suspend fun getBooksFromFirestoreByTitle(title: String): List<BookFirestore> {
        index = client.initIndex(indexName)
        var libros: List<BookFirestore> = emptyList()

        try {
            val response = index.search(Query(title))
            libros = response.hits.deserialize(BookFirestore.serializer())
            Log.d(TAG, "mi response $response")
            Log.d(TAG, "mis libros $libros")
        } catch (e: Exception) {
            Log.e(TAG, "Error during Algolia search", e)
        }

        return libros
    }
    

    private suspend fun getBooksFromFirestoreByIsbn(isbn: String): List<BookFirestore> {
        val querySnapshot =
            firestore.collection(FirebaseConstants.BOOKS_COLLECTION)
                .orderBy("isbn_13") //Ver para no hardcodearlo
                .startAt(isbn)
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