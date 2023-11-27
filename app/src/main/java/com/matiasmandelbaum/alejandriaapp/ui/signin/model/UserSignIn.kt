package com.matiasmandelbaum.alejandriaapp.ui.signin.model

data class UserSignIn(
    val name: String,
    val lastName: String,
    val email: String,
    val birthDate:String,
    val password: String,
    val passwordConfirmation: String
) {
    fun isNotEmpty() =
        name.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
             && password.isNotEmpty() && passwordConfirmation.isNotEmpty()  && birthDate.isNotEmpty()
}