package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.data.signin.remote.UserService.Companion.USER_COLLECTION
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfileBinding
import com.matiasmandelbaum.alejandriaapp.ui.signin.SignInFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var isInEditMode = false
    private var userDocumentReference: DocumentReference? = null
    private var previousEmail: String? = null

    private lateinit var binding: UserProfileBinding
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
                        val document =
                            querySnapshot.documents[0] // Access the first (and only) document

                        // User document found, you can access its data here.
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")

                        Log.d(TAG, "Nombre: $nombre, Apellido: $apellido")

                        // Update the edit texts with the retrieved data
                        binding.editNombre.setText(nombre)
                        binding.editApellido.setText(apellido)

                        userDocumentReference = document.reference
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

        binding.overlayFab1.visibility = View.GONE
        binding.overlayFab2.visibility = View.GONE

        binding.editFab.setOnClickListener {
            if (binding.overlayFab1.visibility == View.VISIBLE) {
                // Hide overlay_fab_1 and overlay_fab_2
                binding.overlayFab1.visibility = View.GONE
                binding.overlayFab2.visibility = View.GONE
            } else {
                // Show overlay_fab_1 and overlay_fab_2
                binding.overlayFab1.visibility = View.VISIBLE
                binding.overlayFab2.visibility = View.VISIBLE
            }
        }

        binding.overlayFab1.setOnClickListener {
            if (!isInEditMode) {
                // Enter edit mode
                enterEditMode()
            } else {
                // Save the changes
                saveChanges()
            }
        }

        binding.overlayFab2.setOnClickListener {
            // Aquí navegamos al fragmento userMailFragment
            findNavController().navigate(com.matiasmandelbaum.alejandriaapp.R.id.userMailFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        AuthManager.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        AuthManager.removeAuthStateListener(authStateListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun enterEditMode() {
        isInEditMode = true

        // Set TextInputEditText fields as editable
        binding.editNombre.isEnabled = true
        binding.editApellido.isEnabled = true

        // Cambiar el texto del encabezado
        binding.userMailHeader.text = "Cambio de nombre"

        binding.editNombre.requestFocus()
        binding.editNombre.text?.let { binding.editNombre.setSelection(it.length) }

        binding.overlayFab1.setImageResource(com.matiasmandelbaum.alejandriaapp.R.drawable.ic_save)
    }

    private fun saveChanges() {
        // Update the Firestore document with the edited values
        val newNombre = binding.editNombre.text.toString()
        val newApellido = binding.editApellido.text.toString()

        // Get the current user's email
        userDocumentReference?.update(
            mapOf(
                "nombre" to newNombre,
                "apellido" to newApellido
            )
        )
            ?.addOnSuccessListener {
                // Document updated successfully
                Log.d(TAG, "Document updated successfully")
                Toast.makeText(context, "Actualizado con éxito", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { exception ->
                Log.e(TAG, "Error updating document: $exception")
            }

        binding.editNombre.isEnabled = false
        binding.editApellido.isEnabled = false
        binding.overlayFab1.setImageResource(com.matiasmandelbaum.alejandriaapp.R.drawable.ic_profile)
        binding.userMailHeader.text = getString(com.matiasmandelbaum.alejandriaapp.R.string.personalInfo)

        isInEditMode = false
    }
}
