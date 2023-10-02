package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote.client

import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiClient {
    @GET(" ")
    suspend fun searchBooksByISBN(
        @Query("q") isbn: String,
        @Query("key") apiKey: String
    ): GoogleBooksResponse
}