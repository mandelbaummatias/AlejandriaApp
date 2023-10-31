package com.matiasmandelbaum.alejandriaapp.ui.booksreserved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.ui.adapter.ReserveAdapter
import java.text.SimpleDateFormat


class BooksReservedFragment : Fragment() {
    private lateinit var adapter: ReserveAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var reserveList: ArrayList<Reserves>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_books_reserved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper: SnapHelper = LinearSnapHelper()

        getReservesForCurrentUser()

        recyclerView = view.findViewById(R.id.reservesRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ReserveAdapter(reserveList)
        recyclerView.adapter = adapter
        snapHelper.attachToRecyclerView(recyclerView)
    }


    private fun getReservesForCurrentUser() {
        reserveList = ArrayList()

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val reserveListEmptyTextView = view?.findViewById<TextView>(R.id.reserveListEmpty)

        if (currentUser != null) {
            val userEmail = currentUser.email

            val bookReserveCollection = db.collection("reservas_libros")
            bookReserveCollection.whereEqualTo("mail_usuario", userEmail)
                .get()
                .addOnSuccessListener { reserveQuerySnapshot ->
                    for (document in reserveQuerySnapshot) {
                        val isbn = document.getString("isbn_13")

                        if (isbn != null) {
                            this.getBookDetails(isbn, document)
                        }
                    }
                    if (reserveQuerySnapshot.isEmpty) {
                        reserveListEmptyTextView?.text = getString(R.string.noReservedBooksMsg)
                        reserveListEmptyTextView?.visibility = View.VISIBLE
                    } else {
                        reserveListEmptyTextView?.visibility = View.GONE
                    }
                }
        }else{
            reserveListEmptyTextView?.text = getString(R.string.notLogedReservedBooksMsg)
            reserveListEmptyTextView?.visibility = View.VISIBLE
        }
    }

    private fun getBookDetails(isbn: String, reserveDocument: DocumentSnapshot) {

        val db = FirebaseFirestore.getInstance()
        val bookCollection = db.collection("libros")

        bookCollection.whereEqualTo("isbn_13", isbn).get()
            .addOnSuccessListener { bookQuerySnapshot ->
                for (bookDocument in bookQuerySnapshot) {
                    val title = bookDocument.getString("titulo") ?: ""
                    val author = bookDocument.getString("autor") ?: ""
                    val dateReserve = reserveDocument.getDate("fecha_reserva")

                    val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm")
                    val reserveDate = sdf.format(dateReserve)

                    val reserve = Reserves(isbn, title, author, reserveDate)
                    reserveList.add(reserve)
                }
                reserveList.sort()
                adapter.notifyDataSetChanged()
            }
    }
}