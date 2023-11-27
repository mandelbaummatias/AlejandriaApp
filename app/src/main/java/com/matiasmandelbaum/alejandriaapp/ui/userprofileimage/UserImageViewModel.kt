package com.matiasmandelbaum.alejandriaapp.ui.userprofileimage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ChangeImageForUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserImageViewModel @Inject constructor(private val changeImageForUserUseCase: ChangeImageForUserUseCase) :
    ViewModel() {

    private val _changeResult = MutableLiveData<Result<Unit>>()
    val changeResult: LiveData<Result<Unit>> = _changeResult
    fun changeImageForUser(newImage: String) {
        viewModelScope.launch {
            val result = changeImageForUserUseCase(newImage)
            _changeResult.postValue(result)
        }
    }
}