package com.matiasmandelbaum.alejandriaapp.ui.userprofilemail

data class UserProfileViewState(
    val isLoading: Boolean = false,
    val isValidName: Boolean = true,
    val isValidLastName: Boolean = true,
    val isValidBirthDate: Boolean = true
) {
    fun userValidated() =
        isValidName && isValidLastName && isValidBirthDate
}