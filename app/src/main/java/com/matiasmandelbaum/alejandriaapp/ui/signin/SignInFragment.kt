package com.matiasmandelbaum.alejandriaapp.ui.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.matiasmandelbaum.alejandriaapp.ui.signup.SignInViewModel

import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


private const val TAG  = "SignUpFragment"
@AndroidEntryPoint
class SignInFragment: Fragment() {

    private lateinit var binding : FragmentSignInBinding
    private lateinit var datePicker: MaterialDatePicker<Long>

    private val viewModel : SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreate")
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupDatePicker()

//        viewModel.onDatePickerClicked.observe(viewLifecycleOwner){
//            {
//            //{
//              Log.d(TAG, "Hola! click en date pciekr")
//            }
//            Log.d(TAG, "Hola! click en date pciekr")
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpFragment = this
    }


    private fun setupDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDateStr = format.format(Date(it))
            val selectedLocalDate =
                LocalDate.parse(selectedDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
          //  binding.viewModel?.selectedDate?.set(selectedLocalDate)
        }
    }

    fun showDatePicker() {
        Log.d(TAG, "showDatePicker()")
        // Log.d("EntryDeviceFragment", "clase desde InventoryViewModel: ${inventoryViewModel.type}")
        datePicker.show(parentFragmentManager, "datePicker")
    }
}