package com.matiasmandelbaum.alejandriaapp.ui.signin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.event.Event
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.DAY_MONTH_YEAR
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.YEAR_MONTH_DAY
import com.matiasmandelbaum.alejandriaapp.domain.usecase.CreateAccountUseCase
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private const val TAG = "SignUpViewModel"

@HiltViewModel
class SignInViewModel @Inject constructor(val createAccountUseCase: CreateAccountUseCase) :
    ViewModel() {

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
        const val MIN_NAME_LENGTH = 2
        const val MIN_DATE_LENGTH = 10
    }

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>> = _navigateToLogin

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>> = _navigateToHome

    private val _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState> = _viewState

    private var _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean> = _showErrorDialog
    fun onSignInSelected(userSignIn: UserSignIn) {
        val viewState = userSignIn.toSignInViewState()
        if (viewState.userValidated() && userSignIn.isNotEmpty()) {
            signInUser(userSignIn)
        } else {
            onFieldsChanged(userSignIn)
        }
    }

    private fun signInUser(userSignIn: UserSignIn) {
        viewModelScope.launch {
            _viewState.value = SignInViewState(isLoading = true)
            val accountCreated = createAccountUseCase(userSignIn)
            if (accountCreated) {
                _navigateToHome.value = Event(true)
            } else {
                _showErrorDialog.value = true
            }
            _viewState.value = SignInViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(userSignIn: UserSignIn) {
        _viewState.value = userSignIn.toSignInViewState()
    }


    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidOrEmptyPassword(password: String, passwordConfirmation: String): Boolean =

        (password.length >= MIN_PASSWORD_LENGTH && password == passwordConfirmation) || password.isEmpty() || passwordConfirmation.isEmpty()

    private fun isValidName(name: String): Boolean {
        return name.length >= MIN_NAME_LENGTH || name.isEmpty()
    }


    private fun isValidDate(date: String): Boolean {
        return isUserAtLeast18YearsOld(date) && date.length >= MIN_DATE_LENGTH || date.isEmpty()
    }

    private fun isUserAtLeast18YearsOld(date: String): Boolean {
        val dateFormats = listOf(
            DateTimeFormatter.ofPattern(YEAR_MONTH_DAY),
            DateTimeFormatter.ofPattern(DAY_MONTH_YEAR)
        )

        for (dateFormat in dateFormats) {
            try {
                val parsedDate = LocalDate.parse(date, dateFormat)
                val today = LocalDate.now()
                val age = ChronoUnit.YEARS.between(parsedDate, today)
                return age >= 18
            } catch (e: DateTimeParseException) {
                Log.d(TAG, "Exception in parsing date")
            }
        }
        return false
    }

    private fun UserSignIn.toSignInViewState(): SignInViewState {
        return SignInViewState(
            isValidEmail = isValidOrEmptyEmail(email),
            isValidDate = isValidDate(birthDate),
            isValidPassword = isValidOrEmptyPassword(password, passwordConfirmation),
            isValidLastName = isValidName(lastName),
            isValidName = isValidName(name)
        )
    }
}