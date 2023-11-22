package com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.components

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookItem(
    @Json(name = "volumeInfo")
    val volumeInfo: VolumeInfo
)
