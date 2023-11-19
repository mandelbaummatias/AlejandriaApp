package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(private val getUserByEmailUseCase: GetUserByEmailUseCase) :
    ViewModel() {

    private val _user = MutableLiveData<Result<User>?>()
    val user: MutableLiveData<Result<User>?> get() = _user

    fun getUserByEmail(email: String) {
        _user.value = Result.Loading
        viewModelScope.launch {
            _user.value = getUserByEmailUseCase(email)
        }
    }


}


