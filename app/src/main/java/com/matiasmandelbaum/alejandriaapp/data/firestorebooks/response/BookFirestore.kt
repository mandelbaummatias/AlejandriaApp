package com.matiasmandelbaum.alejandriaapp.data.firestorebooks.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookFirestore(
    val autor: String = "",
    val titulo: String = "",
    val cantidad: Int = 0,
    val cantidad_disponible: Int = 0,
    val isbn_13: String = ""
) : Parcelable //Creating a no-argument constructor indirectly for Firestore.