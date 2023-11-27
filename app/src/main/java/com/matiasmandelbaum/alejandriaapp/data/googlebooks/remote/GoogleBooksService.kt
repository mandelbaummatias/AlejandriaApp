package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote

import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.GoogleBooksResponse
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GoogleBooksService @Inject constructor(private val googleBooksApiClient: GoogleBooksApiClient) {

    suspend fun searchBooksInGoogleBooks(booksFirestore: List<BookFirestore>): List<GoogleBooksResponse> {
        val booksGoogleResponse = mutableListOf<GoogleBooksResponse>()

        for (book in booksFirestore) {
            var retryCount = 0
            var success = false

            while (retryCount < MAX_RETRY_COUNT && !success) {
                try {
                    val bookGoogleResponse = withContext(Dispatchers.IO) {
                        googleBooksApiClient.searchBooksByISBN(book.isbn)
                    }
                    booksGoogleResponse.add(bookGoogleResponse)
                    success = true
                } catch (e: JsonDataException) {
                    retryCount++
                } catch (e: Exception) {
                    break
                }
            }
        }
        return booksGoogleResponse
    }

    companion object {
        private const val MAX_RETRY_COUNT = 5
    }
}