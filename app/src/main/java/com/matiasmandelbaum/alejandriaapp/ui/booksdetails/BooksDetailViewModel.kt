package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.matiasmandelbaum.alejandriaapp.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "BooksDetailViewModel"

@HiltViewModel
class BooksDetailViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _book = MutableLiveData<Book>()
    // val book : LiveData<Book> = _book

    val book: Book = savedStateHandle["book"]!!

    init {

    }

    fun onCreate(){
        Log.d(TAG, "init!")
        Log.d(TAG, "mi libro $book")
    }

}


