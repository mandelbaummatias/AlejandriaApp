package com.matiasmandelbaum.alejandriaapp.domain.model.userprofile

data class UserProfile(
    val name: String = "",
    val lastName: String= "",
    val email: String = "",
    val image: String? = null,
    val birthDate: String = ""
)