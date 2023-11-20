package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matiasmandelbaum.alejandriaapp.common.event.Event
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ChangeUserProfileUseCase
import com.matiasmandelbaum.alejandriaapp.domain.usecase.GetUserByEmailUseCase
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.UserEmailViewState
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.UserProfileViewState
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.model.UserProfile2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UserProfileViewModel"

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val changeUserProfileUseCase: ChangeUserProfileUseCase
) :
    ViewModel() {

    private val _userProfile = MutableLiveData<Result<UserProfile>?>()
    val userProfile: MutableLiveData<Result<UserProfile>?> get() = _userProfile

    private companion object {
        const val MIN_NAME_LENGTH = 2
        const val MIN_LAST_NAME_LENGTH = 2
    }

    fun getUserByEmail(email: String) {
        _userProfile.value = Result.Loading
        viewModelScope.launch {
            _userProfile.value = getUserByEmailUseCase(email)
        }
    }

    private val _showPasswordRequiredDialog = MutableLiveData<Event<Boolean>>()
    val showPasswordRequiredDialog: LiveData<Event<Boolean>>
        get() = _showPasswordRequiredDialog

    private val _showOnSuccessfulSavedDataMessage = MutableLiveData<Event<Boolean>>()
    val showOnSuccessfulSavedDataMessage: LiveData<Event<Boolean>>
        get() = _showOnSuccessfulSavedDataMessage

    private val _viewState = MutableStateFlow(UserProfileViewState())
    val viewState: StateFlow<UserProfileViewState>
        get() = _viewState

    private val _emailViewState = MutableStateFlow(UserEmailViewState())
    val emailViewState: StateFlow<UserEmailViewState>
        get() = _emailViewState

    private var _showErrorDialog = MutableLiveData(UserProfile2())
    val showErrorDialog: LiveData<UserProfile2>
        get() = _showErrorDialog

    fun onSaveProfileSelected(name: String, lastName: String, email: String) {
        Log.d(TAG, " onSaveProfileSelected")
        if (isValidName(name) && isValidLastName(lastName)) {
            saveUserProfile(name, lastName, email)
        } else {
            onFieldsChanged(name, lastName)
        }
    }

    fun onSaveUserEmailSelected(email: String, previousEmail: String) {
        Log.d(TAG, " onSaveUserEmailSelected")
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
                Log.d(TAG, "son diferentes: $email y $previousEmail")
                _showPasswordRequiredDialog.value = Event(true)
            } else {
                Log.d(TAG, "no son dif los emails")
                //dismiss?
            }
        }
    }

    private fun saveUserProfile(name: String, lastName: String, email: String) {
        Log.d(TAG, "email? $email")
        viewModelScope.launch {
            _viewState.value = UserProfileViewState(isLoading = true)
            when (changeUserProfileUseCase(name, lastName, email)) {
                is Result.Error -> {
                    Log.d(TAG, "Chang Error")
                    _showErrorDialog.value =
                        UserProfile2(email = name, password = lastName, showErrorDialog = true)
                    _viewState.value = UserProfileViewState(isLoading = false)
                }

                is Result.Success -> {
                    _showOnSuccessfulSavedDataMessage.value = Event(true)
                    Log.d(TAG, "Change ok")
                }

                else -> {
                    Unit
                }
            }
            _viewState.value = UserProfileViewState(isLoading = false)
        }
    }

    fun onFieldsChanged(name: String, lastName: String) {
        _viewState.value = UserProfileViewState(
            isValidName = isValidName(name),
            isValidLastName = isValidLastName(lastName)
        )
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
}


