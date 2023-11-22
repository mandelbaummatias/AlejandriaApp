package com.matiasmandelbaum.alejandriaapp.domain.model.book

import android.os.Parcelable
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.response.components.ImageLinks
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Book(
    val autor: String,
    val titulo: String,
    val isbn: String,
    val descripcion: String?,
    val valoracion: Double?,
    val imageLinks: ImageLinks?,
    val cantidadDisponible: Int
): Parcelable