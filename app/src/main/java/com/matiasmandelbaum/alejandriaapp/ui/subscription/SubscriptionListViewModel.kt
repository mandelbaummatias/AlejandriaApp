package com.matiasmandelbaum.alejandriaapp.ui.subscription

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
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubscriptionListViewModel @Inject constructor(
    private val createSubscriptionUseCase: CreateSubscriptionUseCase,
    private val fetchSubscriptionUseCase: FetchSubscriptionUseCase,
    private val addSubscriptionIdToUserUseCase: AddSubscriptionIdToUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {


    private val _subscription: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscription: LiveData<Result<Subscription>> = _subscription

    private val _subscriptionExists: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionExists: LiveData<Result<Subscription>> = _subscriptionExists

    private val _subscriptionUser: MutableLiveData<Result<SubscriptionUser>?> = MutableLiveData()
    val subscriptionUser: MutableLiveData<Result<SubscriptionUser>?> = _subscriptionUser

    fun createSubscription(payerEmail: String) {
        _subscription.value = Result.Loading
        viewModelScope.launch {
            val result = createSubscriptionUseCase(payerEmail)
            _subscription.postValue(result)
        }
    }


    fun continueSubscription(id: String) {
        _subscription.value = Result.Loading
        viewModelScope.launch {
            val result = fetchSubscriptionUseCase(id)
            _subscription.postValue(result)
        }
    }

    fun fetchSubscription(id: String) {
        viewModelScope.launch {
            val result = fetchSubscriptionUseCase(id)
            _subscriptionExists.postValue(result)
        }
    }

    fun getUserById(userId: String) {
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _subscriptionUser.postValue(result)
        }
    }

    fun addSubscriptionIdToUser(subscriptionId: String, userId: String) {
        viewModelScope.launch {
            addSubscriptionIdToUserUseCase(subscriptionId, userId)
        }
    }
}