package com.matiasmandelbaum.alejandriaapp.ui.signin

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.ex.dismissKeyboard
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
import com.matiasmandelbaum.alejandriaapp.data.util.time.TimeUtils.DAY_MONTH_YEAR
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSigninBinding
import com.matiasmandelbaum.alejandriaapp.ui.signin.model.UserSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSigninBinding
    private lateinit var datePicker: MaterialDatePicker<Long>

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initUI()
        return binding.root
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
                viewModel.viewState.collect { viewState ->
                    updateUI(viewState)
                }
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            editTextNombre.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
            }

            editTextApellido.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
            }

            editTextEmail.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
            }

            editTextContrasenia.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
            }

            editTextFechaNacimiento.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
                setOnClickListener {
                    showDatePicker()
                }
            }

            editTextRepetirContrasenia.apply {
                loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
                setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
                onTextChanged { onFieldChanged() }
            }

            editTextFechaNacimiento.setOnClickListener {
                showDatePicker()
            }

            alreadyHaveAccount.setOnClickListener {
                viewModel.onLoginSelected()
            }

            buttonRegistro.setOnClickListener {
                it.dismissKeyboard()
                viewModel.onSignInSelected(
                    UserSignIn(
                        name = editTextNombre.text.toString(),
                        lastName = editTextApellido.text.toString(),
                        email = editTextEmail.text.toString(),
                        birthDate = editTextFechaNacimiento.text.toString(),
                        password = editTextContrasenia.text.toString(),
                        passwordConfirmation = editTextRepetirContrasenia.text.toString()
                    )
                )
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            navigateToHome.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    goToHome()
                    showSignInSucessful()
                }
            }

            showErrorDialog.observe(viewLifecycleOwner) { showError ->
                if (showError) {
                    showEmailAlreadyRegistered()
                }
            }

            navigateToLogin.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    goToLogin()
                }
            }
        }
    }

    private fun showSignInSucessful() {
        val snackbar = Snackbar.make(
            requireView(),
            getString(R.string.registro_exitoso), Snackbar.LENGTH_SHORT
        )
        snackbar.show()
    }

    private fun showEmailAlreadyRegistered() {
        val snackbar = Snackbar.make(
            requireView(),
            getString(R.string.error_signin), Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }

    private fun goToLogin() {
        val action = SignInFragmentDirections.actionSignInFragmentToLoginFragment()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigate(action, navOptions)
    }

    private fun goToHome() {
        val action = SignInFragmentDirections.actionSignInFragmentToHomeListFragment()
        findNavController().navigate(action)
    }

    private fun updateUI(viewState: SignInViewState) {
        with(binding) {
            textInputLayoutNombre.error =
                if (viewState.isValidName) null else getString(R.string.signin_error_realname)
            textInputLayoutApellido.error =
                if (viewState.isValidLastName) null else getString(R.string.signin_error_nickname)
            textInputLayoutEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.signin_error_mail)
            textInputLayoutFechaNacimiento.error =
                if (viewState.isValidDate) null else getString(R.string.signin_error_date)
            textInputLayoutContrasenia.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
            textInputLayoutRepetirContrasenia.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
        }
    }

    private fun onFieldChanged(hasFocus: Boolean = false) {
        if (!hasFocus) {
            viewModel.onFieldsChanged(
                UserSignIn(
                    name = binding.editTextNombre.text.toString(),
                    lastName = binding.editTextApellido.text.toString(),
                    email = binding.editTextEmail.text.toString(),
                    birthDate = binding.editTextFechaNacimiento.text.toString(),
                    password = binding.editTextContrasenia.text.toString(),
                    passwordConfirmation = binding.editTextRepetirContrasenia.text.toString()
                )
            )
        }
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
            val format = SimpleDateFormat(DAY_MONTH_YEAR, Locale.getDefault())
            val selectedDateStr = format.format(Date(it))
            val selectedLocalDate =
                LocalDate
                    .parse(
                        selectedDateStr,
                        DateTimeFormatter.ofPattern(DAY_MONTH_YEAR)
                    )
            val format_second = DateTimeFormatter.ofPattern(DAY_MONTH_YEAR)
            val formattedDate = selectedLocalDate.format(format_second)

            val editableText = Editable.Factory.getInstance().newEditable(formattedDate)

            binding.editTextFechaNacimiento.text = editableText
            hasSelectedDate = true
            finalSelectedDate = formattedDate
        }

        datePicker.addOnNegativeButtonClickListener {
            if (hasSelectedDate) {
                binding.editTextFechaNacimiento.text = Editable.Factory.getInstance().newEditable(
                    finalSelectedDate
                )
            } else {
                binding.editTextFechaNacimiento.text =
                    Editable.Factory.getInstance().newEditable(" ")
            }

        }
    }

    private fun showDatePicker() {
        datePicker.show(parentFragmentManager, "datePicker")
    }
}



