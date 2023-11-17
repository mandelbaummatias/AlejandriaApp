package com.matiasmandelbaum.alejandriaapp.ui.booksreserved

import com.google.firebase.Timestamp
import java.util.Date

//data class Reserves(var isbn: String, var title: String, var author: String, var reserveDate: Date, val status: String) : Comparable<Reserves> {
//    override fun compareTo(other: Reserves): Int {
//        return other.reserveDate.compareTo(this.reserveDate)
//    }
//}
data class Reserves(var isbn: String, var title: String, var author: String, var reserveDate: Timestamp, val status: String) : Comparable<Reserves> {
    override fun compareTo(other: Reserves): Int {
        return other.reserveDate.compareTo(this.reserveDate)
    }
}