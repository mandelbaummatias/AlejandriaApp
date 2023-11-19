package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import com.google.firebase.firestore.DocumentReference

data class User(
    val name: String = "",
    val lastName: String= "",
    val email: String = "",
    val image: String? = null,
    val documentReference: DocumentReference
    // add other properties as needed
)