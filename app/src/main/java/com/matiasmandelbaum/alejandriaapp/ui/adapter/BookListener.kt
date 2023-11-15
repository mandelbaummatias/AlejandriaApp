package com.matiasmandelbaum.alejandriaapp.ui.adapter

import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book

class BookListener(val clickListener: (book: Book) -> Unit) {
    fun onClick(book: Book) = clickListener(book)
}