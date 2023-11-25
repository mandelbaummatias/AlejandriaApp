package com.matiasmandelbaum.alejandriaapp.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import javax.inject.Inject

private const val TAG = "GetUserBySubscriptionIdUseCase"

