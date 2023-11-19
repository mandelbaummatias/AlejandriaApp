package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.EMAIL
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.LAST_NAME
import com.matiasmandelbaum.alejandriaapp.data.util.firebaseconstants.users.UsersConstants.NAME
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfileBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.UserEmailViewState
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.UserProfileViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var isInEditMode = false
    private var userDocumentReference: DocumentReference? = null
    private var previousEmail: String? = null
    private val viewModel: UserProfileViewModel by viewModels()

    private lateinit var binding: UserProfileBinding

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


        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.editNombre.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            onTextChanged { onFieldChanged() }
        }.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)


        binding.editApellido.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editEmail.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onEmailChanged(hasFocus) }
            onTextChanged { onEmailChanged() }
        }

        // with(binding) {
        // binding.editFab.setOnClickListener {
        //     it.dismissKeyboard()

        binding.editFab.setOnClickListener {
            if (!isInEditMode) {
                Log.d(TAG, "in edit mode from click listener")
                // Enter edit mode
                enterEditMode()
                //it.dismissKeyboard()
            } else {

                viewModel.onSaveProfileSelected(
                    binding.editNombre.text.toString(),
                    binding.editApellido.text.toString(),
                    binding.editEmail.text.toString()
                )

                viewModel.onSaveUserEmailSelected(
                    binding.editEmail.text.toString()
                )

                Log.d(TAG, "to save changes from click listener ")
                // Save the changes
                //     saveChanges()
//                binding.editNombre.apply {
//                    loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
//                    onTextChanged { onFieldChanged() }
//                }.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
//
//
//                binding.editApellido.apply {
//                    loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
//                    setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
//                    onTextChanged { onFieldChanged() }
//                }
//
//                binding.editEmail.apply {
//                    loseFocusAfterAction(EditorInfo.IME_ACTION_DONE)
//                    setOnFocusChangeListener { _, hasFocus -> onEmailChanged(hasFocus) }
//                    onTextChanged { onFieldChanged() }
//                }
//
//                // with(binding) {
//                // binding.editFab.setOnClickListener {
//                //     it.dismissKeyboard()
//                viewModel.onSaveProfileSelected(
//                    binding.editNombre.text.toString(),
//                    binding.editApellido.text.toString(),
//                    binding.editEmail.text.toString()
//                )
//
//                viewModel.onSaveUserEmailSelected(
//                    binding.editEmail.text.toString()
//                )

                Log.d(TAG, "cual es el email? ${binding.editEmail.text}")
                // }
                // }

            }
        }
    }

    private fun initObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    handleLoading(false)
                    val user = result.data
                    // Update UI with user data
                    updateUI(user)
                    userDocumentReference = result.data.documentReference
                }

                is Result.Error -> {
                    handleLoading(false)
                    // Handle error
                    Log.e(TAG, "Error retrieving user data: ${result.message}")
                }

                is Result.Loading -> {
                    handleLoading(true)
                }

                else -> {
                    Unit
                }
            }
        }


        viewModel.showErrorDialog.observe(viewLifecycleOwner) {
            if (it.showErrorDialog) {
                Log.d(TAG, "HAY UN ERROR CON EL LOGIN ")
            }
        }
    }

    private fun updateUI(userProfile: UserProfile) {
        with(binding) {
            editNombre.setText(userProfile.name)
            editApellido.setText(userProfile.lastName)
            editEmail.setText(userProfile.email)

            if (!userProfile.image.isNullOrEmpty()) {
                val resourceId = resources.getIdentifier(
                    userProfile.image,
                    "drawable",
                    requireContext().packageName
                )
                profileImage.setImageResource(resourceId)
            } else {
                // Default image
                profileImage.setImageResource(R.drawable.alejandria_logo)
            }
        }
    }

    private fun onErrorProfileUpdateUI(viewState: UserProfileViewState) {
        Log.d(TAG, "onErrorProfileUpdateUI")
        with(binding) {
            //pbLoading.isVisible = viewState.isLoading
            editNombreLayout.error =
                if (viewState.isValidName) null else getString(R.string.nombre_invalido)
            editApellidoLayout.error =
                if (viewState.isValidLastName) null else getString(R.string.apellido_invalido)
        }
    }

    private fun onErrorEmailUpdateUI(viewState: UserEmailViewState) {
        Log.d(TAG, "onErrorEmailUpdateUI")
        with(binding) {
            editEmailLayout.error =
                if (viewState.isValidEmail) null else getString(R.string.email_invalido)
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

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileBinding.inflate(inflater, container, false)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect {
                    onErrorProfileUpdateUI(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.emailViewState.collect {
                    onErrorEmailUpdateUI(it)
                }
            }
        }
        return binding.root
    }

    private fun enterEditMode() {
        isInEditMode = true

        with(binding) {
            editNombre.isEnabled = true
            editApellido.isEnabled = true
            editEmail.isEnabled = true

            userMailHeader.text = "Cambio de nombre"

            editNombre.requestFocus()
            editNombre.text?.let { editNombre.setSelection(it.length) }

            editFab.setImageResource(R.drawable.ic_save)
        }
    }

    private fun saveChanges() {
        // Update the Firestore document with the edited values
        val newNombre = binding.editNombre.text.toString()
        val newApellido = binding.editApellido.text.toString()
        val newEmail = binding.editEmail.text.toString()

        // Always update name and last name
        userDocumentReference?.update(
            mapOf(
                NAME to newNombre,
                LAST_NAME to newApellido
            )
        )?.addOnSuccessListener {
            // Document updated successfully
            Log.d(TAG, "Document updated successfully")

            // Show Toast only if the email is not different
            if (previousEmail == newEmail) {
                Toast.makeText(context, "Actualizado con éxito", Toast.LENGTH_SHORT).show()
            }

            // If the email is different, prompt the email verification
            if (previousEmail != newEmail) {
                showChangeEmailVerification()
            }
        }?.addOnFailureListener { exception ->
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
                            user.updateEmail(newEmail).addOnSuccessListener {
                                userDocumentReference?.update(
                                    mapOf(
                                        EMAIL to newEmail
                                    )
                                )
                                binding.editEmail.setText(newEmail)
                                bottomSheetDialog.dismiss()
                                Log.d(TAG, "Actualizado")
                            }.addOnFailureListener {
                                Log.d(TAG, "update del mail fallo")
                            }
                                .addOnCanceledListener {
                                    Log.d(TAG, "cancelado")
                                }

                            // saveEmail(user)

                        } else {
                            Log.d(TAG, "La reautenticación falló")
                        }
                    }
            }
        }
    }


    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            viewModel.onFieldsChanged(
                name = binding.editNombre.text.toString(),
                lastName = binding.editApellido.text.toString()
            )
        }
    }

    private fun onEmailChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            viewModel.onEmailChanged(
                email = binding.editEmail.text.toString()
            )
        }
    }


    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                userProfileScreen.visibility = View.GONE
                progressBarUserProfile.visibility = View.VISIBLE
            } else {
                userProfileScreen.visibility = View.VISIBLE
                progressBarUserProfile.visibility = View.GONE
            }
        }
    }

}

