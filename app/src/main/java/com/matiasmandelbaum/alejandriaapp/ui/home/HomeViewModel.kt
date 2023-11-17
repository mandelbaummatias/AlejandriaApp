package com.matiasmandelbaum.alejandriaapp.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.repository.BooksRepositoryImpl
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.repository.BooksRepository
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetAllBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllBooksUseCase: GetAllBooksUseCase,private val booksRepository: BooksRepository) :
    ViewModel() {
    private val _booksListState = MutableLiveData<Result<List<Book>>>()
    val bookListState: LiveData<Result<List<Book>>> = _booksListState


    val bookListState2: StateFlow<Result<List<Book>>> =  booksRepository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    fun getAllBooks() {
        _booksListState.postValue(Result.Loading)
        viewModelScope.launch {
            val booksResult = getAllBooksUseCase()
            _booksListState.postValue(booksResult)
        }
    }
}