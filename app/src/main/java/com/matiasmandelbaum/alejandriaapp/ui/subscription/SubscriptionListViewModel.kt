package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.usecase.AddSubscriptionIdToUserUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.CreateSubscriptionUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.FetchSubscriptionUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByIdUseCase
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SubscriptionListViewModel"

@HiltViewModel
class SubscriptionListViewModel @Inject constructor(
    private val createSubscriptionUseCase: CreateSubscriptionUseCase,
    private val fetchSubscriptionUseCase: FetchSubscriptionUseCase,
    private val addSubscriptionIdToUserUseCase: AddSubscriptionIdToUserUseCase, //este
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {


    private val _subscription: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscription: LiveData<Result<Subscription>> = _subscription

    private val _subscriptionExists: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionExists: LiveData<Result<Subscription>> = _subscriptionExists

    private val _user: MutableLiveData<Result<User>?> = MutableLiveData()
    val user: MutableLiveData<Result<User>?> = _user

    fun createSubscription(payerEmail: String) {
        Log.d(TAG, "createSubscription()")
        _subscription.value = Result.Loading
        viewModelScope.launch {
            val result = createSubscriptionUseCase(payerEmail)
            _subscription.postValue(result)
        }
    }


    fun continueSubscription(id: String) {
        Log.d(TAG, "continueSubscription with $id")
        _subscription.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "click en subscription")
            val result = fetchSubscriptionUseCase(id)
            //_subscription.postValue(result)
            _subscription.postValue(result)
        }

    }

    fun fetchSubscription(id: String) {
        Log.d(TAG, "ejecutando fetchSubscription")
        // _subscriptionExists.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "subscription fetch")
            val result = fetchSubscriptionUseCase(id)
            Log.d(TAG, "a ver $result")
            // _subscriptionExists.postValue(result)
            _subscriptionExists.value = result

        }
    }

    fun getUserById(userId: String) {
        Log.d(TAG, "getUserById")
        // _user.value = Result.Loading
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
              _user.postValue(result)
           // _user.value = result
        }
    }

    fun resetUser() {
        Log.d(TAG, "resetUser()")
        //_user.postValue(null)
       // _user.value = null
    }

    fun addSubscriptionIdToUser(subscriptionId: String, userId: String) {
        Log.d(TAG, "addSubscription ")
        viewModelScope.launch {
            addSubscriptionIdToUserUseCase(subscriptionId, userId)
        }
    }
}