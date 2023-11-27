package com.matiasmandelbaum.alejandriaapp.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.event.Event
import com.matiasmandelbaum.alejandriaapp.data.signin.response.LoginResult
import com.matiasmandelbaum.alejandriaapp.domain.usecase.LoginUseCase
import com.matiasmandelbaum.alejandriaapp.ui.login.model.UserLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val loginUseCase: LoginUseCase) : ViewModel() {

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome

    private val _navigateToSignIn = MutableLiveData<Event<Boolean>>()
    val navigateToSignIn: LiveData<Event<Boolean>> = _navigateToSignIn

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState> = _viewState

    private var _showErrorDialog = MutableLiveData(UserLogin())
    val showErrorDialog: LiveData<UserLogin> = _showErrorDialog

    fun onLoginSelected(email: String, password: String) {
        if (isValidEmail(email) && isValidPassword(password)) {
            loginUser(email, password)
        } else {
            onFieldsChanged(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _viewState.value = LoginViewState()
            when (loginUseCase(email, password)) {
                LoginResult.Error -> {
                    _showErrorDialog.value =
                        UserLogin(email = email, password = password, showErrorDialog = true)
                    _viewState.value = LoginViewState()
                }

                is LoginResult.Success -> {
                    _navigateToHome.value = Event(true)
                }
            }
            _viewState.value = LoginViewState()
        }
    }

    fun onFieldsChanged(email: String, password: String) {
        _viewState.value = LoginViewState(
            isValidEmail = isValidEmail(email),
            isValidPassword = isValidPassword(password)
        )
    }

    fun onSignInSelected() {
        _navigateToSignIn.value = Event(true)
    }

    private fun isValidEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH || password.isEmpty()

}