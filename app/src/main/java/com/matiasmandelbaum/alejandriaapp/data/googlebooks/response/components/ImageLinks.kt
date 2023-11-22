package com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.components

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ImageLinks(
    @Json(name = "smallThumbnail")
    val smallThumbnail: String?
): Parcelable