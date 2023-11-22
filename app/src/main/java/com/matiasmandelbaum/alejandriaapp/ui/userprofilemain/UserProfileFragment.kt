package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfileBinding

import com.matiasmandelbaum.alejandriaapp.ui.passwordconfirmation.PasswordConfirmationFragment
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.UserEmailViewState
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.UserProfileViewState
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemail.model.UserProfile2
import dagger.hilt.android.AndroidEntryPoint
import com.matiasmandelbaum.alejandriaapp.ui.userprofilemain.UserProfile
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class UserProfileFragment : Fragment(), DialogClickListener {

    private var isInEditMode = false
    private var userDocumentReference: DocumentReference? = null
    private lateinit var datePicker: MaterialDatePicker<Long>

    //  private var previousEmail: String? = null
    lateinit var previousEmail: String
    lateinit var previousDate: String
    lateinit var newEmail: String
    private val viewModel: UserProfileViewModel by viewModels()

    private lateinit var binding: UserProfileBinding

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            if (userEmail != null) {
                previousEmail = userEmail
            }
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

        binding.editDate.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editDate.setOnClickListener {
            previousDate = binding.editDate.text.toString()
            showDatePicker()
        }

        binding.editFab.setOnClickListener {
            if (!isInEditMode) {
                Log.d(TAG, "in edit mode from click listener")
                // Enter edit mode
                enterEditMode()
                //it.dismissKeyboard()
            } else {
                Log.d(TAG, "PREVIOUS EMAIL $previousEmail")
//                viewModel.onSaveProfileSelected(
//                    binding.editNombre.text.toString(),
//                    binding.editApellido.text.toString(),
//                    previousEmail,
//                    binding.editDate.text.toString()
//                )
                if (viewModel.onSaveProfileSelected(
                        UserProfile(
                            binding.editNombre.text.toString(),
                            binding.editApellido.text.toString(),
                            previousEmail,
                            binding.editDate.text.toString()
                        )
                    )
                ) {
                    exitEditMode()
                }

                newEmail = binding.editEmail.text.toString()

                viewModel.onSaveUserEmailSelected(
                    binding.editEmail.text.toString(), previousEmail
                )
                // exitEditMode()

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

        viewModel.showPasswordRequiredDialog.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                //  showChangeEmailVerification()
                showChangeEmailVerification()
            }
        }

        viewModel.showOnSuccessfulSavedDataMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                showProfileUpdateSuccessMessage()
            }
        }
    }

    private fun showProfileUpdateSuccessMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.datos_actualizados_con_exito), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun updateUI(userProfile: com.matiasmandelbaum.alejandriaapp.domain.model.userprofile.UserProfile) {
        with(binding) {
            editNombre.setText(userProfile.name)
            editApellido.setText(userProfile.lastName)
            editEmail.setText(userProfile.email)
            editDate.setText(userProfile.birthDate)

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
            editDateLayout.error =
                if (viewState.isValidBirthDate) null else getString(R.string.fecha_invalida)
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
        setupDatePicker()
        return binding.root
    }

    private fun enterEditMode() {
        isInEditMode = true
        with(binding) {
            editNombre.isEnabled = true
            editApellido.isEnabled = true
            editEmail.isEnabled = true
            editDate.isEnabled = true
            userMailHeader.text = getString(R.string.modificar_datos)
            editNombre.requestFocus()
            editNombre.text?.let { editNombre.setSelection(it.length) }
            editFab.setImageResource(R.drawable.ic_save)
        }
    }

    private fun exitEditMode() {
        with(binding) {
            editNombre.isEnabled = false
            editApellido.isEnabled = false
            editEmail.isEnabled = false
            editDate.isEnabled = false
            editFab.setImageResource(R.drawable.ic_edit)
            userMailHeader.text = getString(R.string.personalInfo)

        }
        isInEditMode = false
    }


    private fun showChangeEmailVerification() {
        val newEmail = binding.editEmail.text.toString()
        val bottomSheetFragment = PasswordConfirmationFragment.newInstance(newEmail, previousEmail)
        bottomSheetFragment.setDialogClickListener(this@UserProfileFragment)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }


    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            viewModel.onFieldsChanged(
                UserProfile(
                    name = binding.editNombre.text.toString(),
                    lastName = binding.editApellido.text.toString(),
                    birthDate = binding.editDate.text.toString()
                )

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

    override fun onFinishClickDialog(clickValue: Boolean) {
        if (clickValue) {
            showEmailUpdateSuccesfulMessage()
            binding.editEmail.setText(newEmail)
        } else {
            binding.editEmail.setText(previousEmail) //handle loading?
            showEmailUpdateUnsuccessfulMessage()
        }
    }

    private fun showEmailUpdateSuccesfulMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.datos_actualizados_con_exito), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showEmailUpdateUnsuccessfulMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.email_no_fue_actualizado), // Assuming you have a string resource for the message
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setupDatePicker() {

        val calendarMin = Calendar.getInstance()
        calendarMin.add(Calendar.YEAR, -18)


        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -18)
        val minDateInMillis = cal.timeInMillis

// Define date validator
        val dateValidatorMin: CalendarConstraints.DateValidator =
            DateValidatorPointBackward.before(minDateInMillis)

        val constraints: CalendarConstraints =
            CalendarConstraints.Builder()
                .setValidator(dateValidatorMin)

                .build()


        val builder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)

        builder.setSelection(calendarMin.timeInMillis)
        builder.setTitleText(getString(R.string.selecciona_fecha_de_nacimiento))
        builder.setCalendarConstraints(constraints)
        datePicker = builder.build()

        var hasSelectedDate = false

        var finalSelectedDate = ""


        datePicker.addOnPositiveButtonClickListener {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDateStr = format.format(Date(it))
            val selectedLocalDate =
                LocalDate.parse(selectedDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            Log.d(TAG, "date: $selectedLocalDate")
            val format2 = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = selectedLocalDate.format(format2)

            // Convert the String to Editable
            val editableText = Editable.Factory.getInstance().newEditable(formattedDate)

            binding.editDate.text = editableText
            hasSelectedDate = true
            finalSelectedDate = formattedDate
            binding.editDate.requestFocus()
            binding.editDate.text?.let { binding.editDate.setSelection(it.length) }
        }

        datePicker.addOnNegativeButtonClickListener {
            Log.d(TAG, "en negative viendo hasSelectedDate: $hasSelectedDate")
            //  datePicker.selection?.let {
            //      it.let {
            if (hasSelectedDate) {
                binding.editDate.text = Editable.Factory.getInstance().newEditable(
                    finalSelectedDate
                )
            } else {
                Log.d(TAG, "doesn't have selected date")
//                binding.editDate.text =
//                    Editable.Factory.getInstance().newEditable(" ")
                binding.editDate.setText(previousDate)
                binding.editDate.requestFocus()
                binding.editDate.text?.let { binding.editDate.setSelection(it.length) }
                //        Log.d(TAG, "pasandole null en negative?")
                //viewModel.isValidDate(null)
            }

        }
    }

    fun showDatePicker() {
        Log.d(TAG, "showDatePicker()")
        datePicker.show(parentFragmentManager, "datePicker")
    }


}

