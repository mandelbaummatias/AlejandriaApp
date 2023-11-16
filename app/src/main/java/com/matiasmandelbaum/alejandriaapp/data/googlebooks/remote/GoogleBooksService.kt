package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote

import android.util.Log
import com.matiasmandelbaum.alejandriaapp.core.googlebooks.GoogleBooksConfig
import com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response.BookFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.GoogleBooksResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "GoogleBooksService"

class GoogleBooksService @Inject constructor(private val googleBooksApiClient: GoogleBooksApiClient) {
    suspend fun searchBooksInGoogleBooks(booksFirestore: List<BookFirestore>): List<GoogleBooksResponse> {
        return try {
            val booksGoogleResponse = mutableListOf<GoogleBooksResponse>()
            for (book in booksFirestore) {
                val bookGoogleResponse = withContext(Dispatchers.IO) {
                    googleBooksApiClient.searchBooksByISBN(book.isbn, GoogleBooksConfig.apiKey)
                }
                booksGoogleResponse.add(bookGoogleResponse)
            }
            return booksGoogleResponse
        } catch (e: Exception) {
            Log.d(TAG, "exception en google books $e")
            emptyList()
        }
    }
}