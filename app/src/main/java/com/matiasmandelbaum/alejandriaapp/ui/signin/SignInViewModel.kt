package com.matiasmandelbaum.alejandriaapp.ui.signin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.Event
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
    }

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToHome = MutableLiveData<Event<Boolean>>()
    val navigateToHome: LiveData<Event<Boolean>>
        get() = _navigateToHome

    private val _viewState = MutableStateFlow(SignInViewState())
    val viewState: StateFlow<SignInViewState>
        get() = _viewState

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
                Log.d(TAG, "show error: $showErrorDialog")
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
        Log.d(TAG, " mi name al principio $name")
        return name.length >= MIN_PASSWORD_LENGTH || name.isEmpty()
    }


     fun isValidDate(date: String): Boolean {
        Log.d(TAG, "mi date al principio isValid -> $date")
     //   return date.isNotEmpty()

        return date.length >= 10 || date.isEmpty() //isValidDate(date)||
    }

//     fun isValidDate(date: String?): Boolean {
//        Log.d(TAG, "mi date al principio isValid -> $date")
//        if (date != null) {
//            if (date.isEmpty()) {
//                Log.d(TAG, "date es empty todavia")
//                return true
//            }
//        } else {
//            Log.d(TAG, "date es null. return false")
//            return false
//        }
//
//
//
//        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//
//        try {
//            Log.d(TAG, "mi string before parse $date")
//            val parsedDate = LocalDate.parse(date, formatter)
//            // Check if the parsed date matches the original date string
//            val formattedDate = parsedDate.format(formatter)
//            Log.d(TAG, "formatted date $formattedDate")
//            return date == formattedDate
//        } catch (e: DateTimeParseException) {
//            Log.d(TAG, "error en parse $e")
//            return false
//        }
//    }
    private fun isUserAtLeast18YearsOld(date: String): Boolean {
        Log.d(TAG, "isUserAtLeast...(date: $date)")
        val dateFormats = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"), // Add more formats as needed
            // Add additional date formats here
        )

        for (dateFormat in dateFormats) {
            try {
                val parsedDate = LocalDate.parse(date, dateFormat)
                val today = LocalDate.now()
                val age = ChronoUnit.YEARS.between(parsedDate, today)
                return age >= 18
            } catch (e: DateTimeParseException) {
                // Continue to the next format if parsing fails
            }
        }

        Log.d(TAG, "Invalid date format: $date")
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