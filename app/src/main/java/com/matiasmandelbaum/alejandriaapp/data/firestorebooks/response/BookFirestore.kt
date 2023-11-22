package com.matiasmandelbaum.alejandriaapp.data.firestorebooks.response

import kotlinx.serialization.Serializable

//@Parcelize
@Serializable
data class BookFirestore(
    val autor: String = "",
    val titulo: String = "",
    val cantidad: Int = 0,
    val cantidad_disponible: Int = 0,
    val isbn: String = ""
)
