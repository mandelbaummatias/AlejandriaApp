package com.matiasmandelbaum.alejandriaapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.ui.booksreserved.Reserves
import java.text.SimpleDateFormat

class ReserveAdapter(private val reserveList : ArrayList<Reserves>) : RecyclerView.Adapter<ReserveAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val reserveView = LayoutInflater.from(parent.context).inflate(R.layout.item_reserve,
            parent, false)
        return MyViewHolder(reserveView)
    }

    override fun getItemCount(): Int {
        return reserveList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentReserve = reserveList[position]
        holder.isbn.text = currentReserve.isbn
        holder.title.text = currentReserve.title
        holder.author.text = currentReserve.author

        val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm")
        val stringDate = dateFormat.format(currentReserve.reserveDate)
        holder.reserveDate.text = stringDate

        if (currentReserve.status == "A retirar") {
            holder.status.text = holder.itemView.context.getString(R.string.reservedBookStatusNotRetired)
        } else {
            holder.status.text = currentReserve.status
        }
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val isbn : TextView = itemView.findViewById(R.id.reserved_book_isbn)
        val title : TextView = itemView.findViewById(R.id.reserved_book_title)
        val author : TextView = itemView.findViewById(R.id.reserved_book_author)
        val reserveDate : TextView = itemView.findViewById(R.id.reserved_book_date)
        val status : TextView = itemView.findViewById(R.id.reserved_book_status)
    }
}