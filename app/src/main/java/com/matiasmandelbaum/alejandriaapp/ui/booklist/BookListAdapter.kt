package com.matiasmandelbaum.alejandriaapp.ui.booklist


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.ItemBookBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.Book

private const val TAG = "BookListAdapter"

//class BookListAdapter(private val booksList: MutableList<Book>) : ListAdapter<Book, BookHolder>(
class BookListAdapter(val clickListener: BookListener) : ListAdapter<Book, BookListAdapter.BookHolder>(
    DiffCallback
) {
    class BookHolder(private val item: ItemBookBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(book : Book, clickListener: BookListener) {
            val rating = book.valoracion?.toFloat() ?: 0.0f
           // val rating = book.valoracion
            item.bookNameTextView.text = book.titulo
            item.publisherNameTextView.text = book.autor
            item.barraPuntaje.rating = rating;
//            item.ratingTextView.text = rating?.let{
//                "Valoración: ${it}"
//            }
            item.bookSmallThumbnail.load("${book.imageLinks?.smallThumbnail}")
            item.book = book
            item.clickListener = clickListener

//        item.bookNameTextView.text = book.titulo
//        //authorTextView.text = book.autor
//        //  ratingTextView.text = book.valoracion?.toString()
//        // coverImageView.load(book.imageLinks?.smallThumbnail)
//        // coverImageView.load(book.imageLinks?.smallThumbnail)
//        item.bookSmallThumbnail.load(book.imageLinks?.smallThumbnail)
            Log.d(TAG, "${book.imageLinks?.smallThumbnail}")

        }

        // Cargar la imagen utilizando Glide (u otra biblioteca de carga de imágenes)
//        Glide.with(itemView.context)
//            .load(book.imageLinks?.smallThumbnail)
//            .placeholder(R.drawable.ic_menu_book)
//            .into(coverImageView)

//        Picasso.get().load(book.imageLinks?.smallThumbnail)
//            .placeholder(R.drawable.ic_menu_book)
//            .into(coverImageView);
//    }
    }

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
        holder.bind(book,clickListener)

    }

    override fun submitList(list: List<Book>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldBook: Book, newBook: Book): Boolean {
                Log.d(TAG, "areItemsTheSame")
                return oldBook === newBook
                // return oldBook.itemId == newBook.itemId
            }

            override fun areContentsTheSame(oldBook: Book, newBook: Book): Boolean {
                Log.d(TAG, "areContentsTheSame")
                return oldBook.titulo == newBook.titulo
            }

        }

    }



}

class BookListener(val clickListener: (book: Book) -> Unit) {
    fun onClick(book:Book) = clickListener(book)
}


