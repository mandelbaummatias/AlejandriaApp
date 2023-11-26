package com.matiasmandelbaum.alejandriaapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.reservas.ReservationsConstants.PENDING_STATUS
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils
import com.matiasmandelbaum.alejandriaapp.databinding.ItemReserveBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.reserve.Reserves
import java.text.SimpleDateFormat
import java.util.Locale

class ReserveAdapter(private val reserveList: ArrayList<Reserves>) :
    RecyclerView.Adapter<ReserveAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemReserveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reserveList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(reserveList[position])
    }

    class MyViewHolder(private val binding: ItemReserveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reserve: Reserves) = with(binding) {
            reservedBookIsbn.text = reserve.isbn
            reservedBookTitle.text = reserve.title
            reservedBookAuthor.text = reserve.author

            val sdf = SimpleDateFormat(TimeUtils.DAY_MONTH_YEAR_HOUR_MIN, Locale.getDefault())
            reservedBookDate.text = sdf.format(reserve.reserveDate.toDate())

            reservedBookStatus.text = if (reserve.status == PENDING_STATUS) {
                itemView.context.getString(R.string.reserved_book_status_not_retired)
            } else {
                reserve.status
            }
        }
    }
}