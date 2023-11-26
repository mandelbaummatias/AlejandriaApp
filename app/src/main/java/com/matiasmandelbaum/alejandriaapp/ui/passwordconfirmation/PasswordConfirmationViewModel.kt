package com.matiasmandelbaum.alejandriaapp.ui.passwordconfirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ChangeUserEmailInReservesUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ChangeUserEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordConfirmationViewModel @Inject constructor(
    private val changeUserEmailUseCase: ChangeUserEmailUseCase,
    private val changeUserEmailInReservesUseCase: ChangeUserEmailInReservesUseCase
) :
    ViewModel() {
    private val _onChangeUserEmailSuccess = MutableLiveData<Result<Unit>>()
    val onChangeUserEmailSuccess: LiveData<Result<Unit>> = _onChangeUserEmailSuccess

    fun changeUserEmail(newEmail: String, previousEmail: String, pass: String) {
        _onChangeUserEmailSuccess.value = Result.Loading
        viewModelScope.launch {
            val result = changeUserEmailUseCase(newEmail, previousEmail, pass)
            if (result is Result.Success) {
                changeUserEmailInReservesUseCase(newEmail, previousEmail)
            }
            _onChangeUserEmailSuccess.postValue(result)
        }
    }
}