package com.matiasmandelbaum.alejandriaapp.ui.userprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService.Companion.USER_COLLECTION
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfileMainBinding
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var isInEditMode = false
    private var userDocumentReference: DocumentReference? = null
    private var previousEmail: String? = null


    private lateinit var binding: UserProfileMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            previousEmail = userEmail
            Log.d(TAG, "Email of the logged-in user: $userEmail")

            // Query the Firestore collection to find a user with the matching email.
            firestore.collection(USER_COLLECTION)
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0] // Access the first (and only) document

                        // User document found, you can access its data here.
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val email = document.getString("email")

                        Log.d(TAG, "$nombre")
                        Log.d(TAG, "$apellido")
                        Log.d(TAG, "$email")

                        // Update the edit texts with the retrieved data
                        binding.editNombre.setText(nombre)
                        binding.editApellido.setText(apellido)
                        binding.editEmail.setText(email)

                        userDocumentReference = document.reference
                        Log.d(TAG, "Nombre: $nombre, Apellido: $apellido, Email: $email")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error querying Firestore: $exception")
                }
        } else {
            Log.d(TAG, "User is null")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editFab.setOnClickListener {
            if (isInEditMode) {
                // Save the changes
                saveChanges()
            } else {
                // Enter edit mode
                enterEditMode()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //   authManager.addAuthStateListener(authStateListener)
        AuthManager.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        AuthManager.removeAuthStateListener(authStateListener)
        //  authManager.removeAuthStateListener(authStateListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileMainBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun enterEditMode() {
        isInEditMode = true
        binding.editNombreLayout.visibility = View.VISIBLE
        binding.editApellidoLayout.visibility = View.VISIBLE
        binding.editEmailLayout.visibility = View.VISIBLE


        // Set TextInputEditText fields as editable
        binding.editNombre.isEnabled = true
        binding.editApellido.isEnabled = true
        binding.editEmail.isEnabled = true

        binding.editNombre.requestFocus()
        binding.editNombre.text?.let { binding.editNombre.setSelection(it.length) }

        binding.editFab.setImageResource(com.matiasmandelbaum.alejandriaapp.R.drawable.ic_save)
    }

    private fun saveChanges() {
        // Update the Firestore document with the edited values
        val newNombre = binding.editNombre.text.toString()
        val newApellido = binding.editApellido.text.toString()
        val newEmail = binding.editEmail.text.toString()

        // Get the current user's email
        userDocumentReference?.update(
            mapOf(
                "nombre" to newNombre,
                "apellido" to newApellido,
                "email" to newEmail
            )
        )
            ?.addOnSuccessListener {
                // Document updated successfully
                Log.d(TAG, "Document updated successfully")
            }
            ?.addOnFailureListener { exception ->
                Log.e(TAG, "Error updating document: $exception")
            }

       // val currentUser = FirebaseAuth.getInstance().currentUser

        val user = FirebaseAuth.getInstance().currentUser
        // Get auth credentials from the user for re-authentication
        // Get auth credentials from the user for re-authentication
        val credential = EmailAuthProvider
            //hay que poenr el mail de nuevo y la contraseña
            .getCredential("${user?.email}", "1") // Current Login Credentials \\

        // Prompt the user to re-provide their sign-in credentials
        // Prompt the user to re-provide their sign-in credentials
        user!!.reauthenticate(credential)
            .addOnCompleteListener {
                Log.d(TAG, "User re-authenticated.")
                //Now change your email address \\
                //----------------Code for Changing Email Address----------\\
              //  val user = FirebaseAuth.getInstance().currentUser
                user!!.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User email address updated.")
                        }
                    }
                //----------------------------------------------------------\\
            }

        binding.editNombre.isEnabled = false
        binding.editApellido.isEnabled = false
        binding.editEmail.isEnabled = false

        binding.editFab.setImageResource(com.matiasmandelbaum.alejandriaapp.R.drawable.ic_edit)

        isInEditMode = false
    }

}

