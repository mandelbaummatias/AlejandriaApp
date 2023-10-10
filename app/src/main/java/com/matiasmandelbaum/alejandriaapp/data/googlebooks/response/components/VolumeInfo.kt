package com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VolumeInfo(
    @Json(name = "description")
    val description: String?,
    @Json(name = "averageRating")
    val averageRating: Double?,
    @Json(name = "imageLinks")
    val imageLinks: ImageLinks?,
    @Json(name = "publishedDate")
    val publishedDate: String?
)