package com.matiasmandelbaum.alejandriaapp.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "SignUpViewModel"
@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {

    private val _onDatePickerClicked = MutableLiveData<Unit>()
    val onDatePickerClicked : LiveData<Unit> = _onDatePickerClicked

    init{
        Log.d(TAG,"init!")
    }
    fun onSignInSelected(userSignIn: UserSignIn) {
        val viewState = userSignIn.toSignInViewState()
        if (viewState.userValidated() && userSignIn.isNotEmpty()) {
            signInUser(userSignIn)
        } else {
            onFieldsChanged(userSignIn)
        }
    }
    fun onDatePickerClicked() {
        Log.d(TAG, "onDatePickerClicked")
        _onDatePickerClicked.value = Unit
    }

}