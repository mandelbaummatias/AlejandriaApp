package com.matiasmandelbaum.alejandriaapp.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.Book
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetAllBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllBooksUseCase: GetAllBooksUseCase) :
    ViewModel() {
    private val _booksListState = MutableLiveData<Result<List<Book>>>()
    val bookListState: LiveData<Result<List<Book>>> = _booksListState

    fun getAllBooks() {
        _booksListState.postValue(Result.Loading)
        viewModelScope.launch {
            val booksResult = getAllBooksUseCase()
            _booksListState.postValue(booksResult)
        }
    }
}