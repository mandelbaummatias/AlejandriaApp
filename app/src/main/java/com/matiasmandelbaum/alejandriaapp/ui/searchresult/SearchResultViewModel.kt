package com.matiasmandelbaum.alejandriaapp.ui.searchresult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetBooksByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.algolia.search.client.ClientSearch
import com.algolia.search.configuration.ConfigurationSearch
import com.algolia.search.dsl.query
import javax.inject.Inject

private const val TAG = "SearchResultViewModel"

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val getBooksByTitleUseCase: GetBooksByTitleUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _booksListState = MutableLiveData<Result<List<Book>>>()
    val bookListState: LiveData<Result<List<Book>>> = _booksListState

    private val titulo: String = savedStateHandle["titulo"]!!

    fun getBooksByTitle() {
        Log.d(TAG, "getBooksByTitle")
        _booksListState.postValue(Result.Loading)
        viewModelScope.launch {
            val booksResult = getBooksByTitleUseCase(titulo)
            _booksListState.postValue(booksResult)
        }
    }

    override fun onCleared() {
        super.onCleared()
       // searcher.cancel()
    }
}

