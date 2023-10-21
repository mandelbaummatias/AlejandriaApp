package com.matiasmandelbaum.alejandriaapp.data.signin.remote

import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn) = runCatching {

        val user = hashMapOf(
            "apellido" to userSignIn.lastName,
            "email" to userSignIn.email,
            "fecha_nacimiento" to userSignIn.birthDate,
            "nombre" to userSignIn.name,
          //  "test" to "test"
        )

        firebase.db
            .collection(USER_COLLECTION)
            .add(user).await()

    }.isSuccess
}