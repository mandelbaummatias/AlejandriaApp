package com.matiasmandelbaum.alejandriaapp.data.signin.remote

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.matiasmandelbaum.alejandriaapp.common.result.Result

private const val TAG = "UserService"

class UserService @Inject constructor(private val firebase: FirebaseClient) {

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun createUserTable(userSignIn: UserSignIn, uid: String) = runCatching {

        val user = hashMapOf(
            "apellido" to userSignIn.lastName,
            "email" to userSignIn.email,
            "fecha_nacimiento" to userSignIn.birthDate,
            "nombre" to userSignIn.name,
            "reservo_libro" to false
        )

        firebase.db
            .collection(USER_COLLECTION)
            .document(uid) // Specify the custom document ID here
            .set(user)
            .await()

//        firebase.db
//            .collection(USER_COLLECTION)
//            .add(user).await()

    }.isSuccess


    suspend fun addSubsciptionId(subscriptionId: String, userId: String) {
        val userToUpdate = firebase.db.collection(USER_COLLECTION).document(userId)

        val updates = hashMapOf(
            "suscripcion_mp_id" to subscriptionId,
        //    "reservo_libro" to false
        )

        try {
            // Update the document using await() to wait for the result
            userToUpdate.update(updates as Map<String, Any>).await()
            Log.d(TAG, "update MP ok")
            // The document was successfully updated
            // You can add code for any success handling here
        } catch (e: Exception) {
            Log.d(TAG, "failure on update MP ${e.message}")
            // Handle the error
            // You can add code to handle the error here
        }
    }

    suspend fun getUserBySubscriptionId(subscriptionId: String, userId: String): Boolean {
        val userRef = Firebase.firestore.collection("users").document(userId)

        try {
            val snapshot = userRef.get().await()

            if (snapshot.exists()) {
                val userData = snapshot.data
                if (userData != null) {
                    if (userData["suscripcion_mp_id"] == subscriptionId) {
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
        val userRef = Firebase.firestore.collection("users").document(userId)

        return try {
            val snapshot = userRef.get().await()

            if (snapshot.exists()) {
                val userData = snapshot.data
                val suscripcionMpId = userData?.get("suscripcion_mp_id") as? String

                return if (suscripcionMpId.isNullOrEmpty()) {
                    // User does not have the desired subscription or it is empty
                    Log.d(TAG, "User does not have the desired subscription or it is empty")
                    // You can add code for handling this case
                    Result.Success(User())
                } else {
                    // User has the desired subscription
                    Log.d(TAG, "User has subscription $suscripcionMpId")
                    val isEnabled = userData["reservo_libro"] as? Boolean
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
}