package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.matiasmandelbaum.alejandriaapp.data.googlebooks.model.components.ImageLinks
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksDetailsBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.Book
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBooksDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksDetailsBinding.inflate(inflater, container, false)

        val book = getBookDetails()

        binding.bookIsbn.text = "ISBN ${book.isbn}"
        binding.bookTitle.text = book.titulo
        binding.bookAuthor.text = book.autor
        Picasso.get().load(book.imageLinks?.smallThumbnail).into(binding.bookCover);
        binding.bookCalification.text = "${book.valoracion}"
        binding.bookSynopsis.text = book.descripcion

        binding.bookReserveBtn.setOnClickListener {
            //TODO: Reservar libro
            Toast.makeText(context, "Reserva de Libro.", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
    private fun getBookDetails() : Book {

        //TODO: Obtener detalles del libro
        val bookFound = Book(
            isbn = "9788498382662",
            titulo = "Harry Potter y la piedra filosofal",
            autor = "J. K. Rowling",
            descripcion = "Harry Potter se ha quedado huérfano y vive en casa de sus abominables tíos y del insoportable primo Dudley. Harry se siente muy triste y solo, hasta que un buen día recibe una carta que cambiará su vida para siempre. En ella le comunican que ha sido aceptado como alumno en el colegio interno Hogwarts de magia y hechicería. A partir de ese momento, la suerte de Harry da un vuelco espectacular. En esa escuela tan especial aprenderá encantamientos, trucos fabulosos y tácticas de defensa contra las malas artes. Se convertirá en el campeón escolar de quidditch, especie de fútbol aéreo que se juega montado sobre escobas, y se hará un puñado de buenos amigos... aunque también algunos temibles enemigos. Pero sobre todo, conocerá los secretos que le permitirán cumplir con su destino. Pues, aunque no lo parezca a primera vista, Harry no es un chico común y corriente. ¡Es un verdadero mago!",
            valoracion = 4.5,
            imageLinks = ImageLinks(smallThumbnail = "https://images.cdn2.buscalibre.com/fit-in/360x360/e3/bc/e3bcd85377567759874a0664f894a67b.jpg")
        )
        return bookFound
    }
}