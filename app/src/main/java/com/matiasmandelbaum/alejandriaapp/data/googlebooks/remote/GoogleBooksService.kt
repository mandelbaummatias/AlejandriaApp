package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote

import android.util.Log
import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.GoogleBooksResponse
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "GoogleBooksService"

class GoogleBooksService @Inject constructor(private val googleBooksApiClient: GoogleBooksApiClient) {
    suspend fun searchBooksInGoogleBooks(booksFirestore: List<BookFirestore>): List<GoogleBooksResponse> {
        val booksGoogleResponse = mutableListOf<GoogleBooksResponse>()

        for (book in booksFirestore) {
            var retryCount = 0
            var success = false

            while (retryCount < MAX_RETRY_COUNT && !success) {
                try {
                    val bookGoogleResponse = withContext(Dispatchers.IO) {
                        googleBooksApiClient.searchBooksByISBN(book.isbn, GoogleBooksConfig.apiKey)
                    }
                    booksGoogleResponse.add(bookGoogleResponse)
                    success = true
                } catch (e: JsonDataException) {
                    // Log information about the book causing the issue
                    Log.d(TAG, "JsonDataException: Required value 'items' missing for book with ISBN ${book.isbn}. Details: $book")

                    // Increment the retry count and retry the search
                    retryCount++
                } catch (e: Exception) {
                    // Handle other exceptions as needed
                    Log.d(TAG, "Exception in Google Books API: $e")
                    break // Exit the loop if a non-retryable exception occurs
                }
            }
        }

        return booksGoogleResponse
    }

    companion object {
        private const val MAX_RETRY_COUNT = 5 // Set the maximum number of retry attempts
    }
}