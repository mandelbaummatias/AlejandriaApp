package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.usecase.CreateReservationUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.FetchSubscriptionUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByIdUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ReserveBookUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.UpdateUserReservationStateUseCase
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BooksDetailViewModel"

@HiltViewModel
class BooksDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchSubscriptionUseCase: FetchSubscriptionUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val reserveBookUseCase: ReserveBookUseCase,
    private val updateUserReservationStateUseCase: UpdateUserReservationStateUseCase,
    private val createReservationUseCase: CreateReservationUseCase
) : ViewModel() {

    private val _subscriptionExists: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionExists: LiveData<Result<Subscription>> = _subscriptionExists

    private val _user: MutableLiveData<Result<User>?> = MutableLiveData()
    val user: MutableLiveData<Result<User>?> = _user

    private val _isEnabledToReserve = MutableLiveData<Boolean>()
    val isEnabledToReserve: LiveData<Boolean> = _isEnabledToReserve

    val book: Book = savedStateHandle["book"]!!

    private val _reservationState = MutableLiveData<ReservationState>()
    val reservationState: LiveData<ReservationState> = _reservationState

    private val _hasReservedBook = MutableLiveData<Boolean>()
    val hasReservedBook: LiveData<Boolean> = _hasReservedBook

    private val _dialogResult = MutableLiveData<Boolean>()
    val dialogResult: LiveData<Boolean> get() = _dialogResult

    private val _onSuccessfulReservation = MutableLiveData<Result<ReservationResult>?>()
    val onSuccessfulReservation: LiveData<Result<ReservationResult>?> = _onSuccessfulReservation

    fun reserveBook(userEmail: String) {
        _onSuccessfulReservation.value = Result.Loading
        viewModelScope.launch {
            when (val result = reserveBookUseCase(book.isbn, userEmail, book.cantidadDisponible)) {
                is Result.Success -> {
                    _isEnabledToReserve.postValue(false)
                    updateUserReservationStateUseCase(userEmail)
                    createReservationUseCase(book.isbn, userEmail)
                    _onSuccessfulReservation.postValue(result)

                }

                is Result.Error -> _onSuccessfulReservation.postValue(result)
                else -> {
                    Unit
                }
            }
        }
    }

    fun fetchSubscription(id: String) {
        Log.d(TAG, "ejecutando fetchSubscription")
        _subscriptionExists.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "subscription fetch")
            val result = fetchSubscriptionUseCase(id)
            Log.d(TAG, "a ver $result")
            _subscriptionExists.postValue(result)

        }
    }

    fun getUserById(userId: String) {
        Log.d(TAG, "getUserById")
        _user.value = Result.Loading
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            when(result){
                is Result.Success -> {
                    if(result.data.hasReservedBook != false){
                        _isEnabledToReserve.postValue(!result.data.hasReservedBook!!)
                    } else{
                        _isEnabledToReserve.postValue(true)
                    }
                }

                else -> {}
            }
            _user.value = result
        }
    }

    fun updateReservationState(isEnabled: Boolean) {
        Log.d(TAG, "updateReservationState $isEnabled")
        _isEnabledToReserve.value = isEnabled
    }

    fun resetUser() {
        Log.d(TAG, "resetUser()")
        _user.value = null
    }
}




