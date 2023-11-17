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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.ui.adapter.ReserveAdapter
import java.util.Date


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
        return inflater.inflate(R.layout.fragment_books_reserved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper: SnapHelper = LinearSnapHelper()

        getReservesForCurrentUser()

        recyclerView = view.findViewById(R.id.reserves_recycler_view)
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
        val reserveListEmptyTextView = view?.findViewById<TextView>(R.id.reserve_list_empty)

        if (currentUser != null) {
            val userEmail = currentUser.email

            val bookReserveCollection = db.collection("reservas_libros")
            bookReserveCollection.whereEqualTo("mail_usuario", userEmail)
                .get()
                .addOnSuccessListener { reserveQuerySnapshot ->
                    for (document in reserveQuerySnapshot) {
                        val isbn = document.getString("isbn")

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

        bookCollection.whereEqualTo("isbn", isbn).get()
            .addOnSuccessListener { bookQuerySnapshot ->
                for (bookDocument in bookQuerySnapshot) {
                    val title = bookDocument.getString("titulo") ?: ""
                    val author = bookDocument.getString("autor") ?: ""
                    val dateReserve = reserveDocument.getDate("fecha_reserva") ?:  Date()
                    val status = reserveDocument.getString("status") ?: ""
                    val reserve = Reserves(isbn, title, author, dateReserve, status)
                    reserveList.add(reserve)
                }
                reserveList.sort()
                adapter.notifyDataSetChanged()
            }
    }
}