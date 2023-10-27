package com.matiasmandelbaum.alejandriaapp.common.result

sealed class    Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()

    data class Finished<out T>(val data: T) : Result<T>()
}

