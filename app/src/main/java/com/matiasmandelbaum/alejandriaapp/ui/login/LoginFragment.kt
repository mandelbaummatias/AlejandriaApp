package com.matiasmandelbaum.alejandriaapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.matiasmandelbaum.alejandriaapp.R

class LoginFragment : Fragment() {

    private lateinit var passwordRecovery: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        passwordRecovery = view.findViewById(R.id.textViewRecuperarCuenta)
        passwordRecovery.setOnClickListener {
            showPasswordRecovery()
        }
        return view
    }

    private fun showPasswordRecovery() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.fragment_password_recovery, null)
        val btnPasswordRecovery = view.findViewById<Button>(R.id.btnPasswordRecovery)
        val email = view.findViewById<TextInputEditText>(R.id.passwordRecoveryEmail)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        btnPasswordRecovery.setOnClickListener {
            val emailText = email.text.toString()

            if (emailText.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailText).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        bottomSheetDialog.dismiss()
                        Toast.makeText(requireContext(), "Se envió un enlace para restablecer tu contraseña", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al enviar el correo de recuperación", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
