package com.matiasmandelbaum.alejandriaapp.domain.model.userinput

data class UserDataInput(
    val name: String,
    val lastName: String,
    val email: String? = null,
    val birthDate: String
) {
    fun isNotEmpty() =
        name.isNotEmpty() && lastName.isNotEmpty()
                && birthDate.isNotEmpty()
}