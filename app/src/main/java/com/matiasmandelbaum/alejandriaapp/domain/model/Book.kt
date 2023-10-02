package com.matiasmandelbaum.alejandriaapp.domain.model

import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components.ImageLinks

data class Book(
    val autor: String,
    val titulo: String,
    val descripcion: String?,
    val valoracion: Double?,
    val imageLinks: ImageLinks
)
