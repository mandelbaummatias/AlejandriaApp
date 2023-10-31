package com.matiasmandelbaum.alejandriaapp.common.mapper

interface Mapper<F, T> {
    fun mapFrom(from: F): T
}