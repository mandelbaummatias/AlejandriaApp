package com.matiasmandelbaum.alejandriaapp.ui.booklist

import BookHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matiasmandelbaum.alejandriaapp.R

class BookListAdapter (private val booksList: MutableList<Book>) : RecyclerView.Adapter<BookHolder>() {
    override fun getItemCount() = booksList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return (BookHolder(view))
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        val book = booksList[position]
        holder.bind(book)
    }

}

