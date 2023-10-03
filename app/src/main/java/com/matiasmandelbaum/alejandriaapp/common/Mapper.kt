package com.matiasmandelbaum.alejandriaapp.common

interface Mapper<F, T> {
    fun mapFrom(from: F): T
}