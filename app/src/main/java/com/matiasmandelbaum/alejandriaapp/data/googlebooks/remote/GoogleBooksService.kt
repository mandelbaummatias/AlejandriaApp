package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote

import android.util.Log
import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.GoogleBooksResponse
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
private const val TAG = "GoogleBooksServiceY"

class GoogleBooksService @Inject constructor(private val googleBooksApiClient: GoogleBooksApiClient) {

    suspend fun searchBooksInGoogleBooks(booksFirestore: List<BookFirestore>): List<GoogleBooksResponse> {
        val booksGoogleResponse = mutableListOf<GoogleBooksResponse>()

        for (book in booksFirestore) {
            var retryCount = 0
            var success = false

            while (retryCount < MAX_RETRY_COUNT && !success) {
                try {
                    Log.d(TAG, "Searching books for ISBN: ${book.isbn}")

                    val bookGoogleResponse = withContext(Dispatchers.IO) {
                        googleBooksApiClient.searchBooksByISBN(book.isbn)
                    }
                    booksGoogleResponse.add(bookGoogleResponse)
                    success = true

                    Log.d(TAG, "Successfully retrieved books for ISBN: ${book.isbn}")
                } catch (e: JsonDataException) {
                    Log.w(TAG, "JsonDataException occurred while searching books for ISBN: ${book.isbn}", e)
                    retryCount++
                } catch (e: Exception) {
                    Log.e(TAG, "Exception occurred while searching books for ISBN: ${book.isbn}", e)
                    break
                }
            }
        }
        return booksGoogleResponse
    }

    companion object {
        private const val MAX_RETRY_COUNT = 5
        private const val TAG = "GoogleBooksService"
    }
}