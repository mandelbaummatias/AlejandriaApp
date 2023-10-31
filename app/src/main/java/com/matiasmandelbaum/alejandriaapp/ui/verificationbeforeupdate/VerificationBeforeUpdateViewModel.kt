package com.matiasmandelbaum.alejandriaapp.ui.verificationbeforeupdate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.event.Event
import com.matiasmandelbaum.alejandriaapp.domain.usecase.VerifyEmailBeforeUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = " VerificationBeforeUpdateViewModel"

@HiltViewModel
class VerificationBeforeUpdateViewModel @Inject constructor(
    val verifyEmailBeforeUpdate: VerifyEmailBeforeUpdateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val newEmail: String = savedStateHandle["newEmail"]!!

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    private val _navigateToLoginSuccessful = MutableLiveData<Event<Boolean>>()
    val navigateToLoginSuccessful: LiveData<Event<Boolean>>
        get() = _navigateToLoginSuccessful


    private val _navigateToVerifyEmail = MutableLiveData<Event<Boolean>>()
    val navigateToVerifyEmail: LiveData<Event<Boolean>>
        get() = _navigateToVerifyEmail

    init {
        viewModelScope.launch {
            verifyEmailBeforeUpdate(newEmail)
                .catch {
                    Log.i(TAG, "Verification error: ${it.message}")
                }
                .collect { verification ->
                    if (verification) {
                        _showContinueButton.value = Event(verification)
                    }
                }

        }
    }



    fun onGoToDetailSelected() {
        _navigateToLoginSuccessful.value = Event(true)
    }


}