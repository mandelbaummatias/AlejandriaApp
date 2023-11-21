package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

data class UserProfile(
    val name: String,
    val lastName: String,
    val email: String? = null,
    val birthDate: String
) {
    fun isNotEmpty() =
        name.isNotEmpty() && lastName.isNotEmpty()
                && birthDate.isNotEmpty()
}