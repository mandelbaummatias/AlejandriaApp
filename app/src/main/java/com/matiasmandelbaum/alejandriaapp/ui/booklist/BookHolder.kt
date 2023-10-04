package com.matiasmandelbaum.alejandriaapp.ui.booklist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.ItemBookBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.Book
import com.squareup.picasso.Picasso


private const val TAG = "BookHolder"
//class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    private val titleTextView: TextView = itemView.findViewById(R.id.bookTitle)
//    private val authorTextView: TextView = itemView.findViewById(R.id.bookAuthor)
//    private val ratingTextView: TextView = itemView.findViewById(R.id.bookCalification)
//    private val coverImageView: ImageView = itemView.findViewById(R.id.imageView)

class BookHolder(private val item: ItemBookBinding) : RecyclerView.ViewHolder(item.root) {

    fun bind(book : Book) {
        val rating = book.valoracion?.toFloat() ?: 0.0f
        item.bookNameTextView.text = book.titulo
        item.publisherNameTextView.text = book.autor


        item.barraPuntaje.rating = rating;

        item.bookSmallThumbnail.load("${book.imageLinks?.smallThumbnail}")
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


//class BookHolder(private var binding: ItemBookBinding) :
//    RecyclerView.ViewHolder(binding.root) {
//    fun bind(book: Book) {
//        binding.book = book
//    }
//}