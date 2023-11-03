import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.domain.model.reservation.Reservation
import com.matiasmandelbaum.alejandriaapp.domain.repository.ReservationsRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReservationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    ReservationsRepository {

    private val db = firestore
    private val reservationsCollection = db.collection("reservas_libros")

    override suspend fun createReservation(reservation: Reservation): Boolean {
        return try {
            reservationsCollection.add(reservation).await()
            true // reserva creada
        } catch (e: Exception) {
            Log.e(TAG, "error al crear la reserva: ${e.message}")
            false // error al crear la reserva
        }
    }

    companion object {
        private const val TAG = "ReservationsRepositoryImpl"
    }
}
