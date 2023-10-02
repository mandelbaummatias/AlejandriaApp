package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.GoogleBooksResponse
import com.matiasmandelbaum.alejandriaapp.data.util.FirebaseConstants.BOOKS_COLLECTION
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBooksRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) {
    suspend fun getAllNotes(): List<GoogleBooksResponse>
    {
        return try {
            val booksList = firestore.collection(BOOKS_COLLECTION)
                .get()
                .await()
                .toObjects(GoogleBooksResponse::class.java)
            booksList

            // Resource.Success(booksList)
        } catch (e: Exception) {
            println(e)
            emptyList<GoogleBooksResponse>()
        }
    }

}