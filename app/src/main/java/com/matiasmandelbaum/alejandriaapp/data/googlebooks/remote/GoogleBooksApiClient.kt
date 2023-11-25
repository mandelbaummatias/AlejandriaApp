package com.matiasmandelbaum.alejandriaapp.data.googlebooks.remote

import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiClient {
    @GET(" ")
    suspend fun searchBooksByISBN(
        @Query("q") isbn: String
    ): GoogleBooksResponse
}