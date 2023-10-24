package com.matiasmandelbaum.alejandriaapp.ui.userprofilemail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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