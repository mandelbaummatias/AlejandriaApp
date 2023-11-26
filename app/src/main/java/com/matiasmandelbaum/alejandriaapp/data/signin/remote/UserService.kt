package com.matiasmandelbaum.alejandriaapp.data.signin.remote

import android.util.Log
import androidx.navigation.NavOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.DATE_OF_BIRTH
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.HAS_RESERVED_BOOK
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.LAST_NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.SUBSCRIPTION_ID
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.USERS_COLLECTION
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.SubscriptionUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "UserService"

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn, uid: String) = runCatching {

        val user = hashMapOf(
            LAST_NAME to userSignIn.lastName,
            EMAIL to userSignIn.email,
            DATE_OF_BIRTH to userSignIn.birthDate,
            NAME to userSignIn.name,
            HAS_RESERVED_BOOK to false
        )

        firebase.db
            .collection(USER_COLLECTION)
            .document(uid) // Specify the custom document ID here
            .set(user)
            .await()
    }.isSuccess


    suspend fun addSubscriptionId(subscriptionId: String, userId: String) {
        val userToUpdate = firebase.db.collection(USER_COLLECTION).document(userId)

        val updates = hashMapOf(
            SUBSCRIPTION_ID to subscriptionId,
        )

        try {
            userToUpdate.update(updates as Map<String, Any>).await()
            Log.d(TAG, "update MP ok")
        } catch (e: Exception) {
            Log.d(TAG, "failure on update MP ${e.message}")
            throw e
        }
    }

    suspend fun getUserById(userId: String): Result<SubscriptionUser> {
        val userRef = Firebase.firestore.collection(USER_COLLECTION).document(userId)

        return try {
            val snapshot = userRef.get().await()

            if (snapshot.exists()) {
                val userData = snapshot.data
                val suscripcionMpId = userData?.get(SUBSCRIPTION_ID) as? String

                return if (suscripcionMpId.isNullOrEmpty()) {
                    // User does not have the desired subscription or it is empty
                    Log.d(TAG, "User does not have the desired subscription or it is empty")
                    // You can add code for handling this case
                    Result.Success(SubscriptionUser())
                } else {
                    // User has the desired subscription
                    Log.d(TAG, "User has subscription $suscripcionMpId")
                    val isEnabled = userData[HAS_RESERVED_BOOK] as? Boolean
                    Result.Success(SubscriptionUser(suscripcionMpId, isEnabled))
                    // You can add code for any success handling here
                    // For example, you can access other user properties like userData["nombre"], userData["email"], etc.
                }
            } else {
                // Handle the case where the user doesn't exist
                Result.Error("User not found")
            }

        } catch (e: Exception) {
            Log.d(TAG, "Failure in getting user data: ${e.message}")
            Result.Error(e.message ?: "Unknown error")
            // Handle the error
            // You can add code to handle the error here
        }
    }


    suspend fun updateUserEmail(
        newEmail: String,
        previousEmail: String,
        pass: String
    ): Result<Unit> = suspendCoroutine { continuation ->
        val user = firebase.auth.currentUser
        val usersCollection = firebase.db.collection(UsersConstants.USERS_COLLECTION)
        val credential = EmailAuthProvider.getCredential(user?.email!!, pass)

        Log.d(TAG, "CREDENCIALES $newEmail, $previousEmail , $pass")

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "reauthenticate is successful")

                    user.updateEmail(newEmail).addOnCompleteListener { updateEmailTask ->
                        if (updateEmailTask.isSuccessful) {
                            usersCollection
                                .whereEqualTo(EMAIL, previousEmail)
                                .get()
                                .addOnCompleteListener { userDocumentsTask ->
                                    if (userDocumentsTask.isSuccessful) {
                                        val userDocuments = userDocumentsTask.result
                                        if ((userDocuments?.size() ?: 0) > 0) {
                                            val userDocument = userDocuments!!.documents[0]
                                            val userReference = userDocument.reference
                                            userReference.update(
                                                mapOf(
                                                    EMAIL to newEmail
                                                )
                                            ).addOnCompleteListener { updateDocumentTask ->
                                                if (updateDocumentTask.isSuccessful) {
                                                    Log.d(TAG, "Email updated successfully")
                                                    continuation.resume(Result.Success(Unit))
                                                } else {
                                                    Log.d(TAG, "Error updating user document")
                                                    continuation.resume(Result.Error("Error updating user document"))
                                                }
                                            }
                                        } else {
                                            Log.d(TAG, "User not found")
                                            continuation.resume(Result.Error("User not found"))
                                        }
                                    } else {
                                        Log.d(
                                            TAG,
                                            "User query or update failed",
                                            userDocumentsTask.exception
                                        )
                                        continuation.resume(Result.Error("User query or update failed"))
                                    }
                                }
                        } else {
                            Log.d(TAG, "Update email failed", updateEmailTask.exception)
                            continuation.resume(Result.Error("Update email failed"))
                        }
                    }
                } else {

                    Log.d(TAG, "Reauthentication failed")
                    Log.d(TAG, "veo credenciales fallidas $newEmail, $previousEmail, $pass")
                    continuation.resume(Result.Error("Reauthentication failed"))
                }
            }
    }

    suspend fun changeImageForUser(newImage: String): Result<Unit> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (firebase.auth.currentUser != null) {
                val userCollection = firebase.db.collection(USER_COLLECTION)
                if (currentUser != null) {
                    userCollection.whereEqualTo("email", currentUser.email)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val userDocument = querySnapshot.documents[0].reference
                            userDocument.update("image", newImage)
                                .addOnSuccessListener {
                                    Log.d(TAG, "ok")
                                    // showImageUpdatedMessage()
                                    val navOptions = NavOptions.Builder()
                                        .setPopUpTo(R.id.userProfileFragment, true)
                                        .build()
                                    // findNavController().navigate(R.id.userProfileFragment, null, navOptions)
                                    continuation.resume(Result.Success(Unit))
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "Image update failed", exception)
                                    continuation.resume(Result.Error("Image update failed"))
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "User query failed", exception)
                            continuation.resume(Result.Error("User query failed"))
                        }
                }
            } else {
                continuation.resume(Result.Error("User not authenticated"))
            }
        }

    suspend fun getUserByEmail(email: String): Result<UserProfile> =
        suspendCoroutine { continuation ->
            firebase.db.collection(USER_COLLECTION)
                .whereEqualTo(EMAIL, email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]

                        val nombre = document.getString(NAME)
                        val apellido = document.getString(LAST_NAME)
                        val image = document.getString(UsersConstants.IMAGE)
                        val date = document.getString(DATE_OF_BIRTH)
                        val userProfile =
                            UserProfile(
                                nombre!!,
                                apellido!!,
                                email,
                                image,
                                date!!
                            )
                        continuation.resume(Result.Success(userProfile))
                    } else {
                        continuation.resume(Result.Error("User not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.Error("Error querying Firestore: ${exception.message}"))
                }
        }

    suspend fun updateUserProfile(
        name: String,
        lastName: String,
        userEmail: String,
        birthDate: String
    ): Result<Unit> =
        suspendCoroutine { continuation ->
            val usersCollection = firebase.db.collection(USERS_COLLECTION)

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
                        continuation.resume(Result.Error("User not found"))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.Error("User query or update failed: ${e.message}"))
                }
        }

    suspend fun updateUserReservationState(userEmail: String): Result<Unit> =
        suspendCoroutine { continuation ->
            val usersCollection = firebase.db.collection(USERS_COLLECTION)

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
                        continuation.resume(Result.Error("Usuario no encontrado"))
                    }
                }
                .addOnFailureListener {
                    continuation.resume(Result.Error("Error en actualizar usuario"))
                }
        }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        suspendCoroutine { continuation ->
            try {
                if (email.isNotEmpty()) {
                    firebase.auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(Result.Success(Unit))
                            } else {
                                continuation.resume(Result.Error("Error on sending password reset email"))
                            }
                        }
                } else {
                    continuation.resume(Result.Error("Email is empty"))
                }
            } catch (e: Exception) {
                continuation.resume(Result.Error("Exception: $e"))
            }
        }
}