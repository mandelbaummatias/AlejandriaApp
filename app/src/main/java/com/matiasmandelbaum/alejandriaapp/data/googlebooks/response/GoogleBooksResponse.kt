package com.matiasmandelbaum.alejandriaapp.data.googlebooks.response

import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.components.BookItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleBooksResponse(
    @Json(name = "items")
    val items: List<BookItem>
)
