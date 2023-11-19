package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.LAST_NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.NAME
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfileBinding
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var isInEditMode = false
    private var userDocumentReference: DocumentReference? = null
    private var previousEmail: String? = null
    private val viewModel : UserProfileViewModel by viewModels()

    private lateinit var binding: UserProfileBinding
    private val firestore = FirebaseFirestore.getInstance()


    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            previousEmail = userEmail
            Log.d(TAG, "Email of the logged-in user: $userEmail")

            if (userEmail != null) {
                viewModel.getUserByEmail(userEmail)
            } // Notify ViewModel about the user's email
        } else {
            Log.d(TAG, "User is null")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImage.setOnClickListener {
            findNavController().navigate(R.id.changeProfileImageFragment)
        }

        binding.editFab.setOnClickListener {
            if (!isInEditMode) {
                Log.d(TAG, "in edit mode from click listener")
                // Enter edit mode
                enterEditMode()
            } else {
                Log.d(TAG, "to save changes from click listener ")
                // Save the changes
                saveChanges()
            }
        }

        // Observe user data
        viewModel.user.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    // Update UI with user data
                    updateUI(user)
                    userDocumentReference = result.data.documentReference
                }

                is Result.Error -> {
                    // Handle error
                    Log.e(TAG, "Error retrieving user data: ${result.message}")
                }

                is Result.Loading -> {
                    // Show loading indicator if needed
                }

                else -> {}
            }
        }
    }

    private fun updateUI(user: User) {
        // Update UI elements with user data
        binding.editNombre.setText(user.name)
        binding.editApellido.setText(user.lastName)
        binding.editEmail.setText(user.email)

        if (!user.image.isNullOrEmpty()) {
            val resourceId = resources.getIdentifier(
                user.image,
                "drawable",
                requireContext().packageName
            )
            binding.profileImage.setImageResource(resourceId)
        } else {
            // Default image
            binding.profileImage.setImageResource(R.drawable.alejandria_logo)
        }
    }


//    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
//        val user = auth.currentUser
//        if (user != null) {
//            val userEmail = user.email
//            previousEmail = userEmail
//            Log.d(TAG, "Email of the logged-in user: $userEmail")
//
//            // Query the Firestore collection to find a user with the matching email.
//            firestore.collection(USER_COLLECTION)
//                .whereEqualTo(EMAIL, userEmail)
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    if (!querySnapshot.isEmpty) {
//                        val document =
//                            querySnapshot.documents[0] // Access the first (and only) document
//
//                        // User document found, you can access its data here.
//                        val nombre = document.getString(NAME)
//                        val apellido = document.getString(LAST_NAME)
//                        val image = document.getString(IMAGE)
//
//                        Log.d(TAG, "Nombre: $nombre, Apellido: $apellido, Email: $userEmail")
//
//                        // Update the edit texts with the retrieved data
//                        binding.editNombre.setText(nombre)
//                        binding.editApellido.setText(apellido)
//                        binding.editEmail.setText(userEmail)
//
//                        // Load user image
//                        if (!image.isNullOrEmpty()) {
//                            val resourceId = resources.getIdentifier(
//                                image,
//                                "drawable",
//                                requireContext().packageName
//                            )
//                            binding.profileImage.setImageResource(resourceId)
//                        } else {
//                            // Default image
//                            binding.profileImage.setImageResource(R.drawable.alejandria_logo)
//                        }
//
//                        userDocumentReference = document.reference
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.e(TAG, "Error querying Firestore: $exception")
//                }
//        } else {
//            Log.d(TAG, "User is null")
//        }
//    }






//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.profileImage.setOnClickListener {
//            findNavController().navigate(R.id.changeProfileImageFragment)
//        }
//
//        binding.editFab.setOnClickListener {
//            if (!isInEditMode) {
//                // Enter edit mode
//                enterEditMode()
//            } else {
//                // Save the changes
//                saveChanges()
//            }
//        }
//    }



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
        binding.editEmail.isEnabled = true

        // Cambiar el texto del encabezado
        binding.userMailHeader.text = "Cambio de nombre"

        binding.editNombre.requestFocus()
        binding.editNombre.text?.let { binding.editNombre.setSelection(it.length) }

        binding.editFab.setImageResource(R.drawable.ic_save)
    }

    private fun saveChanges() {
        // Update the Firestore document with the edited values
        val newNombre = binding.editNombre.text.toString()
        val newApellido = binding.editApellido.text.toString()

        if (previousEmail != binding.editEmail.text.toString()) {
            Toast.makeText(
                requireContext(),
                "El email es distinto",
                Toast.LENGTH_LONG
            ).show()
            showChangeEmailVerification()
        }

        // Get the current user's email
        userDocumentReference?.update(

            mapOf(
                NAME to newNombre,
                LAST_NAME to newApellido
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
        binding.editEmail.isEnabled = false
        binding.editFab.setImageResource(R.drawable.ic_edit)
        binding.userMailHeader.text = getString(R.string.personalInfo)

        isInEditMode = false
    }

    private fun saveEmail(user: FirebaseUser) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val newEmail = binding.editEmail.text.toString()

        val validEmail = Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() || newEmail.isEmpty()

        if (validEmail) {


            userDocumentReference?.update(
                mapOf(
                    EMAIL to newEmail
                )
            )

                ?.addOnSuccessListener {

                    Log.d(TAG, "Actualizado")
                }
                ?.addOnFailureListener { exception ->
                    Log.e(TAG, "Error: $exception")
                }
            bottomSheetDialog.dismiss()

        } else {
            Toast.makeText(context, "El correo es inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showChangeEmailVerification() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.fragment_pw_confirmation, null)
        val btnEmailChangeConfirmation = view.findViewById<Button>(R.id.btnChangeConfirmation)
        val password = view.findViewById<TextInputEditText>(R.id.passwordEmailChange)
        val newEmail = binding.editEmail.text.toString()


        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        btnEmailChangeConfirmation.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            val pass = password.text.toString()

            Log.d(TAG, "current user in showChangeEmail $user")
            //   val credential = EmailAuthProvider.getCredential(user?.email ?: "", pass)

            val credential = EmailAuthProvider.getCredential("$previousEmail", pass)

            Log.d(TAG, "credentials $credential")

            if (pass.isNotEmpty()) {
                user?.reauthenticate(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "reauthenticate is successful")
                            user.updateEmail(newEmail)
                            userDocumentReference?.update(
                                mapOf(
                                    EMAIL to newEmail
                                )
                            )
                            // saveEmail(user)
                            bottomSheetDialog.dismiss()
                        } else {
                            Log.d(TAG, "La reautenticación falló")
                        }
                    }
            }
        }
    }
}