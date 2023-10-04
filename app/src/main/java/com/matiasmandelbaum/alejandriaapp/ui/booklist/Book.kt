package com.matiasmandelbaum.alejandriaapp.ui.booklist

// No queremos que los objetos de tipo Book muten, por lo que me pareció correcto usar una data class

data class Book(
    val title: String?,
    val author: String?,
    val rating: Float?,
    val imageUrl: String?
)
