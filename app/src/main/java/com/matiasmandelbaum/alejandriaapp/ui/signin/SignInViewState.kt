package com.matiasmandelbaum.alejandriaapp.ui.signin

data class SignInViewState(
    val isLoading: Boolean = false,
    val isValidEmail: Boolean = true,
    val isValidDate: Boolean = true,
    val isValidPassword: Boolean = true,
    val isValidName: Boolean = true,
    val isValidLastName: Boolean = true
){
    fun userValidated() = isValidEmail && isValidPassword && isValidName && isValidLastName && isValidDate
}