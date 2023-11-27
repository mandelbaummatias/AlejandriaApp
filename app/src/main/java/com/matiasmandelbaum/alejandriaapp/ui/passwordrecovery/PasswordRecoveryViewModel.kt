package com.matiasmandelbaum.alejandriaapp.ui.passwordrecovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.usecase.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase) :
    ViewModel() {
    private val _onPasswordResetEmailSent = MutableLiveData<Result<Unit>>()
    val onPasswordResetEmailSent: MutableLiveData<Result<Unit>> = _onPasswordResetEmailSent


    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val result = sendPasswordResetEmailUseCase(email)
            _onPasswordResetEmailSent.postValue(result)
        }
    }
}