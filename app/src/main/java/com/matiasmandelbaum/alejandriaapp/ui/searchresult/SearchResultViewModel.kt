package com.matiasmandelbaum.alejandriaapp.ui.searchresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.TITLE
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetBooksByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val getBooksByTitleUseCase: GetBooksByTitleUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _booksListState = MutableLiveData<Result<List<Book>>>()
    val bookListState: LiveData<Result<List<Book>>> = _booksListState

    private val titulo: String = savedStateHandle[TITLE]!!

    fun getBooksByTitle() {
        _booksListState.postValue(Result.Loading)
        viewModelScope.launch {
            val booksResult = getBooksByTitleUseCase(titulo)
            _booksListState.postValue(booksResult)
        }
    }
}

