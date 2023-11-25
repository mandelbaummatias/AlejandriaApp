package com.matiasmandelbaum.alejandriaapp.ui.booksreserved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.reserve.Reserves
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetReservedBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksReservedViewModel @Inject constructor(
    private val getReservedBooksUseCase: GetReservedBooksUseCase
) : ViewModel() {

    private val _reserveList = MutableLiveData<Result<List<Reserves>>>()
    val reserveList: LiveData<Result<List<Reserves>>> = _reserveList

    fun getReservesForCurrentUser(userEmail: String) {
        _reserveList.value = Result.Loading
        viewModelScope.launch {
            val result = getReservedBooksUseCase(userEmail)
            _reserveList.postValue(result)
        }
    }
}