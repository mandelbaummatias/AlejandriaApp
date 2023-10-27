package com.matiasmandelbaum.alejandriaapp.common.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


object AuthManager {
    private val auth = FirebaseAuth.getInstance()
   // private val auth = FirebaseAuth.getInstance()
    private val authStateLiveData = MutableLiveData<FirebaseUser?>()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            authStateLiveData.value = firebaseAuth.currentUser
        }
    }

    fun getAuthStateLiveData(): LiveData<FirebaseUser?> {
        return authStateLiveData
    }
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








