package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.libros.LibrosConstants.BOOK
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.usecase.CreateReservationUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.FetchSubscriptionUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByIdUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ReserveBookUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.UpdateUserReservationStateUseCase
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private val _subscriptionUser: MutableLiveData<Result<SubscriptionUser>?> = MutableLiveData()
    val subscriptionUser: LiveData<Result<SubscriptionUser>?> = _subscriptionUser

    private val _isEnabledToReserve = MutableLiveData<Boolean>()
    val isEnabledToReserve: LiveData<Boolean> = _isEnabledToReserve

    private val _onSuccessfulReservation = MutableLiveData<Result<ReservationResult>?>()
    val onSuccessfulReservation: LiveData<Result<ReservationResult>?> = _onSuccessfulReservation

    private val _onFailedReservation = MutableLiveData<Result<ReservationResult>?>()
    val onFailedReservation: LiveData<Result<ReservationResult>?> = _onFailedReservation


    val book: Book = savedStateHandle[BOOK]!!

    fun reserveBook(userEmail: String) {
        viewModelScope.launch {
            when (val result = reserveBookUseCase(book.isbn, userEmail)) {
                is Result.Success -> {
                    handleSuccessfulReservation(result, userEmail)
                }

                is Result.Error -> {
                    postOnFailedReservation(result)
                }

                else -> Unit
            }
        }
    }

    private fun handleSuccessfulReservation(
        result: Result.Success<ReservationResult>,
        userEmail: String
    ) {
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

    private fun postOnFailedReservation(result: Result<ReservationResult>) {
        _onFailedReservation.postValue(result)
    }

    private fun fetchSubscription(id: String) {
        _subscriptionState.value = Result.Loading
        viewModelScope.launch {
            val result = fetchSubscriptionUseCase(id)
            _subscriptionState.postValue(result)
        }
    }

    fun getUserById(userId: String) {
        _subscriptionUser.value = Result.Loading
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            handleUserResult(result)
        }
    }

    private fun handleUserResult(result: Result<SubscriptionUser>) {
        when (result) {
            is Result.Success -> handleSuccess(result.data)
            else -> _subscriptionUser.value = result
        }
    }

    private fun handleSuccess(subscriptionUser: SubscriptionUser) {
        if (subscriptionUser.hasReservedBook != false) {
            _isEnabledToReserve.postValue(!subscriptionUser.hasReservedBook!!)
        } else {
            _isEnabledToReserve.postValue(true)
        }

        val subscriptionId = subscriptionUser.subscriptionId

        if (subscriptionId.isNotBlank()) {
            fetchSubscription(subscriptionId)
        } else {
            _subscriptionExists.postValue(false)
        }
        _subscriptionUser.value = Result.Success(subscriptionUser)
    }

    fun resetUser() {
        _subscriptionUser.value = null
    }
}