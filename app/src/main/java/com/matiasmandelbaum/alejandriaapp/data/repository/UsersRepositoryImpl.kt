package com.matiasmandelbaum.alejandriaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService.Companion.USER_COLLECTION
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.DATE_OF_BIRTH
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.HAS_RESERVED_BOOK
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.IMAGE
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.LAST_NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.USERS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.domain.model.user.User
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
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

    override suspend fun updateUserProfile(
        name: String,
        lastName: String,
        userEmail: String,
        birthDate: String
    ): Result<Unit> =
        suspendCoroutine { continuation ->
            val usersCollection = firestore.collection(USERS_COLLECTION)

            usersCollection
                .whereEqualTo(EMAIL, userEmail)
                .get()
                .addOnSuccessListener { userDocuments ->
                    if (userDocuments.size() > 0) {
                        val userDocument = userDocuments.documents[0]
                        val userReference = userDocument.reference
                        userReference.update(
                            mapOf(
                                NAME to name,
                                LAST_NAME to lastName,
                                DATE_OF_BIRTH to birthDate
                            )
                        ).addOnSuccessListener {
                            continuation.resume(Result.Success(Unit))
                        }
                            .addOnFailureListener {
                                continuation.resume(Result.Error(it.toString()))
                            }
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


    override suspend fun updateUserEmail(
        newEmail: String,
        previousEmail: String,
        pass: String
    ) = userService.updateUserEmail(newEmail, previousEmail, pass)


    override suspend fun getUserById(userId: String): Result<com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User> {
        return userService.getUserById(userId)
    }

    override suspend fun getUserByEmail(email: String): Result<UserProfile> =
        suspendCoroutine { continuation ->
            firestore.collection(USER_COLLECTION)
                .whereEqualTo(EMAIL, email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]

                        val nombre = document.getString(NAME)
                        val apellido = document.getString(LAST_NAME)
                        val image = document.getString(IMAGE)
                        val date = document.getString(DATE_OF_BIRTH)
                        val userProfile =
                            UserProfile(nombre!!, apellido!!, email, image, date!!, document.reference)

                        Log.d(TAG, "Nombre: $nombre, Apellido: $apellido, Email: $email, Fecha de nacim.: $date")

                        //   val user = document.toObject(com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.User::class.java())

                        Log.d(TAG, "mi usuaro desde UserRepo $userProfile")
                        continuation.resume(Result.Success(userProfile))
                    } else {
                        continuation.resume(Result.Error("User not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Error("Error querying Firestore: ${exception.message}"))
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
