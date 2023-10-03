package com.matiasmandelbaum.alejandriaapp.common

interface ListMapper<F, T>: Mapper<F,T> {
    fun mapList(from: List<F>): List<T>
}