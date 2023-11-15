package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.HAS_RESERVED_BOOK
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.LAST_NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.USERS_COLLECTION
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
    private val userService: UserService
) : UsersRepository {

    override suspend fun addSubscriptionId(subscriptionId: String, userId: String): Boolean {
        return try {
            userService.addSubscriptionId(subscriptionId, userId)
            true
        } catch (e: Exception) {
            Log.d(TAG, "failure on update MP ${e.message}")
            false
        }
    }

    override suspend fun updateUserReservationState(userEmail: String): Result<Unit> =
        suspendCoroutine { continuation ->
            val usersCollection = firestore.collection(USERS_COLLECTION)

            usersCollection
                .whereEqualTo(EMAIL, userEmail)
                .get()
                .addOnSuccessListener { userDocuments ->
                    if (userDocuments.size() > 0) {
                        val userDocument = userDocuments.documents[0]
                        val userReference = userDocument.reference
                        userReference.update(HAS_RESERVED_BOOK, true)
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
    override suspend fun getUserById(userId: String): Result<com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User> {
        return userService.getUserById(userId)
    }

    override suspend fun getUserByEmail(email: String): User {
        val allUsersResult = getAllUsers() // Obtiene todos los usuarios
        return when (val result = allUsersResult) {
            is Result.Success -> {
                val users = result.data
                users.firstOrNull { it.email == email }
                    ?: throw NoSuchElementException("Usuario no encontrado")
            }

            is Result.Error -> throw IllegalStateException("ERROR: No se pudo recuperar la lista de usuarios")
            else -> {
                throw IllegalStateException("Tipo de dato obtenido desconocido")
            }
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val usersList = mutableListOf<User>()

            val querySnapshot = withContext(Dispatchers.IO) {
                firestore.collection(USERS_COLLECTION)
                    .orderBy(LAST_NAME)
                    .get()
                    .await()
            }

            val users = querySnapshot.toObjects(User::class.java)
            usersList.addAll(users)

            Result.Success(usersList)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }




}
