package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksDetailsBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBooksDetailsBinding
    private val viewModel: BooksDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksDetailsBinding.inflate(inflater, container, false)
        viewModel.onCreate()

        val rating = viewModel.book.valoracion

        binding.bookIsbn.text = "ISBN : ${viewModel.book.isbn}"
        binding.bookTitle.text = viewModel.book.titulo
        binding.bookAuthor.text = viewModel.book.autor
        Picasso.get().load(viewModel.book.imageLinks?.smallThumbnail).into(binding.bookCover);
        binding.bookCalification.text = rating?.let{
            "Valoración: $it"
        }
        binding.bookSynopsis.text = viewModel.book.descripcion

        binding.bookReserveBtn.setOnClickListener {
            //TODO: Reservar libro
            Toast.makeText(context, "Reserva de Libro.", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
}