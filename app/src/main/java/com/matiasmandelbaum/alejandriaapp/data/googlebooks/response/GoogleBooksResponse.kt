package com.matiasmandelbaum.alejandriaapp.data.googlebooks.model

import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components.BookItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleBooksResponse(
    @Json(name = "items")
    val items: List<BookItem>
)
