package com.matiasmandelbaum.alejandriaapp.ui.booklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.ui.booklist.Book

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BookListFragment : Fragment() {

    lateinit var v : View
    lateinit var bookRecycler : RecyclerView
    var books : MutableList<Book> = ArrayList()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var bookListAdapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_book_list, container, false)

        bookRecycler = v.findViewById(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(context)
        bookRecycler.layoutManager = linearLayoutManager
        bookListAdapter = BookListAdapter(books) // Asegúrate de crear el adaptador adecuado
        bookRecycler.adapter = bookListAdapter

        return v
    }

    override fun onStart() {
        super.onStart()

        for (i in 1 .. 200) {
            books.add(Book("Harry Potter y la Piedra Filosofal", "J.K. Rowling", 5.0f, "urlFalsa"))
            books.add(Book("Adan y Eva", "Mark Twain", 3.2f, "urlFalsa"))
            books.add(Book("El extraño caso del Dr. Jekyll y Mr. Hyde", "Robert Louis Stevenson", 4.2f, "urlFalsa"))
            books.add(Book("Los Ojos del Perro Siberiano", "Antonio Santa Ana", 3.7f, "urlFalsa"))
        }
        // Notificar al adaptador que los datos han cambiado
        bookListAdapter.notifyDataSetChanged()
    }
}
