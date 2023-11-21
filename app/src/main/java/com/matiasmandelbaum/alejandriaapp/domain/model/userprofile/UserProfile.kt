package com.matiasmandelbaum.alejandriaapp.domain.model.userprofile

import com.google.firebase.firestore.DocumentReference

data class UserProfile(
    val name: String = "",
    val lastName: String= "",
    val email: String = "",
    val image: String? = null,
    val birthDate: String = "",
    val documentReference: DocumentReference
    // add other properties as needed
)