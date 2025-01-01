package dataLayer.model

import com.raedghazal.kotlinx_datetime_ext.LocalDateTimeFormatter
import com.raedghazal.kotlinx_datetime_ext.Locale
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import modules.utils.timeOnly
enum class ReservationStatus {
    Created,
    Confirmed,
    Cancelled,
    NoShow,
    CheckIn,
}

@Serializable
data class Reservation(
    val id: Int,
    val seatPlan: List<SeatPlan>,
    val status: ReservationStatus,
    val fromDateTime: LocalDateTime,
    val toDateTime: LocalDateTime,
    val personCount: Int,
    val createdBy: String,
    val email: String,
    val tel: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val note: String,
    val company: String,
    val cleaningTimeMinute: Int,
    val childCount: Int,
    val useStroller: Int,
    val userId: Int,
    val createAt: String,
    val updateAt: String
) {
    fun getFormattedDateTimeRange(): String {
        val dateFormatter = LocalDateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.default())
        return "${dateFormatter.format(fromDateTime)} ${fromDateTime.timeOnly()} - ${toDateTime.timeOnly()}"
    }
}

@Serializable
data class SeatPlan(
    val id: Int,
    val tableId: Int,
    val seatCount: Int
)

@Serializable
class ReservationReadyDTO(val code: Int) {
    fun isReady(): Boolean {
        return code == 200
    }
}