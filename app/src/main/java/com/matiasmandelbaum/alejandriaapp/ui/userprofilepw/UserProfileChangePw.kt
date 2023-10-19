package com.matiasmandelbaum.alejandriaapp.ui.userprofilepw

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.matiasmandelbaum.alejandriaapp.R

class ChangePasswordFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val changePasswordButton = view.findViewById<Button>(R.id.confirmNewPwButton)

        // al apretar el botón hay que actualizar la contraseña
        changePasswordButton.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val user = FirebaseAuth.getInstance().currentUser
        val currentPassword = view?.findViewById<EditText>(R.id.actualPasswordField)?.text.toString()
        val newPassword = view?.findViewById<EditText>(R.id.passwordConfirmation)?.text.toString()

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Reautenticación exitosa

                        // Actualizar la contraseña del usuario
                        if (currentPassword.equals(newPassword)) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        // Actualización de contraseña exitosa
                                        Toast.makeText(
                                            requireContext(),
                                            "Contraseña actualizada",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        // Si falla la actualización de la contraseña...
                                        Toast.makeText(
                                            requireContext(),
                                            "La contraseña no fue actualizada. (${updateTask.exception?.message})",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            // Si falla la reautenticación...
                            Toast.makeText(
                                requireContext(),
                                "La reautenticación falló: ${reauthTask.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        } else {
            // El usuario es nulo, no autenticado
            Toast.makeText(
                requireContext(),
                "El usuario no está autenticado",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
