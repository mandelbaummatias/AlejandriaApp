package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.domain.usecase.CreateSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.usecase.FetchSubscriptionUseCase
import javax.inject.Inject

private const val TAG = "SubscriptionViewModel"
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val createSubscriptionUseCase: CreateSubscriptionUseCase,
    private val fetchSubscriptionUseCase : FetchSubscriptionUseCase
) : ViewModel() {

    init {
        Log.d(TAG, "init")
    }

    private val _subscription: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscription: LiveData<Result<Subscription>> = _subscription

//    val subscription: StateFlow<Result<Subscription>> = createSubscriptionUseCase()
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = Result.Loading
//        )

    private val _subscriptionStatus: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionStatus: LiveData<Result<Subscription>> = _subscriptionStatus

//    private val _subscriptionId: MutableLiveData<Result<String>> = MutableLiveData()
//    val subscriptionId: LiveData<Result<String>> = _subscriptionId

    fun createSubscription() {
        _subscription.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "click en subscription")
            val result = createSubscriptionUseCase()
            _subscription.postValue(result)
        }
    }

//    fun createSubscription() {
//        viewModelScope.launch {
//            createSubscriptionUseCase().onEach {
//                _subscription.value = it
//            }.launchIn(viewModelScope)
//        }
//    }

//    fun fetchSubscription(id: String) {
//        Log.d(TAG, "ejecutando fetchSubscription")
//        _subscriptionId.value = Result.Loading
//        viewModelScope.launch {
//            Log.d(TAG, "subscription fetch")
//            val result = fetchSubscriptionUseCase(id)
//            Log.d(TAG, "a ver $result")
//            _subscriptionId.postValue(result)
//        }
//
//    }

    fun fetchSubscription(id: String)  {
        Log.d(TAG, "ejecutando fetchSubscription")
        _subscriptionStatus.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "subscription fetch")
            val result = fetchSubscriptionUseCase(id)
            Log.d(TAG, "a ver $result")
            _subscriptionStatus.postValue(result)

        }
    }
}