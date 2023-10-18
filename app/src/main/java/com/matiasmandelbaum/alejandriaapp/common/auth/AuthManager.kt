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



//class AuthManager {
//    private val auth = FirebaseAuth.getInstance()
//
//    // Create a singleton for the AuthManager
//    companion object {
//        val instance: AuthManager by lazy { AuthManager() }
//    }
//
//    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
//        auth.addAuthStateListener(listener)
//    }
//
//    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
//        auth.removeAuthStateListener(listener)
//    }
//
//    fun getCurrentUser(): FirebaseUser? {
//        return auth.currentUser
//    }
//
//    fun signOut(){
//        auth.signOut()
//    }
//}







