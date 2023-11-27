package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.event.Event
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.DAY_MONTH_YEAR
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.YEAR_MONTH_DAY
import com.matiasmandelbaum.alejandriaapp.domain.model.userinput.UserDataInput
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ChangeUserProfileUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByEmailUseCase
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.model.UserEmailAndPasswordConfirmation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val changeUserProfileUseCase: ChangeUserProfileUseCase
) :
    ViewModel() {

    private val _userProfile = MutableLiveData<Result<UserProfile>?>()
    val userProfile: MutableLiveData<Result<UserProfile>?> = _userProfile

    private companion object {
        const val MIN_NAME_LENGTH = 2
        const val MIN_LAST_NAME_LENGTH = 2
        const val MIN_DATE_LENGTH = 10
    }

    fun getUserByEmail(email: String) {
        _userProfile.value = Result.Loading
        viewModelScope.launch {
            _userProfile.value = getUserByEmailUseCase(email)
        }
    }

    private val _showPasswordRequiredDialog = MutableLiveData<Event<Boolean>>()
    val showPasswordRequiredDialog: LiveData<Event<Boolean>>
         = _showPasswordRequiredDialog

    private val _showOnSuccessfulSavedDataMessage = MutableLiveData<Event<Boolean>>()
    val showOnSuccessfulSavedDataMessage: LiveData<Event<Boolean>> =
        _showOnSuccessfulSavedDataMessage

    private val _viewState = MutableStateFlow(UserProfileViewState())
    val viewState: StateFlow<UserProfileViewState> = _viewState

    private val _emailViewState = MutableStateFlow(UserEmailViewState())
    val emailViewState: StateFlow<UserEmailViewState>
         = _emailViewState

    private var _showErrorDialog = MutableLiveData(UserEmailAndPasswordConfirmation())
    val showErrorDialog: LiveData<UserEmailAndPasswordConfirmation>
         = _showErrorDialog

    fun onSaveProfileSelected(userDataInput: UserDataInput): Boolean {
        val viewState = userDataInput.toUserProfileViewState()
        return if (viewState.userValidated() && userDataInput.isNotEmpty()) {
            saveUserProfile(userDataInput)
            true
        } else {
            onFieldsChanged(userDataInput)
            false
        }
    }

    fun onSaveUserEmailSelected(email: String, previousEmail: String) {
        if (isValidOrEmptyEmail(email)) {
            saveUserEmail(email, previousEmail)
        } else {
            onEmailChanged(email)
        }
    }

    private fun saveUserEmail(email: String, previousEmail: String) {
        viewModelScope.launch {
            _emailViewState.value = UserEmailViewState()
            if (email != previousEmail) {
                _showPasswordRequiredDialog.value = Event(true)
            }
        }
    }

    private fun saveUserProfile(userDataInput: UserDataInput) {
        viewModelScope.launch {
            _viewState.value = UserProfileViewState(isLoading = true)
            when (changeUserProfileUseCase(userDataInput)) {
                is Result.Error -> {
                    _showErrorDialog.value =
                        UserEmailAndPasswordConfirmation(
                            email = userDataInput.name,
                            password = userDataInput.lastName,
                            showErrorDialog = true
                        )
                    _viewState.value = UserProfileViewState(isLoading = false)
                }

                is Result.Success -> {
                    _showOnSuccessfulSavedDataMessage.value = Event(true)
                }

                else -> {
                    Unit
                }
            }
            _viewState.value = UserProfileViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(userDataInput: UserDataInput) {
        _viewState.value = userDataInput.toUserProfileViewState()
    }


    fun onEmailChanged(email: String) {
        _emailViewState.value = UserEmailViewState(
            isValidEmail = isValidOrEmptyEmail(email)
        )
    }

    private fun isValidName(name: String) =
        name.length >= MIN_NAME_LENGTH || name.isEmpty()

    private fun isValidLastName(lastName: String): Boolean =
        lastName.length >= MIN_LAST_NAME_LENGTH || lastName.isEmpty()

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidDate(date: String): Boolean {
        return isUserAtLeast18YearsOld(date) && date.length >= MIN_DATE_LENGTH || date.isEmpty() //isValidDate(date)||
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
            } catch (_: DateTimeParseException) {
            }
        }
        return false
    }

    private fun UserDataInput.toUserProfileViewState(): UserProfileViewState {
        return UserProfileViewState(
            isValidName = isValidName(name),
            isValidLastName = isValidLastName(lastName),
            isValidBirthDate = isValidDate(birthDate)
        )
    }
}