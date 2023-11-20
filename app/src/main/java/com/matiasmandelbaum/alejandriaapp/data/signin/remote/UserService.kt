package com.matiasmandelbaum.alejandriaapp.data.signin.remote

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.DATE_OF_BIRTH
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.HAS_RESERVED_BOOK
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.LAST_NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.SUBSCRIPTION_ID
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
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

    suspend fun getUserBySubscriptionId(subscriptionId: String, userId: String): Boolean {
        val userRef = Firebase.firestore.collection("users").document(userId)

        try {
            val snapshot = userRef.get().await()

            if (snapshot.exists()) {
                val userData = snapshot.data
                if (userData != null) {
                    if (userData[SUBSCRIPTION_ID] == subscriptionId) {
                        // User has the desired subscription
                        Log.d(TAG, "user tiene subs")
                        // You can add code for any success handling here
                        // For example, you can access other user properties like userData["nombre"], userData["email"], etc.
                    } else {
                        Log.d(TAG, "user no tiene esa subs")
                        // User does not have the desired subscription
                        // You can add code for handling this case
                    }
                } else {
                    Log.d(TAG, "User data is null")
                    // Handle the case where userData is null
                    // You can add code to handle this case
                }
            } else {
                Log.d(TAG, "User document does not exist")
                // Handle the case where the user document does not exist
                // You can add code to handle this case
            }

        } catch (e: Exception) {
            Log.d(TAG, "Failure on getting user data: ${e.message}")
            // Handle the error
            // You can add code to handle the error here
        }
        return true
    }

    suspend fun getUserById(userId: String): Result<User> {
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
                    Result.Success(User())
                } else {
                    // User has the desired subscription
                    Log.d(TAG, "User has subscription $suscripcionMpId")
                    val isEnabled = userData[HAS_RESERVED_BOOK] as? Boolean
                    Result.Success(User(suscripcionMpId, isEnabled))
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
                    continuation.resume(Result.Error("Reauthentication failed"))
                }
            }
    }
}