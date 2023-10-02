package com.matiasmandelbaum.alejandriaapp.ui.searchresult

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.model.BookNetwork
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components.ImageLinks
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.client.GoogleBooksApiClient
import com.matiasmandelbaum.alejandriaapp.data.util.FirebaseConstants
import com.matiasmandelbaum.alejandriaapp.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "SearchResultViewModel"

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val client: GoogleBooksApiClient,
   // private val firestore: FirebaseFirestore, //Necesito un modulo para proveer
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val titulo: String = savedStateHandle["titulo"]!!

    fun fetchBooksResult() {
        viewModelScope.launch {
            val booksRemote = fetchBooksFromDatabase()
            if (booksRemote.isNotEmpty()) {
                searchBooksInGbooks(booksRemote)
            }
            Log.d(TAG, "books firestore $booksRemote")
        }
    }

    private suspend fun fetchBooksFromDatabase(): List<BookNetwork> {
        return try {
            Log.d(TAG, "en fetchBooks2, mi titulo $titulo")
            val querySnapshot =
                FirebaseFirestore.getInstance().collection(FirebaseConstants.BOOKS_COLLECTION)
                    .orderBy("titulo")
                    .startAt(titulo)
                    .endAt("$titulo~")
                    .get()
                    .await()

            val booksList = querySnapshot.toObjects(BookNetwork::class.java)
            Log.d(TAG, "mis libros buscados $booksList")
            booksList // Return the list of books
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching books", e)
            emptyList() // Return an empty list or handle the error as needed
        } catch (e: IndexOutOfBoundsException) {
            Log.d(TAG, "out of bounds $e")
            emptyList()
        }
    }

    private fun searchBooksInGbooks(booksNetwork: List<BookNetwork>) {
        viewModelScope.launch {
            try {
                val booksDomain = mutableListOf<Book>()
                for (book in booksNetwork) {
                    val bookGoogleResponse =
                        client.searchBooksByISBN(book.isbn_13, GoogleBooksConfig.apiKey)
                    Log.d(
                        TAG,
                        "RECUPERANDO POR FIRESTORE ISBN A GOOGLE BOOKS !! ${bookGoogleResponse.items[0].volumeInfo.description}"
                    )
                    val bookToList = Book(
                        autor = book.autor,
                        titulo = book.titulo,
                        descripcion = bookGoogleResponse.items[0].volumeInfo.description,
                        valoracion = bookGoogleResponse.items[0].volumeInfo.averageRating,
                        imageLinks = ImageLinks(
                            smallThumbnail = bookGoogleResponse.items[0].volumeInfo.imageLinks?.smallThumbnail
                        )
                    )
                    booksDomain.add(bookToList)
                }
                //Debería cambiarse el método para retornar una List<Book> y mostrarle en la lista
                Log.d(TAG, "Estos irian al recycler $booksDomain")
            } catch (e: Exception) {
                Log.d(TAG, "exception en google books $e")
            }
        }
    }
}

