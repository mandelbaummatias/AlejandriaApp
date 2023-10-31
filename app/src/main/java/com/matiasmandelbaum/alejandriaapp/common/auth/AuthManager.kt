package com.matiasmandelbaum.alejandriaapp.common.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private const val TAG = "AuthManager"
object AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val _authStateLiveData = MutableLiveData<FirebaseUser?>()
    val authStateLiveData : LiveData<FirebaseUser?> = _authStateLiveData

    init {
        Log.d(TAG, "init")
        auth.addAuthStateListener { firebaseAuth ->
            Log.d(TAG, "value changed ${firebaseAuth.currentUser}")
            _authStateLiveData.value = firebaseAuth.currentUser
        }
    }

        fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }

    fun signOut() {
        Log.d(TAG,"signOut")
        auth.signOut()
        _authStateLiveData.postValue(null)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}








