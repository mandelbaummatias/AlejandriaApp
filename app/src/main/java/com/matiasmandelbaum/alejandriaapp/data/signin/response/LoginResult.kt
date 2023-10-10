package com.matiasmandelbaum.alejandriaapp.data.signin.response

sealed class LoginResult {
    object Error : LoginResult()
    data class Success(val verified: Boolean) : LoginResult()
}