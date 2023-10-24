package com.matiasmandelbaum.alejandriaapp.common.mapper

interface ListMapper<F, T>: Mapper<F, T> {
    fun mapList(from: List<F>): List<T>
}