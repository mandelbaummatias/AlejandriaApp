package com.matiasmandelbaum.alejandriaapp.domain.model.reservation

import com.google.firebase.Timestamp
import java.util.Date

data class Reservation (
    val estado : String,
    val fecha_fin : String,
    val fecha_inicio : String,
    val fecha_reserva : Timestamp,
    val isbn_13 : String,
    val mail_usuario : String
)