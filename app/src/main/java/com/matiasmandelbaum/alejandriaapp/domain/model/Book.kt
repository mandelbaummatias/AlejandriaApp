package com.matiasmandelbaum.alejandriaapp.domain.model

import android.os.Parcelable
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components.ImageLinks
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Book(
    val autor: String,
    val titulo: String,
    val isbn: String,
    val descripcion: String?,
    val valoracion: Double?,
    val imageLinks: ImageLinks?
): Parcelable