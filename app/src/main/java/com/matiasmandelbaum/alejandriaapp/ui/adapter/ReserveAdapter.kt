package com.matiasmandelbaum.alejandriaapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.ui.booksreserved.Reserves

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
        holder.reserveDate.text = currentReserve.reserveDate
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val isbn : TextView = itemView.findViewById(R.id.reservedBookIsbn)
        val title : TextView = itemView.findViewById(R.id.reservedBookTitle)
        val author : TextView = itemView.findViewById(R.id.reservedBookAuthor)
        val reserveDate : TextView = itemView.findViewById(R.id.reservedBookDate)
    }
}