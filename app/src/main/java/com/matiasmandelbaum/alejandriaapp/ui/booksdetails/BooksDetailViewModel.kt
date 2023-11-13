package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

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

    private val _subscriptionState: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionState: LiveData<Result<Subscription>> = _subscriptionState

    private val _subscriptionExists: MutableLiveData<Boolean> = MutableLiveData()
    val subscriptionExists: LiveData<Boolean> = _subscriptionExists

    private val _user: MutableLiveData<Result<User>?> = MutableLiveData()
    val user: LiveData<Result<User>?> = _user

    private val _isEnabledToReserve = MutableLiveData<Boolean>()
    val isEnabledToReserve: LiveData<Boolean> = _isEnabledToReserve

    private val _onSuccessfulReservation = MutableLiveData<Result<ReservationResult>?>()
    val onSuccessfulReservation: LiveData<Result<ReservationResult>?> = _onSuccessfulReservation

    val book: Book = savedStateHandle["book"]!!

    fun reserveBook(userEmail: String) {
        viewModelScope.launch {
            when (val result = reserveBookUseCase(book.isbn, userEmail, book.cantidadDisponible)) {
                is Result.Success -> {
                    handleSuccessfulReservation(result, userEmail)
                }
                is Result.Error -> postOnSuccessfulReservation(result)
                else -> Unit
            }
        }
    }

    private fun handleSuccessfulReservation(result: Result.Success<ReservationResult>, userEmail: String) {
        viewModelScope.launch {
            postOnSuccessfulReservation(result)
            _isEnabledToReserve.postValue(false)
            updateUserReservationStateUseCase(userEmail)
            createReservationUseCase(book.isbn, userEmail)
        }
    }

    private fun postOnSuccessfulReservation(result: Result<ReservationResult>) {
        _onSuccessfulReservation.postValue(result)
    }

    private fun fetchSubscription(id: String) {
        _subscriptionState.value = Result.Loading
        viewModelScope.launch {
            val result = fetchSubscriptionUseCase(id)
            _subscriptionState.postValue(result)
        }
    }

    fun getUserById(userId: String) {
        _user.value = Result.Loading
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            handleUserResult(result)
        }
    }

    private fun handleUserResult(result: Result<User>) {
        when (result) {
            is Result.Success -> handleSuccess(result.data)
            else -> _user.value = result
        }
    }

    private fun handleSuccess(user: User) {
        if (user.hasReservedBook != false) {
            _isEnabledToReserve.postValue(!user.hasReservedBook!!)
        } else {
            _isEnabledToReserve.postValue(true)
        }

        val subscriptionId = user.subscriptionId

        if (subscriptionId.isNotBlank()) {
            fetchSubscription(subscriptionId)
        } else {
            _subscriptionExists.postValue(false)
        }
        _user.value = Result.Success(user)
    }

    fun resetUser() {
        _user.value = null
    }
}