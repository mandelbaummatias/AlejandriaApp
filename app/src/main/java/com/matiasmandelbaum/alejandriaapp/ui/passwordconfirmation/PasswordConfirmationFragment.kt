package com.matiasmandelbaum.alejandriaapp.ui.passwordconfirmation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListenerProvider
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentPwConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "PasswordConfirmationFragment"

@AndroidEntryPoint
class PasswordConfirmationFragment : BottomSheetDialogFragment(), DialogClickListenerProvider {

    private lateinit var binding: FragmentPwConfirmationBinding
    private var listener: DialogClickListener? = null

    private val viewModel: PasswordConfirmationViewModel by viewModels()

    companion object {
        private const val ARG_NEW_EMAIL = "arg_new_email"
        private const val ARG_PREVIOUS_EMAIL = "arg_previous_email"

        fun newInstance(newEmail: String, previousEmail: String): PasswordConfirmationFragment {
            val fragment = PasswordConfirmationFragment()
            val args = Bundle()
            args.putString(ARG_NEW_EMAIL, newEmail)
            args.putString(ARG_PREVIOUS_EMAIL, previousEmail)
            fragment.arguments = args
            Log.d(TAG, "my args $args")
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPwConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

    }

    private fun initListeners() {
        val newEmail = arguments?.getString(ARG_NEW_EMAIL)
        val previousEmail = arguments?.getString(ARG_PREVIOUS_EMAIL)

        binding.btnChangeConfirmation.setOnClickListener {

            val pass = binding.passwordEmailChange.text.toString()
            if (pass.isNotEmpty())
                try {
                    if (newEmail != null) {
                        if (previousEmail != null) {
                            viewModel.changeUserEmail(newEmail, previousEmail, pass)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "exception $e")
                }
        }
//            val user = FirebaseAuth.getInstance().currentUser
//            val pass = binding.passwordEmailChange.text.toString()
//
//            if (pass.isNotEmpty()) {
//                try {
//                    val credential = EmailAuthProvider.getCredential("$previousEmail", pass)
//
//                    user?.reauthenticate(credential)
//                        ?.addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Log.d(
//                                    TAG,
//                                    "reauthenticate is successful"
//                                )
//                                if (newEmail != null) {
//
//                                    user.updateEmail(newEmail).addOnSuccessListener {
//                                        val usersCollection = FirebaseFirestore.getInstance()
//                                            .collection(UsersConstants.USERS_COLLECTION)
//                                        usersCollection
//                                            .whereEqualTo(UsersConstants.EMAIL, previousEmail)
//                                            .get()
//                                            .addOnSuccessListener { userDocuments ->
//                                                if (userDocuments.size() > 0) {
//                                                    val userDocument = userDocuments.documents[0]
//                                                    val userReference = userDocument.reference
//                                                    userReference.update(
//                                                        mapOf(
//                                                            UsersConstants.EMAIL to newEmail
//                                                        )
//                                                    ).addOnSuccessListener {
//                                                        Log.d(TAG, "cambio mail ok")
//                                                        listener?.onFinishClickDialog(true) //implementar MMVVM.
//                                                        //quiza descartar, quizá  usar by activityViewModels
//                                                    }
//                                                        .addOnFailureListener {
//                                                            Log.d(TAG, "error cambio mail")
//                                                        }
//                                                } else {
//                                                    Log.d(TAG, "User not found")
//
//                                                }
//                                            }
//                                            .addOnFailureListener { e ->
//                                                Log.d(TAG, "User query or update failed $e")
//                                            }
//                                        //  binding.editEmail.setText(newEmail)
//
//                                        this.dismiss()
//
//                                        Log.d(
//                                            TAG,
//                                            "Actualizado"
//                                        )
//                                    }.addOnFailureListener {
//                                        Log.d(
//                                            TAG,
//                                            "update del mail fallo"
//                                        )
//                                    }
//                                        .addOnCanceledListener {
//                                            Log.d(
//                                                TAG,
//                                                "cancelado"
//                                            )
//                                        }
//                                }
//                            } else {
//                                Log.d(
//                                    TAG,
//                                    "La reautenticación falló"
//                                )
//                                binding.textInputLayoutPassword.error = "Contraseña incorrecta"
//                            }
//                        }
//                } catch (e: IllegalArgumentException) {
//                    Log.e(
//                        TAG,
//                        "IllegalArgumentException: ${e.message}"
//                    )
//                    // Handle the case where the password is empty or null
//                    binding.textInputLayoutPassword.error = "Contraseña inválida"
//                }
//            } else {
//                // Handle the case when the password is empty
//                binding.textInputLayoutPassword.error = "La contraseña no puede estar vacía"
//            }
//        }
    }

    override fun setDialogClickListener(listener: DialogClickListener) {
        this.listener = listener
    }


}