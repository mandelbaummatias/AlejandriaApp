package com.matiasmandelbaum.alejandriaapp.ui.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.event.Event
import com.matiasmandelbaum.alejandriaapp.domain.usecase.SendEmailVerificationUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "VerificationViewModel"
@HiltViewModel
class VerificationViewModel @Inject constructor(
    val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _navigateToLoginSuccessful = MutableLiveData<Event<Boolean>>()
    val navigateToLoginSuccessful: LiveData<Event<Boolean>>
        get() = _navigateToLoginSuccessful

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    init {
        viewModelScope.launch {
            sendEmailVerificationUseCase()
            Log.d(TAG, "verification mail sent")
        }
        viewModelScope.launch {

            verifyEmailUseCase()
                .catch {
                    Log.i(TAG, "Verification error: ${it.message}")
                }
                .collect { verification ->
                    if(verification){
                        _showContinueButton.value = Event(verification)
                    }
                }

        }
    }

    fun onGoToDetailSelected() {
        _navigateToLoginSuccessful.value = Event(true)
    }
}