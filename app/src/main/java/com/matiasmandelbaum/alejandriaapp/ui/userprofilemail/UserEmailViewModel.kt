package com.matiasmandelbaum.alejandriaapp.ui.userprofilemail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.Event
import com.matiasmandelbaum.alejandriaapp.domain.usecase.SendEmailVerificationUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.VerifyEmailBeforeUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "UserEmailViewModel"

@HiltViewModel
class UserEmailViewModel @Inject constructor(
) : ViewModel() {


//    init {
//        viewModelScope.launch {
//            verifyEmailBeforeUpdate(newEmail)
//                .catch {
//                    Log.i(TAG, "Verification error: ${it.message}")
//                }
//                .collect { verification ->
//                    if (verification) {
//                        _showContinueButton.value = Event(verification)
//                    }
//                }
//
//        }
//    }
//
//    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
//    val showContinueButton: LiveData<Event<Boolean>>
//        get() = _showContinueButton
//
//
//    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
//    val navigateToVerifyEmail: LiveData<Event<Boolean>>
//        get() = _navigateToVerifyEmail
//
//
//    fun onChangeSubmitted(){
//        sendVerificationToNewEmail()
//    }
//
//    private fun sendVerificationToNewEmail() {
//        viewModelScope.launch {
//
//        }
//    }

}