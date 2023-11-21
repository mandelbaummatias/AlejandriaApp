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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

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

    //    fun onSaveProfileSelected(name: String, lastName: String, email: String, birthDate: String) {
//        Log.d(TAG, " onSaveProfileSelected")
//        if (isValidName(name) && isValidLastName(lastName) && isValidDate(birthDate)) {
//            // if (isValidName(name) && isValidLastName(lastName) && isValidOrEmptyDate(birthDate)) {
//            saveUserProfile(name, lastName, email, birthDate)
//        } else {
//            onFieldsChanged(name, lastName, birthDate)
//        }
//    }
//    fun onSaveProfileSelected(userProfile: com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile) {
//        Log.d(TAG, " onSaveProfileSelected")
//        val viewState = userProfile.toUserProfileViewState()
//        if (viewState.userValidated() && userProfile.isNotEmpty()) {
//            // if (isValidName(name) && isValidLastName(lastName) && isValidOrEmptyDate(birthDate)) {
//            saveUserProfile(userProfile)
//            //return true
//        } else {
//            onFieldsChanged(userProfile)
//            //return false
//        }
//    }

    fun onSaveProfileSelected(userProfile: com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile): Boolean {
        Log.d(TAG, "onSaveProfileSelected")
        val viewState = userProfile.toUserProfileViewState()
        return if (viewState.userValidated() && userProfile.isNotEmpty()) {
            saveUserProfile(userProfile)
            true
        } else {
            onFieldsChanged(userProfile)
            false
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

//    private fun saveUserProfile(name: String, lastName: String, email: String, birthDate: String) {
//        Log.d(TAG, "email? $email")
//        viewModelScope.launch {
//            _viewState.value = UserProfileViewState(isLoading = true)
//            when (changeUserProfileUseCase(name, lastName, email, birthDate)) {
//                is Result.Error -> {
//                    Log.d(TAG, "Chang Error")
//                    _showErrorDialog.value =
//                        UserProfile2(email = name, password = lastName, showErrorDialog = true)
//                    _viewState.value = UserProfileViewState(isLoading = false)
//                }
//
//                is Result.Success -> {
//                    _showOnSuccessfulSavedDataMessage.value = Event(true)
//                    Log.d(TAG, "Change ok")
//                }
//
//                else -> {
//                    Unit
//                }
//            }
//            _viewState.value = UserProfileViewState(isLoading = false)
//        }
//    }

    private fun saveUserProfile(userProfile: com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile) {
        viewModelScope.launch {
            _viewState.value = UserProfileViewState(isLoading = true)
            when (changeUserProfileUseCase(userProfile)) {
                is Result.Error -> {
                    Log.d(TAG, "Chang Error")
                    _showErrorDialog.value =
                        UserProfile2(
                            email = userProfile.name,
                            password = userProfile.lastName,
                            showErrorDialog = true
                        ) //pasarlo a true?
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


//    fun onFieldsChanged(name: String, lastName: String, birthDate: String) {
//        _viewState.value = UserProfileViewState(
//            isValidName = isValidName(name),
//            isValidLastName = isValidLastName(lastName),
//            isValidBirthDate = isDateFormatValid(birthDate)
//        )
//    }

    fun onFieldsChanged(userProfile: com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile) {
        _viewState.value = userProfile.toUserProfileViewState()
    }


    fun onEmailChanged(email: String) {
        _emailViewState.value = UserEmailViewState(
            isValidEmail = isValidOrEmptyEmail(email)
        )
    }


//    private fun isValidName(name: String) =
//        name.length >= MIN_NAME_LENGTH && !name.isNullOrBlank()


    private fun isValidName(name: String) =
        name.length >= MIN_NAME_LENGTH || name.isEmpty()

//    private fun isValidLastName(lastName: String): Boolean =
//        lastName.length >= MIN_LAST_NAME_LENGTH && !lastName.isNullOrBlank()

    private fun isValidLastName(lastName: String): Boolean =
        lastName.length >= MIN_LAST_NAME_LENGTH ||lastName.isEmpty()

//    private fun isValidOrEmptyEmail(email: String) =
//        Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isNullOrBlank()

    private fun isValidOrEmptyEmail(email: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()

    private fun isValidDate(date: String): Boolean {
        Log.d(TAG, "mi date al principio isValid -> $date")
        return isUserAtLeast18YearsOld(date) && date.length >= MIN_DATE_LENGTH || date.isEmpty() //isValidDate(date)||
    }

//    fun isValidDate(date: String): Boolean {
//        Log.d(TAG, "mi date al principio isValid -> $date")
//        //   return date.isNotEmpty()
//
//        return isUserAtLeast18YearsOld(date) && date.length >= MIN_DATE_LENGTH || date.isEmpty() //isValidDate(date)||
//    }

    private fun isValidOrEmptyDate(birthDate: String) =
        !birthDate.isNullOrBlank() && isDateFormatValid(birthDate) && isUserAtLeast18YearsOld(
            birthDate
        ) && birthDate.length >= MIN_DATE_LENGTH

    private fun isDateFormatValid(dateString: String): Boolean {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format)
        sdf.isLenient = true // Esto hace que la validación sea estricta

        return try {
            sdf.parse(dateString)
            true
        } catch (e: ParseException) {
            false
        }
    }

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


    private fun com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile.toUserProfileViewState(): UserProfileViewState {
        return UserProfileViewState(
            isValidName = isValidName(name),
            isValidLastName = isValidLastName(lastName),
            isValidBirthDate = isValidDate(birthDate)
        )
    }

}