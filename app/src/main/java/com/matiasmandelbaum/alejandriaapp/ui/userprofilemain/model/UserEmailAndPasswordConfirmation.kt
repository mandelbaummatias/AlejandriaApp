package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.model

data class UserEmailAndPasswordConfirmation(
    val email: String = "",
    val password: String = "",
    val showErrorDialog: Boolean = false
)
