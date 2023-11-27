package com.matiasmandelbaum.alejandriaapp.ui.userprofilemain

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfileBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.userinput.UserDataInput
import com.matiasmandelbaum.alejandriaapp.ui.passwordconfirmation.PasswordConfirmationFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class UserProfileFragment : Fragment(), DialogClickListener {

    private var isInEditMode = false
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var previousEmail: String
    private lateinit var previousDate: String
    private lateinit var newEmail: String
    private val viewModel: UserProfileViewModel by viewModels()

    private lateinit var binding: UserProfileBinding

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initCollectors()
        initListeners()
        initObservers()
        setupDatePicker()
    }

    private fun initCollectors() {
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
    }

    private fun initListeners() {
        with(binding) {
            editNombre.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                onTextChanged { onFieldChanged() }
            }.loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)

            editApellido.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
            }

            editEmail.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onEmailChanged(hasFocus) }
                onTextChanged { onEmailChanged() }
            }

            editDate.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
                setOnClickListener {
                    previousDate = editDate.text.toString()
                    showDatePicker()
                }
            }

            editFab.setOnClickListener {
                if (!isInEditMode) {
                    enterEditMode()
                } else {
                    if (viewModel.onSaveProfileSelected(
                            UserDataInput(
                                editNombre.text.toString(),
                                editApellido.text.toString(),
                                previousEmail,
                                editDate.text.toString()
                            )
                        )
                    ) {
                        exitEditMode()
                    }

                    newEmail = editEmail.text.toString()

                    viewModel.onSaveUserEmailSelected(
                        editEmail.text.toString(), previousEmail
                    )
                }
            }

            profileImage.setOnClickListener {
                findNavController().navigate(R.id.changeProfileImageFragment)
            }
        }
    }

    private fun initObservers() {
        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { handleAuthState(it) }

        with(viewModel) {
            userProfile.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        handleLoading(false)
                        val user = result.data
                        updateUI(user)
                    }

                    is Result.Error -> {
                        handleLoading(false)
                    }

                    is Result.Loading -> {
                        handleLoading(true)
                    }

                    else -> {
                        Unit
                    }
                }
            }

            showErrorDialog.observe(viewLifecycleOwner) {
                if (it.showErrorDialog) {
                    showOnLoginErrorMessage()
                }
            }

            showPasswordRequiredDialog.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    showChangeEmailVerification()
                }
            }

            showOnSuccessfulSavedDataMessage.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    showProfileUpdateSuccessMessage()
                }
            }
        }
    }

    private fun handleAuthState(user: FirebaseUser?) {
        user?.email?.let {
            previousEmail = it
            viewModel.getUserByEmail(it)
        }
    }

    private fun showOnLoginErrorMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_en_login), Snackbar.LENGTH_SHORT
        ).show()
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
                profileImage.setImageResource(R.drawable.alejandria_logo)
            }
        }
    }

    private fun onErrorProfileUpdateUI(viewState: UserProfileViewState) {
        with(binding) {
            editNombreLayout.error =
                if (viewState.isValidName) null else getString(R.string.nombre_invalido)
            editApellidoLayout.error =
                if (viewState.isValidLastName) null else getString(R.string.apellido_invalido)
            editDateLayout.error =
                if (viewState.isValidBirthDate) null else getString(R.string.fecha_invalida)
        }
    }

    private fun onErrorEmailUpdateUI(viewState: UserEmailViewState) {
        with(binding) {
            editEmailLayout.error =
                if (viewState.isValidEmail) null else getString(R.string.email_invalido)
        }
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
            userMailHeader.text = getString(R.string.personal_info)

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
                UserDataInput(
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
            binding.editEmail.setText(previousEmail)
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
            getString(R.string.email_no_fue_actualizado),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setupDatePicker() {
        val calendarMin = Calendar.getInstance()
        calendarMin.add(Calendar.YEAR, -18)

        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -18)
        val minDateInMillis = cal.timeInMillis

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
            val format2 = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = selectedLocalDate.format(format2)

            val editableText = Editable.Factory.getInstance().newEditable(formattedDate)

            binding.editDate.text = editableText
            hasSelectedDate = true
            finalSelectedDate = formattedDate
            binding.editDate.requestFocus()
            binding.editDate.text?.let { binding.editDate.setSelection(it.length) }
        }

        datePicker.addOnNegativeButtonClickListener {
            if (hasSelectedDate) {
                binding.editDate.text = Editable.Factory.getInstance().newEditable(
                    finalSelectedDate
                )
            } else {
                binding.editDate.setText(previousDate)
                binding.editDate.requestFocus()
                binding.editDate.text?.let { binding.editDate.setSelection(it.length) }
            }

        }
    }

    private fun showDatePicker() {
        datePicker.show(parentFragmentManager, "datePicker")
    }
}

