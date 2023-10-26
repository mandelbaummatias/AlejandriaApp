package com.matiasmandelbaum.alejandriaapp.common.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


object AuthManager {
    private val auth = FirebaseAuth.getInstance()
        fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


}








