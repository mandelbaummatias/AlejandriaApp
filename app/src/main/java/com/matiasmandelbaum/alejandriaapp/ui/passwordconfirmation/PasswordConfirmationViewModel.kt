package com.matiasmandelbaum.alejandriaapp.ui.passwordconfirmation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.domain.usecase.ChangeUserEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordConfirmationViewModel @Inject constructor(private val changeUserEmailUseCase: ChangeUserEmailUseCase) :
    ViewModel() {
    private val _onChangeUserEmailSuccess = MutableLiveData<Result<Unit>>()
    val onChangeUserEmailSuccess: LiveData<Result<Unit>> = _onChangeUserEmailSuccess

    fun changeUserEmail(newEmail: String, previousEmail: String, pass: String) {
        _onChangeUserEmailSuccess.value = Result.Loading
        viewModelScope.launch {
            val result = changeUserEmailUseCase(newEmail, previousEmail, pass)
            if (result is Result.Success) {
                changeUserEmailInReserves(newEmail, previousEmail)
            }
            _onChangeUserEmailSuccess.postValue(result)
        }
    }

    private fun changeUserEmailInReserves(newEmail: String,oldEmail: String) { //refactor
        val db = FirebaseFirestore.getInstance()
        val reservesCollection = db.collection("reservas_libros")

        reservesCollection.whereEqualTo("mail_usuario", oldEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val reserveDocument = document.reference
                        reserveDocument.update("mail_usuario", newEmail)
                            .addOnSuccessListener {
                                Log.d(TAG, "Actualización exitosa")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error al actualizar")
                            }
                    }
                } else {
                    Log.d(TAG, "No hay reservas para actualizar.")
                }
            }
    }

}