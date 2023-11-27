package com.matiasmandelbaum.alejandriaapp.common.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val _authStateLiveData = MutableLiveData<FirebaseUser?>()
    val authStateLiveData: LiveData<FirebaseUser?> = _authStateLiveData

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authStateLiveData.value = firebaseAuth.currentUser
        }
    }

    fun signOut() {
        auth.signOut()
        _authStateLiveData.postValue(null)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}








