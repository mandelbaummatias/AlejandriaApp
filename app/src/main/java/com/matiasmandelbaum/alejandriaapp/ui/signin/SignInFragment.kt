package com.matiasmandelbaum.alejandriaapp.ui.signin

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.ex.dismissKeyboard
import com.matiasmandelbaum.alejandriaapp.common.ex.loseFocusAfterAction
import com.matiasmandelbaum.alejandriaapp.common.ex.onTextChanged
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
import javax.inject.Inject


private const val TAG = "SignUpFragment"

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
        Log.d(TAG, "onCreate")
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initUI()
        setupDatePicker()


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    updateUI(viewState)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInFragment = this
    }

    private fun initUI() {
        initListeners()
        initObservers()
//        binding.viewBottom.tvFooter.text = span(
//            getString(R.string.signin_footer_unselected),
//            getString(R.string.signin_footer_selected)
//        )
    }

    private fun initListeners() {
        binding.editTextNombre.apply {
            //   Log.d(TAG, "editNombre focus methods")
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editTextApellido.apply {
            //   Log.d(TAG, "editApellido focus methods")
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editTextEmail.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editTextContrasenia.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editTextFechaNacimiento.apply {

            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editTextRepetirContrasenia.apply {
            loseFocusAfterAction(EditorInfo.IME_ACTION_NEXT)
            setOnFocusChangeListener { _, hasFocus -> onFieldChanged(hasFocus) }
            onTextChanged { onFieldChanged() }
        }

        binding.editTextFechaNacimiento.setOnClickListener {
            showDatePicker()
        }

        binding.alreadyHaveAccount.setOnClickListener {
            viewModel.onLoginSelected()
        }


        with(binding) {
            btnRegistro.setOnClickListener {
                it.dismissKeyboard()
                viewModel!!.onSignInSelected(
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
    }

    private fun initObservers() {
        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                goToHome()
                showSignInSucessful()
            }
        }

        viewModel.showErrorDialog.observe(viewLifecycleOwner) { showError ->
            if (showError) {
                showEmailAlreadyRegistered()
            }
        }

        viewModel.navigateToVerifyEmail.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                goToVerifyEmail()
            }
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        }
    }

    private fun showSignInSucessful(){
        val snackbar = Snackbar.make(requireView(),
            getString(R.string.registro_exitoso), Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    private fun goToVerifyEmail() {
        val action = SignInFragmentDirections.actionSignInFragmentToVerificationFragment()
        findNavController().navigate(action)
    }

    private fun showEmailAlreadyRegistered(){
        val snackbar = Snackbar.make(requireView(),
            getString(R.string.error_signin), Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun goToLogin(){
        val action = SignInFragmentDirections.actionSignInFragmentToLoginFragment()
        findNavController().navigate(action)
    }



    private fun goToHome(){
        val action = SignInFragmentDirections.actionSignInFragmentToHomeListFragment()
        findNavController().navigate(action)
    }

//    private fun showErrorDialog() {
//        ErrorDialog.create(
//            title = getString(R.string.signin_error_dialog_title),
//            description = getString(R.string.signin_error_dialog_body),
//            positiveAction = ErrorDialog.Action(getString(R.string.signin_error_dialog_positive_action)) {
//                it.dismiss()
//            }
//        ).show(dialogLauncher, requireActivity())
//    }

    private fun updateUI(viewState: SignInViewState) {
        with(binding) {
            //pbLoading.isVisible = viewState.isLoading
            binding.textInputLayoutNombre.error =
                if (viewState.isValidName) null else getString(R.string.signin_error_realname)
            binding.textInputLayoutApellido.error =
                if (viewState.isValidLastName) null else getString(R.string.signin_error_nickname)
            binding.textInputLayoutEmail.error =
                if (viewState.isValidEmail) null else getString(R.string.signin_error_mail)
            binding.textInputLayoutFechaNacimiento.error =
                if (viewState.isValidDate) null else getString(R.string.signin_error_date)
            binding.textInputLayoutContrasenia.error =
                if (viewState.isValidPassword) null else getString(R.string.signin_error_password)
            binding.textInputLayoutRepetirContrasenia.error =
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

            binding.editTextFechaNacimiento.text = editableText
            hasSelectedDate = true
            finalSelectedDate = formattedDate
        }

        datePicker.addOnNegativeButtonClickListener {
            Log.d(TAG, "en negative viendo hasSelectedDate: $hasSelectedDate")
            //  datePicker.selection?.let {
            //      it.let {
            if (hasSelectedDate) {
                binding.editTextFechaNacimiento.text = Editable.Factory.getInstance().newEditable(
                    finalSelectedDate
                )
            } else {
                Log.d(TAG, "doesn't have selected date")
                binding.editTextFechaNacimiento.text =
                    Editable.Factory.getInstance().newEditable(" ")
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



