package com.matiasmandelbaum.alejandriaapp.ui.booklist


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.ItemBookBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.Book

private const val TAG = "BookListAdapter"

//class BookListAdapter(private val booksList: MutableList<Book>) : ListAdapter<Book, BookHolder>(
class BookListAdapter : ListAdapter<Book, BookHolder>(
    DiffCallback
) {
    //  override fun getItemCount() = booksList.size

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
        //  val book = booksList[position]
        val book = getItem(position)
        holder.bind(book)
    }

    override fun submitList(list: List<Book>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldBook: Book, newBook: Book): Boolean {
                Log.d(TAG, "areItemsTheSame?")
                return oldBook === newBook
                // return oldBook.itemId == newBook.itemId
            }

            override fun areContentsTheSame(oldBook: Book, newBook: Book): Boolean {
                Log.d(TAG, "areContentsTheSame?")
                return oldBook.titulo == newBook.titulo
            }

        }

    }

}

