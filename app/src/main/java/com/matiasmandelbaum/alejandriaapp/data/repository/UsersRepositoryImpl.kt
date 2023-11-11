package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.FirebaseConstants
import com.matiasmandelbaum.alejandriaapp.data.util.FirebaseConstants.USERS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.domain.model.user.User
import com.matiasmandelbaum.alejandriaapp.domain.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "UsersRepositoryImpl"

class UsersRepositoryImpl @Inject constructor(

    private val firestore: FirebaseFirestore,

    ) : UsersRepository {

    override suspend fun getUserByEmail(email: String): User {
        val allUsersResult = getAllUsers() // Obtiene todos los usuarios
        return when (val result = allUsersResult) {
            is Result.Success -> {
                val users = result.data
                users.firstOrNull { it.email == email } ?: throw NoSuchElementException("Usuario no encontrado")
            }
            is Result.Error -> throw IllegalStateException("ERROR: No se pudo recuperar la lista de usuarios")
            else -> {throw IllegalStateException("Tipo de dato obtenido desconocido")}
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val usersList = mutableListOf<User>()

            val querySnapshot = withContext(Dispatchers.IO) {
                firestore.collection(FirebaseConstants.USERS_COLLECTION)
                    .orderBy("apellido")
                    .get()
                    .await()
            }

            val users = querySnapshot.toObjects(User::class.java)
            usersList.addAll(users)

            Result.Success(usersList)
        } catch (e: Exception) {
            Result.Error(e.message!!)
        }
    }

    override suspend fun updateUserReservationState(userEmail: String): Result<Unit> = suspendCoroutine { continuation ->
        val usersCollection = firestore.collection(USERS_COLLECTION)

        usersCollection
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { userDocuments ->
                if (userDocuments.size() > 0) {
                    val userDocument = userDocuments.documents[0]
                    val userReference = userDocument.reference
                    userReference.update("reservo_libro", true)
                    continuation.resume(Result.Success(Unit))
                } else {
                    Log.d(TAG, "User not found")
                    continuation.resume(Result.Error("User not found"))
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "User query or update failed $e")
                continuation.resume(Result.Error("User query or update failed: ${e.message}"))
            }
    }


}
