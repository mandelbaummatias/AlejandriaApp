package com.matiasmandelbaum.alejandriaapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.matiasmandelbaum.alejandriaapp.databinding.ItemBookBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book


class BookListAdapter(private val clickListener: BookListener) :
    ListAdapter<Book, BookListAdapter.BookHolder>(
        DiffCallback
    ) {
    class BookHolder(private val item: ItemBookBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(book: Book, clickListener: BookListener) {
            val rating = book.valoracion?.toFloat() ?: 0.0f
            item.bookNameTextView.text = book.titulo
            item.publisherNameTextView.text = book.autor
            item.barraPuntaje.rating = rating;
            item.bookSmallThumbnail.load("${book.imageLinks?.smallThumbnail}")
            item.book = book
            item.clickListener = clickListener

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        return BookHolder(
            ItemBookBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book, clickListener)

    }

    override fun submitList(list: List<Book>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldBook: Book, newBook: Book): Boolean {
                return oldBook === newBook
            }

            override fun areContentsTheSame(oldBook: Book, newBook: Book): Boolean {
                return oldBook.titulo == newBook.titulo
            }
        }
    }
}




