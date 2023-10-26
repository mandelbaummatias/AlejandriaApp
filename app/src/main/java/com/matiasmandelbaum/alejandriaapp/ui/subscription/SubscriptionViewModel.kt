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
import com.matiasmandelbaum.alejandriaapp.domain.usecase.AddSubscriptionIdToUserUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.FetchSubscriptionUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByIdUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserBySubscriptionIdUseCase
import javax.inject.Inject

private const val TAG = "SubscriptionViewModel"

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val createSubscriptionUseCase: CreateSubscriptionUseCase,
    private val fetchSubscriptionUseCase: FetchSubscriptionUseCase,
    private val addSubscriptionIdToUserUseCase: AddSubscriptionIdToUserUseCase,
    private val getUserBySubscriptionIdUseCase: GetUserBySubscriptionIdUseCase,
    //private val checkUserHasSubscriptionIdUseCase: CheckUserHasSubscriptionIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    init {
        Log.d(TAG, "init")
    }


    private val _subscription: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscription: LiveData<Result<Subscription>> = _subscription


    private val _subscriptionStatus: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionStatus: LiveData<Result<Subscription>> = _subscriptionStatus

    private val _subscriptionExists: MutableLiveData<Result<Subscription>> = MutableLiveData()
    val subscriptionExists: LiveData<Result<Subscription>> = _subscriptionExists

    private val _user: MutableLiveData<Result<User>> = MutableLiveData()
    val user: LiveData<Result<User>> = _user


    private val _subscriptionUrl: MutableLiveData<String> = MutableLiveData()
    val subscriptionUrl: LiveData<String> = _subscriptionUrl


    fun createSubscription() {
        Log.d(TAG, "createSubscription()")
        _subscription.value = Result.Loading
        viewModelScope.launch {
            //Log.d(TAG, "click en subscription")
            val result = createSubscriptionUseCase()
            _subscription.postValue(result)
        }
    }


    fun continueSubscription(id: String) {
        Log.d(TAG, "continueSubscription with $id")
        _subscription.value = Result.Loading
        viewModelScope.launch {
            Log.d(TAG, "click en subscription")
            val result = fetchSubscriptionUseCase(id)
            _subscription.postValue(result)
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

        fun addSubscriptionIdToUser(subscriptionId: String, userId: String) {
            Log.d(TAG, "addSubscription ")
            viewModelScope.launch {
                addSubscriptionIdToUserUseCase(subscriptionId, userId)
            }
        }


        fun getUserById(userId: String) {
            Log.d(TAG, "getUserById")
            _user.value = Result.Loading
            viewModelScope.launch {
                val result = getUserByIdUseCase(userId)
                _user.postValue(result)
            }
        }

    fun notifyUserSignOut(userId: String) {
        Log.d(TAG, "getUserById")
        _user.value = Result.Loading
        viewModelScope.launch {
            val result = getUserByIdUseCase(userId)
            _user.postValue(result)
        }
    }

        fun updateInitPointUrl(initPoint: String) {
            Log.d(TAG, "updateInitPoint $initPoint")
            _subscriptionUrl.value = initPoint
        }
    }

