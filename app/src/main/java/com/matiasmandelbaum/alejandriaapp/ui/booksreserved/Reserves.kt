package com.matiasmandelbaum.alejandriaapp.ui.booksreserved

data class Reserves(var isbn : String, var title : String, var author : String, var reserveDate : String): Comparable<Reserves> {
    override fun compareTo(other: Reserves): Int {
        return other.reserveDate.compareTo(this.reserveDate)
    }
}