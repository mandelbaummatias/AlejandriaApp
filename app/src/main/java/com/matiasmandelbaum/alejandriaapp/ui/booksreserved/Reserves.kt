package com.matiasmandelbaum.alejandriaapp.ui.booksreserved

import java.util.Date

data class Reserves(var isbn: String, var title: String, var author: String, var reserveDate: Date) : Comparable<Reserves> {
    override fun compareTo(other: Reserves): Int {
        return other.reserveDate.compareTo(this.reserveDate)
    }
}