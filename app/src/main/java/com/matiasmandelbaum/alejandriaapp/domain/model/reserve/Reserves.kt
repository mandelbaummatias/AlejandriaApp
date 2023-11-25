package com.matiasmandelbaum.alejandriaapp.domain.model.reserve

import com.google.firebase.Timestamp

data class Reserves(
    var isbn: String,
    var title: String,
    var author: String,
    var reserveDate: Timestamp,
    val status: String
) : Comparable<Reserves> {
    override fun compareTo(other: Reserves): Int {
        return other.reserveDate.compareTo(this.reserveDate)
    }
}