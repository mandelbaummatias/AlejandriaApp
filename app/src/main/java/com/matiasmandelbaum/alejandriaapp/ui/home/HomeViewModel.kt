package com.matiasmandelbaum.alejandriaapp.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetAllBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllBooksUseCase: GetAllBooksUseCase,private val booksRepository: BooksRepository) :
    ViewModel() {
    val bookListState: StateFlow<Result<List<Book>>> =  booksRepository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

//
//    val bookListState2: StateFlow<Result<List<Book>>> =  booksRepository.getAllItems()
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = Result.Loading
//        )

    // Function to load more items


//    fun getAllBooks() {
//        _booksListState.postValue(Result.Loading)
//        viewModelScope.launch {
//            val booksResult = getAllBooksUseCase()
//            _booksListState.postValue(booksResult)
//        }
//    }

}