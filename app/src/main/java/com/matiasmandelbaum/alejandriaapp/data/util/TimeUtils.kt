import java.sql.Timestamp

object TimeUtils {
    fun longToTimestamp(tiempoEnMilis: Long): Timestamp {
        return Timestamp(tiempoEnMilis)
    }
}