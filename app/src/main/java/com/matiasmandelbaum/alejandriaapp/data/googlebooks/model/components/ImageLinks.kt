package com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageLinks(
    @Json(name = "smallThumbnail")
    val smallThumbnail: String?
)