package com.matiasmandelbaum.alejandriaapp.common.bindingadapter

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter("localDateTextInput")
fun setLocalDateTextInput(view: TextInputEditText, localDate: LocalDate?) {
    localDate?.let {
        val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        view.setText(it.format(format))
    } ?: view.setText("")
}